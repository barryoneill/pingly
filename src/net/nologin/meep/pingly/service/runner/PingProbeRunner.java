package net.nologin.meep.pingly.service.runner;

import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.probe.PingProbe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class PingProbeRunner extends ProbeRunner {

	public PingProbeRunner(ProbeRun probeRun) {
		super(probeRun);
	}

	@Override
	protected void doRun() throws ProbeRunCancelledException {

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
			// assumption - ping on path!
			String pingCmd = String.format("ping -c %d -w %d %s",count,deadline,host);

			notifyUpdate("Pinging : " + host);
			notifyUpdate("With count=" + count + " and deadline=" + deadline + "s...\n");
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
				notifyFinishedWithSuccess("Ping response OK");
			} else {
				notifyFinishedWithFailure("Ping failed.");
			}

		}
		catch (InterruptedException e) {
			notifyFinishedWithFailure("Ping Interrupted:" + e.getMessage());
		}
		catch(UnknownHostException e){
			notifyFinishedWithFailure("Unknown host error: " + e.getMessage());
		}
		catch(IOException e){
			notifyFinishedWithFailure("IO Error:" + e.getMessage());
		}

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
