package net.nologin.meep.pingly.util;


public class NumberUtils {

	public static int checkRange(int value, int min, int max){

		if(min > max){
			min = max; // sanity
		}

		if(value > max){
			return max;
		}
		if(value < min){
			return min;
		}
		return value;

	}
}
