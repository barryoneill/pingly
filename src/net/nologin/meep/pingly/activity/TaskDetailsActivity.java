package net.nologin.meep.pingly.activity;


import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import net.nologin.meep.pingly.model.PinglyTaskDataHelper;
import net.nologin.meep.pingly.model.PinglyTask;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TaskDetailsActivity extends BasePinglyActivity {
		
	public static final String PARAMETER_TASK_ID = "param_task";
	
	private EditText taskName;
	private EditText taskDesc;
	private EditText taskURL;	
	private Button butSave;
	private Button butCancel;
	
	private PinglyTaskDataHelper dataHelper;
	
	private PinglyTask currentTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);

		// load data ref
		dataHelper = new PinglyTaskDataHelper(this);
		
		// load refs
		taskName = (EditText) findViewById(R.id.text_newTask_name);
		taskDesc = (EditText) findViewById(R.id.text_newTask_desc);
		taskURL = (EditText) findViewById(R.id.text_newTask_url);		
		butSave = (Button) findViewById(R.id.but_newTask_save);
		butCancel = (Button) findViewById(R.id.but_newTask_cancel);
				
		
		// populate fields if param set	
		Bundle b = getIntent().getExtras();		
		if(b != null && b.containsKey(PARAMETER_TASK_ID)){
			Long taskId = b.getLong(PARAMETER_TASK_ID, -1);
			if(taskId >= 0){
				Log.d(PinglyConstants.LOG_TAG, "Will be loading task ID " + taskId);
				
				currentTask = dataHelper.findTaskById(taskId);
				Log.d(PinglyConstants.LOG_TAG, "Got task: " + currentTask);
				
				taskName.setText(currentTask.name);
				taskDesc.setText(currentTask.desc);
				taskURL.setText(currentTask.url);
			}
		}
		
		// if currentTask is null, we assume a new task (handles case where ID isn't found)
		if(currentTask == null){
			Log.d(PinglyConstants.LOG_TAG, "Preparing form for new task");
			currentTask = new PinglyTask();
		}
		
		
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
				
				PinglyTask duplicate = dataHelper.findTaskByName(name);
				if(duplicate != null && duplicate.id != currentTask.id){
					taskName.setError("That name is already in use by another task");
					return;
				}
				
				currentTask.name = name;
				currentTask.desc = desc;
				currentTask.url = url;
				
				Log.d(PinglyConstants.LOG_TAG, "Saving task: " + currentTask);
				dataHelper.saveTask(currentTask);
				
				goToTaskList(v);	
			}
		});
		
		
   }   

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (dataHelper != null) {
	        dataHelper.close();
	    }
	}
  
}
 	