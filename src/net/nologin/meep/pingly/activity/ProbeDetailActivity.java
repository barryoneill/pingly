package net.nologin.meep.pingly.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeTypeAdapter;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.model.probe.PingProbe;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.NumberUtils;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.util.StringUtils;
import net.nologin.meep.pingly.util.ui.NumberRangeTextWatcher;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;


public class ProbeDetailActivity extends BasePinglyActivity {

	private EditText probeName;
	private EditText probeDesc;
	private LinearLayout typeSpecificContainer;

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

		Button butSave = (Button) findViewById(R.id.but_probeDetail_save);
		Button butSaveRun = (Button) findViewById(R.id.but_probeDetail_saveRun);
		Button butCancel = (Button) findViewById(R.id.but_probeDetail_cancel);

		currentprobe = getIntentExtraProbe();

		if (currentprobe == null) {
			Log.d(LOG_TAG, "Preparing form for new probe");
			currentprobe = Probe.getInstance(PingProbe.TYPE_KEY);
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
				if(saveProbe()){
					goToProbeList(v);
				}
			}
		});

		butSaveRun.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(saveProbe()){
					PinglyUtils.startActivityProbeRunner(ProbeDetailActivity.this,currentprobe);
				}
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

	private boolean saveProbe(){
		String name = probeName.getText().toString().trim();
		String desc = probeDesc.getText().toString().trim();

		probeName.setError(null); // clear any possible previous error

		if (StringUtils.isBlank(name)) {
			probeName.setError(getText(R.string.probe_validation_empty_name));
			return false;
		}

		Probe duplicate = probeDAO.findProbeByName(name);
		if (duplicate != null && duplicate.id != currentprobe.id) {
			probeName.setError(getString(R.string.probe_validation_duplicate_name));
			return false;
		}

		currentprobe.name = name;
		currentprobe.desc = desc;

		// let the current type's manager do any processing and validation before saving
		if (!getManagerForType(currentprobe.getTypeKey()).beforeProbeSave()) {
			return false;
		}

		int successMsgId = currentprobe.isNew() ? R.string.toast_probe_added : R.string.toast_probe_updated;

		Log.d(LOG_TAG, "Saving probe: " + currentprobe);
		currentprobe.id = probeDAO.saveProbe(currentprobe);

		PinglyUtils.showToast(this,successMsgId,currentprobe.name);

		return true;

	}

	private void setupForProbeType(String newProbeTypeKey) {

		// if there's a change, update the object with the desired type
		if (!currentprobe.isType(newProbeTypeKey)) {
			Log.e(LOG_TAG, "Converting current probe from " + currentprobe.getTypeKey() + " to " + newProbeTypeKey);

			// replace the existing probe with the new probe type, values copied
			currentprobe = Probe.getInstance(newProbeTypeKey, currentprobe);
		}

		// and get the manager that handles probe-type specific work
		ProbeSpecificDetailHelper currentMgr = getManagerForType(newProbeTypeKey);
		if (currentMgr == null) {
			Log.e(LOG_TAG, "No manager configured for probe type " + newProbeTypeKey);
			return;
		}

		// clear out what's currently there
		typeSpecificContainer.removeAllViews();

		Log.d(LOG_TAG, "Inflating layout " + Integer.toHexString(currentMgr.getLayoutId()));
		getLayoutInflater().inflate(currentMgr.getLayoutId(), typeSpecificContainer);

		// get the manager to setup the view we've just inflated
		currentMgr.afterLayoutInflation();

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
		if (SocketConnectionProbe.TYPE_KEY.equals(probeTypeKey)) {
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

			EditText hostTxt = findEditText(R.id.probe_detail_ping_host);
			EditText countTxt = findEditText(R.id.probe_detail_ping_count);
			EditText deadlineTxt = findEditText(R.id.probe_detail_ping_deadline);

			countTxt.addTextChangedListener(new NumberRangeTextWatcher(PingProbe.PACKET_COUNT_MIN, PingProbe.PACKET_COUNT_MAX));
			deadlineTxt.addTextChangedListener(new NumberRangeTextWatcher(PingProbe.DEADLINE_MIN, PingProbe.DEADLINE_MAX));

			PingProbe p = (PingProbe) currentprobe;
			p.packetCount = NumberUtils.checkRange(p.packetCount, PingProbe.PACKET_COUNT_MIN, PingProbe.PACKET_COUNT_MAX);
			p.deadline = NumberUtils.checkRange(p.deadline, PingProbe.DEADLINE_MIN, PingProbe.DEADLINE_MAX);

			hostTxt.setText(p.host);
			countTxt.setText(String.valueOf(p.packetCount));
			deadlineTxt.setText(String.valueOf(p.deadline));
		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave PING");

			EditText hostTxt = findEditText(R.id.probe_detail_ping_host);
			EditText countTxt = findEditText(R.id.probe_detail_ping_count);
			EditText deadlineTxt = findEditText(R.id.probe_detail_ping_deadline);

			if (StringUtils.isBlank(hostTxt.getText().toString())) {
				hostTxt.setError(getString(R.string.probe_validation_empty_host_ip));
				return false;
			}

			PingProbe p = (PingProbe) currentprobe;
			p.host = hostTxt.getText().toString();

			p.packetCount = StringUtils.getInt(countTxt.getText().toString(),
					PingProbe.PACKET_COUNT_MIN,
					PingProbe.PACKET_COUNT_MAX,
					PingProbe.PACKET_COUNT_DEFAULT);
			p.deadline = StringUtils.getInt(deadlineTxt.getText().toString(),
					PingProbe.DEADLINE_MIN,
					PingProbe.DEADLINE_MAX,
					PingProbe.DEADLINE_DEFAULT);
			return true;
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

			EditText urlTxt = findEditText(R.id.probe_detail_httpresponse_url);

			HTTPResponseProbe p = (HTTPResponseProbe) currentprobe;

			urlTxt.setText(p.url);

		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave for HTTP");

			EditText url = findEditText(R.id.probe_detail_httpresponse_url);
			if (StringUtils.isBlank(url.getText().toString())) {
				url.setError(getString(R.string.probe_validation_empty_url));
				return false;
			}

			HTTPResponseProbe p = (HTTPResponseProbe) currentprobe;
			p.url = url.getText().toString();

			return true;
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

			EditText hostTxt = findEditText(R.id.probe_detail_socketconnection_host);
			EditText port = findEditText(R.id.probe_detail_socketconnection_port);

			port.addTextChangedListener(
					new NumberRangeTextWatcher(SocketConnectionProbe.PORT_MIN, SocketConnectionProbe.PORT_MAX));

			SocketConnectionProbe p = (SocketConnectionProbe) currentprobe;
			p.port = NumberUtils.checkRange(p.port, SocketConnectionProbe.PORT_MIN, SocketConnectionProbe.PORT_MAX);

			hostTxt.setText(p.host);
			port.setText(String.valueOf(p.port));

		}

		@Override
		public boolean beforeProbeSave() {
			Log.d(LOG_TAG, "beforeProbeSave for Socket Connection");

			EditText host = findEditText(R.id.probe_detail_socketconnection_host);
			EditText port = findEditText(R.id.probe_detail_socketconnection_port);

			if (StringUtils.isBlank(host.getText().toString())) {
				host.setError(getString(R.string.probe_validation_empty_host_ip));
				return false;
			}

			if (StringUtils.isBlank(port.getText().toString())) {
				port.setError(getString(R.string.probe_validation_empty_port));
				return false;
			}


			SocketConnectionProbe p = (SocketConnectionProbe) currentprobe;
			p.host = host.getText().toString();

			p.port = StringUtils.getInt(port.getText().toString(),
					SocketConnectionProbe.PORT_MIN,
					SocketConnectionProbe.PORT_MAX,
					SocketConnectionProbe.PORT_DEFAULT
					);

			return true;
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
 	