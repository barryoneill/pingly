package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.widget.*;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.model.probe.PingProbe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.StringUtils;
import net.nologin.meep.pingly.adapter.ProbeTypeAdapter;
import net.nologin.meep.pingly.model.probe.Probe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


public class ProbeDetailActivity extends BasePinglyActivity {

	private EditText probeName;
	private EditText probeDesc;
	private LinearLayout typeSpecificContainer;

	private ProbeSpecificDetailHelper currentManager;
	private Probe currentprobe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.probe_detail);

		// load refs
		probeName = (EditText) findViewById(R.id.probe_detail_name);
		probeDesc = (EditText) findViewById(R.id.probe_detail_desc);
		Spinner probeType = (Spinner) findViewById(R.id.probe_detail_type);
		typeSpecificContainer = (LinearLayout) findViewById(R.id.probe_detail_typespecific_container);

		Button butSave = (Button) findViewById(R.id.but_newProbe_save);
		Button butCancel = (Button) findViewById(R.id.but_newProbe_cancel);

		currentprobe = loadProbeParamIfPresent();

		if (currentprobe == null) {
			Log.d(LOG_TAG, "Preparing form for new probe");
			currentprobe = Probe.getInstance(SocketConnectionProbe.TYPE_KEY); // TODO: revisit
		}


		// init the text fields from our new, or existing probe
		probeName.setText(currentprobe.name);
		probeDesc.setText(currentprobe.desc);

		// attach contents
		ProbeTypeAdapter typeAdapter = new ProbeTypeAdapter(this);
		probeType.setAdapter(typeAdapter);
		probeType.setSelection(typeAdapter.getItemPosition(currentprobe.getTypeKey()));


		// attach listeners				
		butCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		butSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String name = probeName.getText().toString().trim();
				String desc = probeDesc.getText().toString().trim();

				// TODO: i18n strings
				if (StringUtils.isBlank(name)) {
					probeName.setError("Please supply a name.");
					return;
				}

				Probe duplicate = probeDAO.findProbeByName(name);
				if (duplicate != null && duplicate.id != currentprobe.id) {
					probeName.setError("That name is already in use by another probe");
					return;
				}

				currentprobe.name = name;
				currentprobe.desc = desc;

				// let the current type's manager do any processing and validation before saving
				if (!getManagerForType(currentprobe.getTypeKey()).beforeProbeSave()) {
					return;
				}

				Log.d(LOG_TAG, "Saving probe: " + currentprobe);
				probeDAO.saveProbe(currentprobe);

				goToProbeList(v);
			}
		});

		probeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				setupForProbeType((String) adapterView.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	private void setupForProbeType(String newProbeTypeKey) {

		// if there's a change, update the object with the desired type
		if (!currentprobe.isType(newProbeTypeKey)) {
			Log.e(LOG_TAG, "Converting current probe from " + currentprobe.getTypeKey() + " to " + newProbeTypeKey);

			// replace the existing probe with the new probe type, values copied
			currentprobe = Probe.getInstance(newProbeTypeKey, currentprobe);
		}

		// and get the manager that handles probe-type specific work
		currentManager = getManagerForType(newProbeTypeKey);
		if (currentManager == null) {
			Log.e(LOG_TAG, "No manager configured for probe type " + newProbeTypeKey);
			return;
		}

		// clear out what's currently there
		typeSpecificContainer.removeAllViews();

		Log.d(LOG_TAG, "Inflating layout " + Integer.toHexString(currentManager.getLayoutId()));
		View container = getLayoutInflater().inflate(currentManager.getLayoutId(), typeSpecificContainer);

		// get the manager to setup the view we've just inflated
		currentManager.afterLayoutInflation();

	}

	private ProbeSpecificDetailHelper getManagerForType(String probeTypeKey) {
		if (probeTypeKey == null) {
			return null;
		}
		if (PingProbe.TYPE_KEY.equals(probeTypeKey)) {
			return new PingDetailHelper();
		}
		if (HTTPResponseProbe.TYPE_KEY.equals(probeTypeKey)) {
			return new HTTPResponseDetailHelper();
		}
		if (SocketConnectionProbe.TYPE_KEY.equals(probeTypeKey)){
			return new SocketConnectionDetailHelper();
		}
		Log.e(LOG_TAG, "No manager configured for key " + probeTypeKey);
		return null;
	}

	// ======================================================================================
	class PingDetailHelper implements ProbeSpecificDetailHelper {

		@Override
		public int getLayoutId() {
			return R.layout.probe_detail_ping;
		}

		@Override
		public void afterLayoutInflation() {
			Log.d(LOG_TAG, "afterLayoutInflation for PING");

			PingProbe p = (PingProbe)currentprobe;
			getHostField().setText(p.host);
			getCountField().setText(Integer.toString(p.packetCount));
			getDeadlineField().setText(Integer.toString(p.deadline));
		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave PING");

			EditText host = getHostField();

			if (StringUtils.isBlank(host.getText().toString())) {
				host.setError("Please specify a host");
				return false;
			}

			PingProbe p = (PingProbe)currentprobe;
			p.host = getHostField().getText().toString();
			p.packetCount = Integer.parseInt(getCountField().getText().toString());
			p.deadline = Integer.parseInt(getDeadlineField().getText().toString());
			return true;
		}

		private EditText getHostField(){
			return (EditText)findViewById(R.id.probe_detail_ping_host);
		}
		private EditText getCountField(){
			return (EditText)findViewById(R.id.probe_detail_ping_count);
		}
		private EditText getDeadlineField(){
			return (EditText)findViewById(R.id.probe_detail_ping_deadline);
		}
	}

	// ======================================================================================
	class HTTPResponseDetailHelper implements ProbeSpecificDetailHelper {

		@Override
		public int getLayoutId() {
			return R.layout.probe_detail_httpresponse;
		}

		@Override
		public void afterLayoutInflation() {
			Log.d(LOG_TAG, "afterLayoutInflation for HTTP");
		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave for HTTP");

			EditText url = getURLField();
			if (StringUtils.isBlank(url.getText().toString())) {
				url.setError("Please specify a URL");
				return false;
			}

			return true;
		}

		private EditText getURLField(){
			return (EditText)findViewById(R.id.probe_detail_httpresponse_url);
		}
	}
	// ======================================================================================
	class SocketConnectionDetailHelper implements ProbeSpecificDetailHelper {

		@Override
		public int getLayoutId() {
			return R.layout.probe_detail_socketconnection;
		}

		@Override
		public void afterLayoutInflation() {
			Log.d(LOG_TAG, "afterLayoutInflation for Socket Connection");
		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave for Socket Connection");

			EditText host = getHostField();
			if (StringUtils.isBlank(host.getText().toString())) {
				host.setError("Please specify a host");
				return false;
			}

			EditText port = getPortField();
			if (StringUtils.isBlank(port.getText().toString())) {
				host.setError("Please specify a port");
				return false;
			}


			return true;
		}

		private EditText getHostField(){
			return (EditText)findViewById(R.id.probe_detail_socketconnection_host);
		}
		private EditText getPortField(){
			return (EditText)findViewById(R.id.probe_detail_socketconnection_port);
		}
	}

	// ======================================================================================

	/**
	 * Used meaningfully group together probe-specific activity logic
	 */
	private interface ProbeSpecificDetailHelper {
		/**
		 * the layout resource to be inflated with the probe's type specific fields
		 */
		public int getLayoutId();

		/**
		 * the code that does any population/setup of the inflated view
		 */
		public void afterLayoutInflation();

		/**
		 * the code to prepare the probe object before save() is called
		 */
		public boolean beforeProbeSave();
	}
}
 	