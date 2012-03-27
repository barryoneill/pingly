package net.nologin.meep.pingly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Arrays;
import java.util.List;

public class ScheduleRepeatTypeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;

    private List<ScheduleRepeatType> items;

    public ScheduleRepeatTypeAdapter(Context context) {
        super();

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        // just accept enum order for now
        this.items = Arrays.asList(ScheduleRepeatType.values());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ScheduleRepeatType getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return getItem(pos).id;
    }

    public int getItemPosition(ScheduleRepeatType type){
        return items.indexOf(type);
    }

    static class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return handleCustom(position,convertView,parent,android.R.layout.simple_spinner_item);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return handleCustom(position,convertView,parent,android.R.layout.simple_spinner_dropdown_item);

    }

    private View handleCustom(int position, View convertView, ViewGroup parent, int layoutId){

        ViewHolder holder;

        if (convertView == null) {

            if(inflater == null){
                inflater = LayoutInflater.from(context);
            }

            /* we reuse the standard simple spinner layout, otherwise we'd get the full dropdown
             layout in the unselected spinner.  We just want the name in the standard form */
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScheduleRepeatType type = getItem(position);

        holder.name.setText(PinglyUtils.loadStringForName(context,type.getResourceNameForName()));

        return convertView;
    }

}
