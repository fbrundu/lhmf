package it.polito.ai.lhmf.util;

public class CheckNumber {
	
	public static boolean isNumeric(String args) {
		boolean result = true;
	
		try {	
			Integer.parseInt(args);
		} catch (NumberFormatException e) {
			result = false;
		}
		
		return result;
		
	}
}
