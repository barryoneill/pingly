/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.service.runner;

import android.content.Context;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.probe.PingProbe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * ProbeRunner impl for PingProbe objects.  Performs the actual ping and determines success/failure.
 *
 * May not run on all devices as it requires a ping binary:
 *
 * Assumption 1: ping binary is on the vm's PATH,
 * Assumption 2: ping binary supports -c and -w arguments
 *
 * Perhaps in the future a check can be done on application start,
 * and hide the PingProbe from the user altogether.  I'll wait to
 * see what people report back with first. (see notes in doRun())
 *
 */
public class PingProbeRunner extends ProbeRunner {

	private static final String PING_CMD_FMT = "ping -c %d -w %d %s";

	public PingProbeRunner(ProbeRun probeRun) {
		super(probeRun);
	}

	@Override
	protected void doRun(Context ctx) throws ProbeRunCancelledException {

		/**
		 * Since java 5 there is an API method for performing ping, eg:
		 * boolean reachable - InetAddress.getByName(host).isReachable(4000))
		 *
		 * However, I'm not using it because it doesn't provide a lot of info,
		 * just a success flag, and also falls back to tcp/7 (echo) if ICMP fails
		 * which may not be what the user wants.
		 *
		 * So, unless this ping(exec) causes headaches for users, I'll go with it for now
		 *
		 * Note, ping on the emulator is unlikely to work, especially for non-local hosts
		 * http://developer.android.com/guide/developing/devices/emulator.html#emulatornetworking
		 */
		PingProbe pingProbe = (PingProbe)getProbe(); // if everything is configured properly
		String host = pingProbe.host;
		int count = pingProbe.packetCount;
		int deadline = pingProbe.deadline;

		try{

			String pingCmd = String.format(PING_CMD_FMT,count,deadline,host);

			notifyUpdate(ctx.getString(R.string.probe_run_PING_startmsg, host, count, deadline));

			Process proc =  Runtime.getRuntime().exec(pingCmd);

			// make sure the output from both gets written
			// alternatively use ProcessBuilder and redirectErrorStream
			// helpful - http://stackoverflow.com/questions/2150723/process-waitfor-threads-and-inputstreams
			publishStream(proc.getErrorStream());
			publishStream(proc.getInputStream());

			checkCancelled();

			// block for ping finish
			int procStatus = proc.waitFor();
			checkCancelled();
			if(procStatus == 0) {
				notifyFinishedWithSuccess(ctx.getString(R.string.probe_run_PING_successmsg));
			} else {
				notifyFinishedWithFailure(ctx.getString(R.string.probe_run_PING_failuremsg));
			}

		}
		catch(UnknownHostException e){
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_PING_err_unknownhost,e.getMessage()));
		}
		catch (InterruptedException e) {
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_general_err_interrupted,e.getMessage()));
		}
		catch(IOException e){
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_general_err_io,e.getMessage()));
		}

	}

	@Override
	protected boolean requiresActiveNetConnection() {
		return true;
	}

	private void publishStream(InputStream ins) throws ProbeRunCancelledException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		String line;
		while ((line = br.readLine()) != null) {
			checkCancelled();
			notifyUpdate(line);
		}
		br.close();
	}
}
