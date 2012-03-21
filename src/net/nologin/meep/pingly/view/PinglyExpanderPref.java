package net.nologin.meep.pingly.view;

import android.content.res.TypedArray;
import android.util.Log;
import android.widget.*;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import net.nologin.meep.pingly.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PinglyExpanderPref extends PinglyBasePrefView {

    private String onClickMethodName;
    private Method onClickMethod;

    public PinglyExpanderPref(Context context) {
        super(context);
    }

    public PinglyExpanderPref(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public PinglyExpanderPref(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
    }

    @Override
    protected void preparePinglyView(Context context, AttributeSet attrs) {

        if(attrs != null){
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyExpanderPref);

            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);
                
                if(attr == R.styleable.PinglyExpanderPref_onClick){
                    if (context.isRestricted()) {
                        throw new IllegalStateException("Onclick method attribute cannot be used within a restricted context");
                    }
                    onClickMethodName = styledAttrs.getString(attr);

                }
            }
            styledAttrs.recycle();
        }

        // ensure checkbox gone but expander present
        checkBox.setVisibility(GONE);
        expanderImage.setVisibility(VISIBLE);
        
    }



    @Override
    public void onClick(View v) {

        // caller should have provided an onClick, but let's not die because of that
        if(StringUtils.isBlank(onClickMethodName)){
            String msg = "No 'onClick' defined on view " + PinglyExpanderPref.this.getClass();
            Log.w(PinglyConstants.LOG_TAG,msg);
            return;
        }
        
        Toast.makeText(getContext(), "And clicked", Toast.LENGTH_SHORT).show();

        if (onClickMethod == null) {
            try {
                onClickMethod = getContext().getClass().getMethod(onClickMethodName,
                        View.class);
            } catch (NoSuchMethodException e) {
                int id = getId();
                String idText = id == NO_ID ? "" : " with id '"
                        + getContext().getResources().getResourceEntryName(
                        id) + "'";
                throw new IllegalStateException("Could not find a method " +
                        onClickMethodName + "(View) in the activity "
                        + getContext().getClass() + " for onClick handler"
                        + " on view " + PinglyExpanderPref.this.getClass() + idText, e);
            }
        }

        try {
            onClickMethod.invoke(getContext(), PinglyExpanderPref.this);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not execute non "
                    + "public method of the activity", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not execute "
                    + "method of the activity", e);
        }
    }


}
