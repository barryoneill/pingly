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
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketConnectionProbeRunner extends ProbeRunner {

	public SocketConnectionProbeRunner(ProbeRun probeRun) {
		super(probeRun);
	}

	public void doRun(Context ctx) throws ProbeRunCancelledException {

		SocketConnectionProbe probe = (SocketConnectionProbe) getProbe(); // if everything is configured properly

		if (StringUtils.isBlank(probe.host)) {
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_SOCK_CONN_err_no_url));
			return;
		}

		checkCancelled();

		SocketChannel sc = null;
		try {

			// 'Socket connection to host 'host', port 80'
			notifyUpdate(ctx.getString(R.string.probe_run_SOCK_CONN_startmsg, probe.host, probe.port));

			sc = SocketChannel.open();
			sc.configureBlocking(false);

			sc.connect(new InetSocketAddress(probe.host, probe.port));

			int secondsWaited = 1;
			while (!sc.finishConnect()) {
				if (secondsWaited >= 5) { // TODO: configure timeout
					// 'Connection to ${host} port ${port} timed out'
					String msg = ctx.getString(R.string.probe_run_SOCK_CONN_timeout_failuremsg, probe.host, probe.port);
					notifyFinishedWithFailure(msg);
					sc.close();
					return;
				}
				checkCancelled();
				// "Waiting on connect.. ${count}"
				notifyUpdate(ctx.getString(R.string.probe_run_SOCK_CONN_wait_second, secondsWaited++));
				Thread.sleep(1000);
			}

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();


			notifyFinishedWithSuccess(ctx.getString(R.string.probe_run_SOCK_CONN_successmsg, probe.host, probe.port));

		}
		catch (InterruptedException e) {
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_general_err_interrupted, e.getMessage()));
		}
		catch (IOException e) {
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_general_err_io, e.getMessage()));
		}
		finally {
			if (sc != null && sc.isConnected()) {
				try {
					sc.close();  // cleanup
				} catch (IOException ignored) {
				}
			}
		}

	}

	@Override
	protected boolean requiresActiveNetConnection() {
		return true;
	}


}
