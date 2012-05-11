package net.nologin.meep.pingly.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.StringUtils;
import net.nologin.meep.pingly.view.PinglyProbeDetailsView;

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
		PinglyProbeDetailsView probeDetailsView;

		int colIDIdx;
		int colNameIdx;
		int colDescIdx;
        int colTypeKeyIdx;
	}

	// http://stackoverflow.com/questions/3535074/getview-vs-bindview-in-a-custom-cursoradapter
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View newView = inflater.inflate(PROBE_ITEM_LAYOUT, parent, false);

		ViewHolder holder = new ViewHolder();
		// view refs
		holder.probeDetailsView = (PinglyProbeDetailsView) newView.findViewById(R.id.probe_list_summaryView);
		// column indexes
		holder.colIDIdx = cursor.getColumnIndex(TBL_PROBE.COL_ID);
		holder.colNameIdx = cursor.getColumnIndex(TBL_PROBE.COL_NAME);
		holder.colDescIdx = cursor.getColumnIndex(TBL_PROBE.COL_DESC);
        holder.colTypeKeyIdx = cursor.getColumnIndex(TBL_PROBE.COL_TYPE_KEY);
		newView.setTag(holder);


		// bindView will be called after newView, populate items there

		return newView;
	}

	@Override
	public void bindView(View convertView, final Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		String probeName = cursor.getString(holder.colNameIdx);
		String probeDesc = cursor.getString(holder.colDescIdx);
		String probeTypeKey = cursor.getString(holder.colTypeKeyIdx);

        holder.probeDetailsView.setProbeName(StringUtils.isBlank(probeName)
				? context.getString(R.string.filler_no_name) : probeName);
        holder.probeDetailsView.setProbeDesc(StringUtils.isBlank(probeDesc)
				? context.getString(R.string.filler_no_description) : probeDesc);
        holder.probeDetailsView.setProbeIconText(Probe.getTypeIconTxt(context, probeTypeKey));
		// ensure view has no onClick set, otherwise it would interfere with adapter click handling
		//holder.probeDetailsView.setProbeOnClickProbeEdit(false);
	}

}
