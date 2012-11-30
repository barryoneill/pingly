/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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

/**
 * cursor adapter to populate probe list entries using a custom layout
 */
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
