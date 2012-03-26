package net.nologin.meep.pingly.util;

// TODO: see if value of apache commons is worth the jar size
public class StringUtils {

	public static final boolean isBlank(String str){
		return str == null || str.trim().length() < 1;
	}
	
}
