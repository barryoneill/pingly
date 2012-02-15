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

public class TaskRunnerActivity extends BasePinglyActivity {
	
	private EditText taskName;
	private EditText taskDesc;
	private EditText taskURL;		
		
	private PinglyTask currentTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_details);

		// load refs
		taskName = (EditText) findViewById(R.id.text_newTask_name);
		taskDesc = (EditText) findViewById(R.id.text_newTask_desc);
		taskURL = (EditText) findViewById(R.id.text_newTask_url);
				
		
		// if currentTask is null, we assume a new task (handles case where ID isn't found)
		if(currentTask == null){
			Log.d(PinglyConstants.LOG_TAG, "Preparing form for new task");
			currentTask = new PinglyTask();
		}
		
	
		
   }   

}
 	