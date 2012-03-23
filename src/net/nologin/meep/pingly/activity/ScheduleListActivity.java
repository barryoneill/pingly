package net.nologin.meep.pingly.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.PinglyCursorProbeAdapter;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.view.PinglyBasePrefView;

public class ScheduleListActivity extends BasePinglyActivity {

    private PinglyCursorProbeAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list);

        Cursor allProbesCursor = probeDAO.findAllProbes();
        listAdapter = new PinglyCursorProbeAdapter(this,allProbesCursor);
        ListView lv = (ListView) findViewById(R.id.schdeuleList);
        lv.setAdapter(listAdapter);
        registerForContextMenu(lv);

        View empty = findViewById(R.id.emptyListElem);


        lv.setEmptyView(empty);

    }



}