package net.nologin.meep.pingly.view;

import android.content.res.TypedArray;
import android.widget.*;
import net.nologin.meep.pingly.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import net.nologin.meep.pingly.StringUtils;

public abstract class PinglyBasePrefView extends RelativeLayout {

    private TextView prefNameTV;
    private TextView prefSummaryTV;

    public PinglyBasePrefView(Context context) {
        super(context);
        this.initViewCommon(context, null);
    }

    public PinglyBasePrefView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.initViewCommon(context, attrs);
    }

    public PinglyBasePrefView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        this.initViewCommon(context, attrs);
    }

    protected abstract void preparePinglyView(Context context, AttributeSet attrs);

    private void initViewCommon(Context context, AttributeSet attrs) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pingly_basepref_view, this);

        if(attrs != null){

            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyBasePrefView);

            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);
                switch (attr) {

                    case R.styleable.PinglyBasePrefView_prefName:
                        setPrefName(styledAttrs.getString(attr));
                        break;

                    case R.styleable.PinglyBasePrefView_prefSummary:
                        setPrefSummary(styledAttrs.getString(attr));
                        break;
                }
            }
            styledAttrs.recycle();
        }

        // subclass do its stuff too
        preparePinglyView(context,attrs);

    }

    public void setPrefName(String prefName) {

        if(prefNameTV == null){
            prefNameTV = (TextView)findViewById(R.id.pcp_name);
        }
        prefNameTV.setText(StringUtils.isBlank(prefName) ? "" : prefName);

    }

    public void setPrefSummary(String prefSummary) {

        if(prefSummaryTV == null){
            prefSummaryTV = (TextView)findViewById(R.id.pcp_summary);
        }
        prefSummaryTV.setText(StringUtils.isBlank(prefSummary) ? "" : prefSummary);

    }



}
