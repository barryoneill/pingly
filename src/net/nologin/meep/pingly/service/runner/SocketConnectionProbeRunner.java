package net.nologin.meep.pingly.service.runner;

import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketConnectionProbeRunner extends ProbeRunner {

	public SocketConnectionProbeRunner(Probe probe){
		super(probe);
	}

	public boolean doRun() throws ProbeRunCancelledException {

		SocketConnectionProbe probe = (SocketConnectionProbe)getProbe(); // if everything is configured properly

		if (StringUtils.isBlank(probe.host)) {
			publishUpdate("No URL specified");
			return RUN_FAILED;
		}

		checkCancelled();

		SocketChannel sc = null;
		try {

			publishUpdate("Making socket request to: \n" + probe.host + "   port " + probe.port);

			sc = SocketChannel.open();
			sc.configureBlocking(false);

			sc.connect(new InetSocketAddress(probe.host, probe.port));

			int secondsWaited=1;
			while (!sc.finishConnect()) {
				if(secondsWaited >= 5){ // TODO: configure timeout
					publishUpdate("Timeout, connection failed");
					sc.close();
					return RUN_FAILED;
				}
				checkCancelled();
				publishUpdate("Waiting on connect.. " + secondsWaited++);
				Thread.sleep(1000);
			}

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();


			publishUpdate("Connection successful");
			return RUN_SUCCESS;

		}
		catch(InterruptedException e){ return RUN_FAILED; }
		catch (IOException e){
			publishUpdate("IO Error: " + e.getMessage());
			return RUN_FAILED;
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


}
