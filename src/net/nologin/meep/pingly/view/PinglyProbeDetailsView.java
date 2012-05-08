package net.nologin.meep.pingly.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;


public class PinglyProbeDetailsView extends RelativeLayout implements View.OnClickListener {

    protected TextView nameTextView;
    protected TextView summaryTextView;
	protected TextView iconTextView;
	protected ImageView separatorBottom;

	private Probe probe = null;

    public PinglyProbeDetailsView(Context context) {
        super(context);
        this.initViewCommon(context, null);
    }

    public PinglyProbeDetailsView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.initViewCommon(context, attrs);
    }

    public PinglyProbeDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        this.initViewCommon(context, attrs);
    }

    private void initViewCommon(Context context, AttributeSet attrs) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.pingly_probedetails_view, this);

        nameTextView = (TextView)findViewById(R.id.view_probedetails_name);
		summaryTextView = (TextView)findViewById(R.id.view_probedetails_desc);
		iconTextView = (TextView)findViewById(R.id.view_probedetails_icontext);
		separatorBottom = (ImageView)findViewById(R.id.view_probedetails_separator_bottom);

        if(attrs != null){

            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.PinglyProbeDetailsView);

            final int n = styledAttrs.getIndexCount();
            for (int i = 0; i < n; ++i) {
                int attr = styledAttrs.getIndex(i);

                switch (attr) {

                    case R.styleable.PinglyProbeDetailsView_probeName:
                        setProbeName(styledAttrs.getString(attr));
                        break;

					case R.styleable.PinglyProbeDetailsView_probeDesc:
						setProbeDesc(styledAttrs.getString(attr));
						break;

					case R.styleable.PinglyProbeDetailsView_probeIconTxt:
						setProbeIconText(styledAttrs.getString(attr));
						break;

					case R.styleable.PinglyProbeDetailsView_probeSeparatorBottom:
						setProbeSeparatorBottom(styledAttrs.getBoolean(attr,false));
						break;

				}
            }
            styledAttrs.recycle();
        }



    }

	/**
	 * populates the name and desc (via setProbeName and setProbeDesc) of the probe, and
	 * adds the onClick listener to the button to jump to the specified probe's details activity
	 * @param probe The probe.  If null, the name and desc will be cleared, and the listener removed.
	 * @param addOnClickForEdit True to make view clickable, and will start edit activity for <code>probe</code>
	 */
	public void initForProbe(final Probe probe, boolean addOnClickForEdit){

		if(probe == null){
			this.probe = null;
			setProbeName("");
			setProbeDesc("");
			setProbeIconText("");
			return;
		}

		this.probe = probe;
		setProbeName(probe.name);
		setProbeDesc(probe.desc);
		setProbeIconText(probe.getTypeIconTxt(getContext()));

		if(addOnClickForEdit){
			setFocusable(true);
			setClickable(true);
			setOnClickListener(this);
		}

	}

	public void setProbeName(String name) {
		nameTextView.setText(name);
	}

	public void setProbeDesc(String desc) {
		summaryTextView.setText(desc);
	}


	public void setProbeIconText(String iconText) {
		iconTextView.setText(iconText);
	}

	public void setProbeSeparatorBottom(boolean render) {
		separatorBottom.setVisibility(render ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View view) {

		if(probe == null){
			Log.w(PinglyConstants.LOG_TAG, "No probe set, ignoring onclick");
			return;
		}

		Log.d(PinglyConstants.LOG_TAG, "Clicked, going to probe " + probe);
		PinglyUtils.startActivityProbeDetail(getContext(), probe);
	}

}
