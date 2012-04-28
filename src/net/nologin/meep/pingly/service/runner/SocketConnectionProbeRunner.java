package net.nologin.meep.pingly.service.runner;

import android.content.Context;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketConnectionProbeRunner extends ProbeRunner {

	public SocketConnectionProbeRunner(ProbeRun probeRun){
		super(probeRun);
	}

	public void doRun(Context ctx) throws ProbeRunCancelledException {

		SocketConnectionProbe probe = (SocketConnectionProbe)getProbe(); // if everything is configured properly

		if (StringUtils.isBlank(probe.host)) {
			notifyFinishedWithFailure("No URL specified");
			return;
		}

		checkCancelled();

		SocketChannel sc = null;
		try {

			notifyUpdate("Making socket request to: \n" + probe.host + "   port " + probe.port);

			sc = SocketChannel.open();
			sc.configureBlocking(false);

			sc.connect(new InetSocketAddress(probe.host, probe.port));

			int secondsWaited=1;
			while (!sc.finishConnect()) {
				if(secondsWaited >= 5){ // TODO: configure timeout
					notifyFinishedWithFailure("Timeout, connection to '" + probe.host + "' timed out");
					sc.close();
					return;
				}
				checkCancelled();
				notifyUpdate("Waiting on connect.. " + secondsWaited++);
				Thread.sleep(1000);
			}

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();


			notifyFinishedWithSuccess("Connection to '" + probe.host + "' was successful");

		}
		catch(InterruptedException e){
			notifyFinishedWithFailure("Connection interruped");
		}
		catch (IOException e){
			notifyFinishedWithFailure("IO Error: " + e.getMessage());
		}
		finally {
			if(sc != null && sc.isConnected()){
				try{
					sc.close();  // cleanup
				}
				catch(IOException ignored){}
			}
		}

	}

	@Override
	protected boolean requiresActiveNetConnection() {
		return true;
	}


}
