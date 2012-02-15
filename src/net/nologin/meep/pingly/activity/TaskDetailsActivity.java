package net.nologin.meep.pingly.activity;


import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import net.nologin.meep.pingly.model.PinglyData;
import net.nologin.meep.pingly.model.PinglyTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TaskDetailsActivity extends BasePinglyActivity {
		
	private EditText taskName;
	private EditText taskDesc;
	private EditText taskURL;	
	private Button butSave;
	private Button butCancel;
	
	private PinglyData data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);

		// load data ref
		data = new PinglyData(this);
		
		// load refs
		taskName = (EditText) findViewById(R.id.text_newTask_name);
		taskDesc = (EditText) findViewById(R.id.text_newTask_desc);
		taskURL = (EditText) findViewById(R.id.text_newTask_url);		
		butSave = (Button) findViewById(R.id.but_newTask_save);
		butCancel = (Button) findViewById(R.id.but_newTask_cancel);
		
		// attach listeners
				
		butCancel.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				finish();				
			}
		});
		
		butSave.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
								
				String name = taskName.getText().toString();
				String desc = taskDesc.getText().toString();
				String url = taskURL.getText().toString();
				
				if(StringUtils.isBlank(name)){
					taskName.setError("Whoops, can't be blank");
					return;
				}
				
				PinglyTask task = new PinglyTask(name,desc,url);
				
				data.insert_task(task);
				
				goToTaskList(v);	
			}
		});
		
		
   }   

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (data != null) {
	        data.close();
	    }
	}
  
}
 	