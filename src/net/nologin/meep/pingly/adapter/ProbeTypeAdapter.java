package net.nologin.meep.pingly.adapter;

import net.nologin.meep.pingly.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.model.ProbeType;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Arrays;
import java.util.List;

public class ProbeTypeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;

    private List<ProbeType> probeTypes;

    public ProbeTypeAdapter(Context context) {
        super();

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        // just accept enum order for now
        probeTypes = Arrays.asList(ProbeType.values());
    }

    @Override
    public int getCount() {
        return probeTypes.size();
    }

    @Override
    public ProbeType getItem(int pos) {
        return probeTypes.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return getItem(pos).id;
    }

    public int getItemPosition(ProbeType type){
        return probeTypes.indexOf(type);
    }

    static class ViewHolder {
        TextView name;
        TextView description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            if(inflater == null){
                inflater = LayoutInflater.from(context);
            }

            /* we reuse the standard simple spinner layout, otherwise we'd get the full dropdown
             layout in the unselected spinner.  We just want the name in the standard form */
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProbeType type = getItem(position);

        holder.name.setText(PinglyUtils.loadStringForName(context,type.getResourceNameForName()));

        return convertView;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            if(inflater == null){
                inflater = LayoutInflater.from(context);
            }

            convertView = inflater.inflate(R.layout.probe_type_list_item, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.probe_type_listitem_name);
            holder.description = (TextView) convertView.findViewById(R.id.probe_type_listitem_desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProbeType type = getItem(position);

        holder.name.setText(PinglyUtils.loadStringForName(context,type.getResourceNameForName()));
        holder.description.setText(PinglyUtils.loadStringForName(context, type.getResourceNameForDesc()));

        return convertView;
    }

}
