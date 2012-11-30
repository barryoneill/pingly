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
package net.nologin.meep.pingly.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import net.nologin.meep.pingly.R;

/**
 * Checkbox on right, with updating text below title based on that checkbox
 */
public class PinglyBooleanPref extends PinglyBasePrefView  {

    private CheckBox checkBox;
    private String summaryOnVal;
    private String summaryOffVal;

    public PinglyBooleanPref(Context context) {
        super(context);
    }

    public PinglyBooleanPref(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public PinglyBooleanPref(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
    }

    @Override
    protected void preparePinglyView(final Context context, AttributeSet attrs) {

        boolean checked = false;

        if(attrs != null){

            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyBooleanPref);

            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);
                switch (attr) {

                    case R.styleable.PinglyBooleanPref_checked:
                        checked = styledAttrs.getBoolean(attr,false);
                        break;

                    case R.styleable.PinglyBooleanPref_summaryOn:
                        summaryOnVal = styledAttrs.getString(attr);
                        break;

                    case R.styleable.PinglyBooleanPref_summaryOff:
                        summaryOffVal = styledAttrs.getString(attr);
                        break;
                }
            }
            styledAttrs.recycle();
        }

        // now that we're aware of summay on or off values, set checkbox val
        setChecked(checked);

        // ensure expander gone but checkbox present
        checkBox.setVisibility(VISIBLE);
        expanderImage.setVisibility(GONE);

        // clicking is done on this view itself, not the checkbox
        checkBox.setFocusable(false);
        checkBox.setClickable(false);

    }


    public boolean getChecked(){
        if(checkBox == null){
            checkBox = (CheckBox)findViewById(R.id.pcp_checkBox);
        }
        return checkBox.isChecked();
    }

    public void setChecked(boolean checked){
        if(checkBox == null){
            checkBox = (CheckBox)findViewById(R.id.pcp_checkBox);
        }
        checkBox.setChecked(checked);

        if(checked && summaryOnVal != null){
            setSummary(summaryOnVal);
        }
        if(!checked && summaryOffVal != null){
            setSummary(summaryOffVal);
        }
    }

    @Override
    public void onClick(View v) {

        setChecked(!getChecked());
    }
}
