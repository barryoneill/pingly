package net.nologin.meep.pingly.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.util.DBUtils;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Date;

import static net.nologin.meep.pingly.db.PinglyDataHelper.TBL_SCHEDULE;

public class ScheduleListCursorAdapter extends SimpleCursorAdapter {

	private LayoutInflater inflater;
	private static final int SCHEDULE_ITEM_LAYOUT = R.layout.schedule_list_item;
	private static final String FROM[] = {};
	private static final int TO[] = {};

	public ScheduleListCursorAdapter(Context context, Cursor c) {
		super(context, SCHEDULE_ITEM_LAYOUT, c, FROM, TO);
		this.inflater = LayoutInflater.from(context);
	}

	static class ViewHolder {
		TextView probeInfo;
		TextView startInfo;
        TextView repeatInfo;
		
		int colProbeFKIdx;
		int colStartTimeIdx;
		int colRepeatTypeIdx;
		int colRepeatValueIdx;

	}

	// http://stackoverflow.com/questions/3535074/getview-vs-bindview-in-a-custom-cursoradapter
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View newView = inflater.inflate(SCHEDULE_ITEM_LAYOUT, parent, false);

		ViewHolder holder = new ViewHolder();
		// view refs
		holder.probeInfo = (TextView) newView.findViewById(R.id.schedule_entry_probeinfo);
		holder.startInfo = (TextView) newView.findViewById(R.id.schedule_entry_startinfo);
        holder.repeatInfo = (TextView) newView.findViewById(R.id.schedule_entry_repeatinfo);

		// column indexes
		holder.colProbeFKIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_PROBE_FK);
		holder.colStartTimeIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_STARTTIME);
		holder.colRepeatTypeIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_REPEATTYPE_ID);
		holder.colRepeatValueIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_REPEAT_VALUE);

		newView.setTag(holder);

		// bindView will be called after newView, populate items there

		return newView;
	}

	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		long probeId = cursor.getLong(holder.colProbeFKIdx);
		Date startTime = DBUtils.fromGMTDateTimeString(cursor.getString(holder.colStartTimeIdx));
		ScheduleRepeatType repeatType = ScheduleRepeatType.fromId(cursor.getInt(holder.colRepeatTypeIdx));
		int repeatValue = cursor.getInt(holder.colRepeatValueIdx);

		// TODO: replace hardcoded text with i18n
        holder.probeInfo.setText("Probe ID " + probeId);
        holder.startInfo.setText("Start Time: " + startTime.toLocaleString());
        String summary = PinglyUtils.loadStringForPlural(context,repeatType.getResourceNameForSummary(),repeatValue);
        holder.repeatInfo.setText("Repeat: " + summary);

	}

}
