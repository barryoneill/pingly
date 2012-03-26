package net.nologin.meep.pingly.view;

import android.content.res.TypedArray;
import android.widget.*;
import net.nologin.meep.pingly.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import net.nologin.meep.pingly.util.StringUtils;

public abstract class PinglyBasePrefView extends RelativeLayout implements View.OnClickListener {

    protected TextView nameTextView;
    protected TextView summaryTextView;
    protected ImageView expanderImage;
    protected CheckBox checkBox;
    protected String idText = "";
    
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

        summaryTextView = (TextView)findViewById(R.id.pcp_summary);
        nameTextView = (TextView)findViewById(R.id.pcp_name);
        expanderImage = (ImageView)findViewById(R.id.pcp_expander);
        checkBox = (CheckBox)findViewById(R.id.pcp_checkBox);
        
        if(attrs != null){

            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyBasePrefView);

            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);
                switch (attr) {

                    case R.styleable.PinglyBasePrefView_name:
                        setName(styledAttrs.getString(attr));
                        break;

                    case R.styleable.PinglyBasePrefView_summary:
                        setSummary(styledAttrs.getString(attr));
                        break;
                }
            }
            styledAttrs.recycle();
        }

        // allow selection/highlight similar to a list item
        setFocusable(true);
        setClickable(true);
        setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));

        // each subclass should define onClick
        setOnClickListener(this);

        // subclass do its stuff too
        preparePinglyView(context,attrs);

    }

    public void setName(String prefName) {

        nameTextView.setText(StringUtils.isBlank(prefName) ? "" : prefName);

    }

    public void setSummary(String prefSummary) {

        summaryTextView.setText(StringUtils.isBlank(prefSummary) ? "" : prefSummary);

    }



}
