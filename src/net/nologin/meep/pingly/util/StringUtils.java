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
package net.nologin.meep.pingly.util;

/**
 * For the couple of methods needed, the size of apache commons isn't worth it.  But if this
 * class gets larger, consider using it instead.
 */
public class StringUtils {

	public static boolean isBlank(String str){
		return str == null || str.trim().length() < 1;
	}

	public static int getInt(String str, int defaultValue){

		try{
			return Integer.parseInt(str);
		}
		catch(NumberFormatException e){
			return defaultValue;
		}
	}

	public static int getInt(String str, int min, int max, int defaultValue){

		try{
			int val = Integer.parseInt(str);
			return NumberUtils.checkRange(val,min,max);
		}
		catch(NumberFormatException e){
			return NumberUtils.checkRange(defaultValue,min,max);
		}

	}


}
