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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.Probe;

import java.util.List;

/**
 * list adapter to populate probetype list entries using a custom layout
 */
public class ProbeTypeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;

    private List<String> probeTypeKeys;

    public ProbeTypeAdapter(Context context) {
        super();

        this.context = context;
        this.inflater = LayoutInflater.from(context);

		probeTypeKeys = Probe.getProbeTypeKeys();
    }

    @Override
    public int getCount() {
        return probeTypeKeys.size();
    }

    @Override
    public String getItem(int pos) {
        return probeTypeKeys.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos; // we'll just use the array index
    }

    public int getItemPosition(String typeKey){
        return probeTypeKeys.indexOf(typeKey);
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

		String typeKey = getItem(position);
        holder.name.setText(Probe.getTypeName(context,typeKey));

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

		String typeKey = getItem(position);

		holder.name.setText(Probe.getTypeName(context,typeKey));
        holder.description.setText(Probe.getTypeDesc(context,typeKey));

        return convertView;
    }

}
