package net.nologin.meep.pingly.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;

public class PinglyProbeDetailsView extends RelativeLayout {

    protected TextView nameTextView;
    protected TextView summaryTextView;
    protected Button editButton;

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
        View view = layoutInflater.inflate(R.layout.pingly_probedetails_view, this);

        nameTextView = (TextView)findViewById(R.id.view_probedetails_name);
		summaryTextView = (TextView)findViewById(R.id.view_probedetails_desc);
        editButton = (Button)findViewById(R.id.view_probedetails_editButton);

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

				}
            }
            styledAttrs.recycle();
        }

    }

	/**
	 * populates the name and desc (via setProbeName and setProbeDesc) of the probe, and
	 * adds the onClick listener to the button to jump to the specified probe's details activity
	 * @param probe The probe.  If null, the name and desc will be cleared, and the listener removed.
	 */
	public void initForProbe(final Probe probe){

		if(probe == null){
			setProbeName("");
			setProbeDesc("");
			editButton.setOnClickListener(null);
			return;
		}

		setProbeName(probe.name);
		setProbeDesc(probe.desc);

		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				PinglyUtils.startActivityProbeDetail(getContext(), probe.id);
			}
		});

	}

	public void setProbeName(String name) {
		nameTextView.setText(name);
	}

	public void setProbeDesc(String desc) {
		summaryTextView.setText(desc);
	}




}
