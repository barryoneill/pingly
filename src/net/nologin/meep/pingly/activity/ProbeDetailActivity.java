package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.widget.Spinner;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import net.nologin.meep.pingly.adapter.ProbeTypeAdapter;
import net.nologin.meep.pingly.model.Probe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import net.nologin.meep.pingly.model.ProbeType;

public class ProbeDetailActivity extends BasePinglyActivity {
	
	private EditText probeName;
	private EditText probeDesc;
	private EditText probeURL;
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
		probeURL = (EditText) findViewById(R.id.probe_detail_url);
        probeType = (Spinner) findViewById(R.id.probe_detail_type);
        
		butSave = (Button) findViewById(R.id.but_newProbe_save);
		butCancel = (Button) findViewById(R.id.but_newProbe_cancel);
				        
		currentprobe = loadProbeParamIfPresent();
		
		if(currentprobe == null){
			Log.d(LOG_TAG, "Preparing form for new probe");
			currentprobe = new Probe();
		}
	
		// init the text fields from our new, or existing probe
        probeName.setText(currentprobe.name);
        probeDesc.setText(currentprobe.desc);
        probeURL.setText(currentprobe.url);

        // attach contents
        ProbeTypeAdapter typeAdapter = new ProbeTypeAdapter(this);
        probeType.setAdapter(typeAdapter);
        probeType.setSelection(typeAdapter.getItemPosition(currentprobe.type));


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
				String url = probeURL.getText().toString().trim();
				ProbeType type = (ProbeType)probeType.getSelectedItem();
                
				// TODO: i18n strings
				if(StringUtils.isBlank(name)) {
                    probeName.setError("Please supply a name.");
					return;
				}
				
				Probe duplicate = probeDataHelper.findProbeByName(name);
				if(duplicate != null && duplicate.id != currentprobe.id) {
                    probeName.setError("That name is already in use by another probe");
					return;
				}
				
				currentprobe.name = name;
				currentprobe.desc = desc;
				currentprobe.url = url;
				currentprobe.type = type;

				Log.d(LOG_TAG, "Saving probe: " + currentprobe);
				probeDataHelper.saveProbe(currentprobe);
				
				goToProbeList(v);
			}
		});
		
		
   }   


}
 	