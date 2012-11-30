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
