package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.widget.Spinner;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.StringUtils;
import net.nologin.meep.pingly.adapter.ProbeTypeAdapter;
import net.nologin.meep.pingly.model.probe.Probe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProbeDetailActivity extends BasePinglyActivity {
	
	private EditText probeName;
	private EditText probeDesc;
    private Spinner probeType;
	private Button butSave;
	private Button butCancel;
	
	private Probe currentprobe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.probe_detail);

		// load refs
		probeName = (EditText) findViewById(R.id.probe_detail_name);
		probeDesc = (EditText) findViewById(R.id.probe_detail_desc);
        probeType = (Spinner) findViewById(R.id.probe_detail_type);
        
		butSave = (Button) findViewById(R.id.but_newProbe_save);
		butCancel = (Button) findViewById(R.id.but_newProbe_cancel);
				        
		currentprobe = loadProbeParamIfPresent();
		
		if(currentprobe == null){
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
				String typeKey = (String)probeType.getSelectedItem();
                
				// TODO: i18n strings
				if(StringUtils.isBlank(name)) {
                    probeName.setError("Please supply a name.");
					return;
				}
				
				Probe duplicate = probeDAO.findProbeByName(name);
				if(duplicate != null && duplicate.id != currentprobe.id) {
                    probeName.setError("That name is already in use by another probe");
					return;
				}
				
				currentprobe.name = name;
				currentprobe.desc = desc;
				// TODO: type changing - currentprobe.type = type;

				Log.d(LOG_TAG, "Saving probe: " + currentprobe);
				probeDAO.saveProbe(currentprobe);

				goToProbeList(v);
			}
		});
		
		
   }   


}
 	