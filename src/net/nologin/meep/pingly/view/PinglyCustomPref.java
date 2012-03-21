package net.nologin.meep.pingly.view;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.*;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import net.nologin.meep.pingly.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PinglyCustomPref extends RelativeLayout implements View.OnClickListener {

    private TextView prefNameTV;
    private TextView prefSummaryTV;
    private String prefOnClick;
    private Method onClickMethod;

    public PinglyCustomPref(Context context) {
        super(context);
        this.initView(context,null);
    }

    public PinglyCustomPref(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.initView(context,attrs);
    }

    public PinglyCustomPref(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        this.initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs) {


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pingly_custom_pref, this);

        if(attrs != null){
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyCustomPref);


            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);
                switch (attr) {

                    case R.styleable.PinglyCustomPref_prefName:
                        setPrefName(styledAttrs.getString(attr));
                        break;

                    case R.styleable.PinglyCustomPref_prefSummary:
                        setPrefSummary(styledAttrs.getString(attr));
                        break;

                    case R.styleable.PinglyCustomPref_prefOnClick:

                        if (context.isRestricted()) {
                            throw new IllegalStateException("Onclick method attribute cannot be used within a restricted context");
                        }
                        initPrefOnClick(styledAttrs.getString(attr));

                }
            }
            styledAttrs.recycle();
        }
        

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

    public void initPrefOnClick(String prefOnClick) {

        Log.e(PinglyConstants.LOG_TAG, "INIT ON " + prefOnClick);

        // this is based on android.view.View's constructor
        this.prefOnClick = prefOnClick;


        if(!StringUtils.isBlank(prefOnClick)) {

            setOnClickListener(this);

            // allow selection/highlight similar to a list item
            setFocusable(true);
            setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));
        }

    }

    @Override
    public void onClick(View v) {

        Toast.makeText(getContext(), "And clicked", Toast.LENGTH_SHORT).show();

        if (onClickMethod == null) {
            try {
                onClickMethod = getContext().getClass().getMethod(prefOnClick,
                        View.class);
            } catch (NoSuchMethodException e) {
                int id = getId();
                String idText = id == NO_ID ? "" : " with id '"
                        + getContext().getResources().getResourceEntryName(
                        id) + "'";
                throw new IllegalStateException("Could not find a method " +
                        prefOnClick + "(View) in the activity "
                        + getContext().getClass() + " for onClick handler"
                        + " on view " + PinglyCustomPref.this.getClass() + idText, e);
            }
        }

        try {
            onClickMethod.invoke(getContext(), PinglyCustomPref.this);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not execute non "
                    + "public method of the activity", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not execute "
                    + "method of the activity", e);
        }
    }


}
