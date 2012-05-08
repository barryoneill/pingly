package net.nologin.meep.pingly.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.util.DBUtils;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Date;

import static net.nologin.meep.pingly.db.PinglyDataHelper.TBL_PROBE;
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

		TextView probeInfo, startInfo, repeatInfo;
		ImageView scheduleIcon;
		
		int colProbeNameIdx, colStartTimeIdx, colRepeatTypeIdx, colRepeatValueIdx, colActiveIdx;

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
		holder.scheduleIcon = (ImageView) newView.findViewById(R.id.schedule_entry_icon);

		// column indexes - see ScheduleDAO.queryForScheduleListCursorAdapter() for query
		holder.colProbeNameIdx = cursor.getColumnIndex(TBL_PROBE.COL_NAME);
		holder.colStartTimeIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_STARTTIME);
		holder.colRepeatTypeIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_REPEATTYPE_ID);
		holder.colRepeatValueIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_REPEAT_VALUE);
		holder.colActiveIdx = cursor.getColumnIndex(TBL_SCHEDULE.COL_ACTIVE);

		newView.setTag(holder);

		// bindView will be called after newView, populate items there

		return newView;
	}

	@Override
	public void bindView(View convertView, final Context ctx, Cursor cursor) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		// name
		String probeName = cursor.getString(holder.colProbeNameIdx);
		holder.probeInfo.setText(probeName);

		// start time
		Date startTime = DBUtils.fromGMTDateTimeString(cursor.getString(holder.colStartTimeIdx));
		holder.startInfo.setText(ctx.getString(R.string.schedule_list_startTime) + startTime.toLocaleString());

		// repeat info
		ScheduleRepeatType repeatType = ScheduleRepeatType.fromId(cursor.getInt(holder.colRepeatTypeIdx));
		int repeatValue = cursor.getInt(holder.colRepeatValueIdx);
        String summary = PinglyUtils.loadStringForPlural(ctx,repeatType.getResourceNameForSummary(),repeatValue);
        holder.repeatInfo.setText(ctx.getString(R.string.schedule_list_repeat) + summary);

		// active/inactive icon
		boolean isActive = cursor.getInt(holder.colActiveIdx) > 0;

		int imgResId = isActive ? R.drawable.schedule_details_icon : R.drawable.schedule_details_disabled_icon;
		holder.scheduleIcon.setImageResource(imgResId);
		setStrikeThrough(holder.startInfo, isActive);
		setStrikeThrough(holder.repeatInfo, isActive);

	}

	// add or remove strikethrough depending on schedule active or not
	private void setStrikeThrough(TextView v, boolean scheduleActive){

		int flags = v.getPaintFlags();
		if(scheduleActive){
			flags &= ~Paint.STRIKE_THRU_TEXT_FLAG; // remove

		}
		else{
			flags |= Paint.STRIKE_THRU_TEXT_FLAG; // add
		}
		v.setPaintFlags(flags);
	}

}
