package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import net.nologin.meep.pingly.model.PinglyTask;

import android.os.Bundle;
import android.util.Log;
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
	
	private PinglyTask currentTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_details);

		// load refs
		taskName = (EditText) findViewById(R.id.text_newTask_name);
		taskDesc = (EditText) findViewById(R.id.text_newTask_desc);
		taskURL = (EditText) findViewById(R.id.text_newTask_url);		
		butSave = (Button) findViewById(R.id.but_newTask_save);
		butCancel = (Button) findViewById(R.id.but_newTask_cancel);
				
		currentTask = loadTaskParamIfPresent();
		
		if(currentTask == null){
			Log.d(LOG_TAG, "Preparing form for new task");
			currentTask = new PinglyTask();	
		}
	
		// init the text fields from our new, or existing task
		taskName.setText(currentTask.name);
		taskDesc.setText(currentTask.desc);
		taskURL.setText(currentTask.url);

		// attach listeners				
		butCancel.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				finish();				
			}
		});
		
		butSave.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
								
				String name = taskName.getText().toString().trim();
				String desc = taskDesc.getText().toString().trim();
				String url = taskURL.getText().toString().trim();
				
				// TODO: i18n strings
				if(StringUtils.isBlank(name)){
					taskName.setError("Please supply a name.");
					return;
				}
				
				PinglyTask duplicate = taskDataHelper.findTaskByName(name);
				if(duplicate != null && duplicate.id != currentTask.id){
					taskName.setError("That name is already in use by another task");
					return;
				}
				
				currentTask.name = name;
				currentTask.desc = desc;
				currentTask.url = url;
				
				Log.d(LOG_TAG, "Saving task: " + currentTask);
				taskDataHelper.saveTask(currentTask);
				
				goToTaskList(v);	
			}
		});
		
		
   }   


  
}
 	