package net.nologin.meep.pingly.adapter;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import static net.nologin.meep.pingly.model.PinglyDataHelper.TBL_TASK;

public class PinglyCursorTaskAdapter extends SimpleCursorAdapter {

	private LayoutInflater inflater;
	private static final int TASK_ITEM_LAYOUT = R.layout.task_list_item;	
	private static final String FROM[] = {};
	private static final int TO[] = {};
		
	public PinglyCursorTaskAdapter(Context context, Cursor c) {

		// TODO: see deprecated notes	
		super(context, TASK_ITEM_LAYOUT, c, FROM, TO);		
		this.inflater = LayoutInflater.from(context);
	}

	static class ViewHolder {
		TextView firstLine;
		TextView secondLine;		
		
		int colIDIdx;
		int colNameIdx;
		int colDescIdx;
		int colURLIdx;
	}

	// http://stackoverflow.com/questions/3535074/getview-vs-bindview-in-a-custom-cursoradapter
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View newView = inflater.inflate(TASK_ITEM_LAYOUT, parent, false);

		ViewHolder holder = new ViewHolder();
		// view refs
		holder.firstLine = (TextView) newView.findViewById(R.id.firstLine);
		holder.secondLine = (TextView) newView.findViewById(R.id.secondLine);		
		// column indexes
		holder.colIDIdx = cursor.getColumnIndex(TBL_TASK.COL_ID);
		holder.colNameIdx = cursor.getColumnIndex(TBL_TASK.COL_NAME);
		holder.colDescIdx = cursor.getColumnIndex(TBL_TASK.COL_DESC);
		holder.colURLIdx = cursor.getColumnIndex(TBL_TASK.COL_URL);
		newView.setTag(holder);

		// bindView will be called after newView, populate items there

		return newView;
	}

	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		// final int taskId = cursor.getInt(holder.colIDIdx);
		
		String taskName = cursor.getString(holder.colNameIdx);
		String taskDesc = cursor.getString(holder.colDescIdx);
		
		// TODO: load blank placeholders with i18n
		holder.firstLine.setText(StringUtils.isBlank(taskName)?"[no name]":taskName);
		holder.secondLine.setText(StringUtils.isBlank(taskDesc)?"[no description]":taskDesc);		

	}

}
