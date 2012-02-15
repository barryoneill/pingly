package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.PinglyTask;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class TaskRunnerActivity extends BasePinglyActivity {

	private TextView taskName;
	private Button runAgainBut;
	private ScrollView taskOutputScroller;

	private PinglyTask currentTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_run_log);

		// parameter must be present
		currentTask = loadTaskParamIfPresent();

		Log.d(LOG_TAG, "Running task " + currentTask);

		// load refs
		taskName = (TextView) findViewById(R.id.text_newTask_name);
		runAgainBut = (Button) findViewById(R.id.but_task_runAgain);
		taskOutputScroller = (ScrollView) findViewById(R.id.task_output_scroller);

		// init view
		taskName.setText(currentTask.name);

		// button is initially disabled
		runAgainBut.setEnabled(false);

	}

}
