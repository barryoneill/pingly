package net.nologin.meep.pingly.service.runner;

import net.nologin.meep.pingly.model.probe.Probe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class PingProbeRunner extends ProbeRunner {

	public PingProbeRunner(Probe probe) {
		super(probe);
	}

	@Override
	protected boolean doRun() throws ProbeRunCancelledException {

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
		String host = "www.google.ie";
		int count = 5;
		int deadline = 5;

		try{
			// assumption - ping on path!
			String pingCmd = String.format("ping -c %d -w %d %s",count,deadline,host);

			publishUpdate("Pinging : " + host);
			publishUpdate("With count=" + count + " and deadline=" + deadline + "s...\n");
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
				publishUpdate("\nPing was successful");
				return RUN_SUCCESS;
			} else {
				publishUpdate("Ping failed.");
				return RUN_FAILED;
			}

		}
		catch (InterruptedException e) {
			publishUpdate("Ping Interrupted:" + e.getMessage());
			return RUN_FAILED;
		}
		catch(UnknownHostException e){
			publishUpdate("Unknown host error: " + e.getMessage());
			return RUN_FAILED;
		}
		catch(IOException e){
			publishUpdate("IO Error:" + e.getMessage());
			return RUN_FAILED;
		}

	}

	private void publishStream(InputStream ins) throws ProbeRunCancelledException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
		String line;
		while ((line = br.readLine()) != null) {
			checkCancelled();
			publishUpdate(line);
		}
		br.close();
	}
}
