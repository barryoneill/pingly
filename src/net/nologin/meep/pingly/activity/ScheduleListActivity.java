package net.nologin.meep.pingly.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeListCursorAdapter;
import net.nologin.meep.pingly.adapter.ScheduleListCursorAdapter;

public class ScheduleListActivity extends BasePinglyActivity {

    private ScheduleListCursorAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list);

        Cursor schedCursor = scheduleDAO.findAllScheduledItems();
        listAdapter = new ScheduleListCursorAdapter(this,schedCursor);
        ListView lv = (ListView) findViewById(R.id.scheduleList);
        lv.setAdapter(listAdapter);
        registerForContextMenu(lv);

        View empty = findViewById(R.id.emptyListElem);


        lv.setEmptyView(empty);

    }



}