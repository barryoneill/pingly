package net.nologin.meep.pingly.util;

// TODO: see if value of apache commons is worth the jar size
public class StringUtils {

	public static final boolean isBlank(String str){
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
