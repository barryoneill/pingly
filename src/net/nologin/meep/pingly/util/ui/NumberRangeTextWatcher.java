package net.nologin.meep.pingly.util.ui;

import android.text.Editable;
import android.text.TextWatcher;


public class NumberRangeTextWatcher implements  TextWatcher {

	private int min,max;

	public NumberRangeTextWatcher(int min, int max){
		this.min = min;
		this.max = max;
	}

	@Override
	public void afterTextChanged(Editable editable) {

		try {
			if(editable.length() > 0){
				int value = Integer.parseInt(editable.toString());
				if(value < min) {
					editable.clear();
					editable.insert(0,String.valueOf(min));
				}
				if(value > max){
					editable.clear();
					editable.insert(0,String.valueOf(max));
				}
			}
		}
		catch (NumberFormatException e) {
			// the EditText this is applied to should have XML attributes such as
			// android:inputType="number"
			// android:numeric="integer"
			// set, so the user can only enter whole numbers.  Just in case, we'll go with the min
			editable.clear();
			editable.insert(0,String.valueOf(min));
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// nop
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// nop
	}
}
