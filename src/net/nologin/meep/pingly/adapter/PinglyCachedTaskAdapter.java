package net.nologin.meep.pingly.adapter;

import java.util.List;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.PinglyTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PinglyCachedTaskAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<PinglyTask> taskList;
	private Context context;

	public PinglyCachedTaskAdapter(List<PinglyTask> taskList, Context context) {
		this.taskList = taskList;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return taskList.size();
	}

	public PinglyTask getItem(int pos) {
		return taskList.get(pos);
	}

	public long getItemId(int pos) {
		return getItem(pos).id;
	}

	static class ViewHolder {
		TextView firstLine;
		TextView secondLine;		
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {

			if(inflater == null){
				inflater = LayoutInflater.from(context);
			}
			
			convertView = inflater.inflate(R.layout.task_list_item, parent, false);

			holder = new ViewHolder();
			holder.firstLine = (TextView) convertView
					.findViewById(R.id.firstLine);
			holder.secondLine = (TextView) convertView
					.findViewById(R.id.secondLine);	

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PinglyTask task = taskList.get(position);

		holder.firstLine.setText(task.name);
		holder.secondLine.setText(task.desc);

		return convertView;
	}

}
