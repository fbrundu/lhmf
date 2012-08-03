package it.polito.ai.lhmf.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
    /** isPhoneNumberValid: Validate phone number using Java reg ex. 
    * This method checks if the input string is a valid phone number. 
    * @param number String. Phone number to validate 
    * @return boolean: true if phone number is valid, false otherwise. 
    */  
    public static boolean isPhoneNumberValid(String phoneNumber) {  
    	
	    boolean isValid = false;  
	    /* Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn 
	        ^\\(? : May start with an option "(" . 
	        (\\d{3}): Followed by 3 digits. 
	        \\)? : May have an optional ")" 
	        [- ]? : May have an optional "-" after the first 3 digits or after optional ) character. 
	        (\\d{3}) : Followed by 3 digits. 
	         [- ]? : May have another optional "-" after numeric digits. 
	         (\\d{4})$ : ends with four digits. 
	     
	             Examples: Matches following phone numbers: 
	             
	     
	    */  
	    //Initialize reg ex for phone number.   
	    String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$"; //(123)456-7890, 123-456-7890, 1234567890, (123)-456-7890 
	    String expression2 = "^[+]?[0-9]{2,4}[- ]?[0-9]{4,14}(?:x.+)?$"; // +CCC NNNNNNNNNN, +CCC-NNNNNNNNNN, +CCCNNNNNNNNNN
	    CharSequence inputStr = phoneNumber;  
	    Pattern pattern = Pattern.compile(expression);  
	    Pattern pattern2 = Pattern.compile(expression2);
	    Matcher matcher = pattern.matcher(inputStr);
	    Matcher matcher2 = pattern2.matcher(inputStr);
	    
	    if(matcher.matches()){  
	    	isValid = true;  
	    } else if (matcher2.matches()) {
	    	isValid = true; 
	    }
    
    return isValid;  
    }  
}
