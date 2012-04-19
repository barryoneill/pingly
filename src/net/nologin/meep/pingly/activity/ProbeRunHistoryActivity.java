package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeListCursorAdapter;
import net.nologin.meep.pingly.adapter.ProbeRunHistoryCursorAdapter;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.service.ProbeRunnerInteractiveService;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import static net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.ACTION_UPDATE;
import static net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.EXTRA_PROBE_RUN_ID;

public class ProbeRunHistoryActivity extends BasePinglyActivity {

	ProbeRunHistoryCursorAdapter listAdapter;

	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.probe_run_history);

		Probe probe = loadProbeParamIfPresent();
		Long probeId = probe == null ? null : probe.id;

		Cursor runHistoryCursor = probeRunDAO.queryForProbeRunHistoryCursorAdapter(probeId);
		listAdapter = new ProbeRunHistoryCursorAdapter(this, runHistoryCursor);
		ListView lv = (ListView) findViewById(R.id.probeRunList);
		lv.setAdapter(listAdapter);
		registerForContextMenu(lv);

		View empty = findViewById(R.id.emptyListElem);
		lv.setEmptyView(empty);

		startManagingCursor(runHistoryCursor);

	}


}

