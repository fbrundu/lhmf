package it.polito.ai.lhmf.exceptions;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class FacebookNeedsRegistration extends UsernameNotFoundException{
	private static final long serialVersionUID = 1L;
	
	private ObjectNode values;
	
	
	public FacebookNeedsRegistration(String msg, ObjectNode values) {
		super(msg);
		this.values = values;
	}
	
	public ObjectNode getFacebookValuesNode(){
		return values;
	}
	
}
