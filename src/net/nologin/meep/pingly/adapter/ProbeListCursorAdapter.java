package net.nologin.meep.pingly.adapter;

import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.DBUtils;
import net.nologin.meep.pingly.util.StringUtils;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.model.ProbeType;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Date;
import java.util.TimeZone;

import static net.nologin.meep.pingly.db.PinglyDataHelper.TBL_PROBE;

public class ProbeListCursorAdapter extends SimpleCursorAdapter {

	private LayoutInflater inflater;
	private static final int PROBE_ITEM_LAYOUT = R.layout.probe_list_item;
	private static final String FROM[] = {};
	private static final int TO[] = {};
		
	public ProbeListCursorAdapter(Context context, Cursor c) {
		super(context, PROBE_ITEM_LAYOUT, c, FROM, TO);
		this.inflater = LayoutInflater.from(context);
	}

	static class ViewHolder {
		TextView probeName;
		TextView probeDesc;		
        TextView probeType;
		
		int colIDIdx;
		int colNameIdx;
		int colDescIdx;        
		int colURLIdx;
        int colTypeIdx;
	}

	// http://stackoverflow.com/questions/3535074/getview-vs-bindview-in-a-custom-cursoradapter
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View newView = inflater.inflate(PROBE_ITEM_LAYOUT, parent, false);

		ViewHolder holder = new ViewHolder();
		// view refs
		holder.probeName = (TextView) newView.findViewById(R.id.probe_item_name);
		holder.probeDesc = (TextView) newView.findViewById(R.id.probe_item_desc);
        holder.probeType = (TextView) newView.findViewById(R.id.probe_item_type);
		// column indexes
		holder.colIDIdx = cursor.getColumnIndex(TBL_PROBE.COL_ID);
		holder.colNameIdx = cursor.getColumnIndex(TBL_PROBE.COL_NAME);
		holder.colDescIdx = cursor.getColumnIndex(TBL_PROBE.COL_DESC);
		holder.colURLIdx = cursor.getColumnIndex(TBL_PROBE.COL_URL);
        holder.colTypeIdx = cursor.getColumnIndex(TBL_PROBE.COL_TYPE_ID);
		newView.setTag(holder);

		// bindView will be called after newView, populate items there

		return newView;
	}

	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		// final int probeId = cursor.getInt(holder.colIDIdx);
		
		String probeName = cursor.getString(holder.colNameIdx);
		String probeDesc = cursor.getString(holder.colDescIdx);

        int typeId = cursor.getInt(holder.colTypeIdx);
        ProbeType type = ProbeType.fromId(typeId);
		
		// TODO: replace hardcoded text with i18n
        holder.probeName.setText(StringUtils.isBlank(probeName) ? "[no name]" : probeName);
        holder.probeDesc.setText(StringUtils.isBlank(probeDesc) ? "[no description]" : probeDesc);
        holder.probeType.setText("Type: " + PinglyUtils.loadStringForName(context, type.getResourceNameForName()));

	}

}