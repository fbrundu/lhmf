package it.polito.ai.lhmf.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;

public class OpenIdNeedsRegistration extends UsernameNotFoundException {
	private static final long serialVersionUID = 1L;
	
	private OpenIDAuthenticationToken token;
	
	public OpenIdNeedsRegistration(String message, OpenIDAuthenticationToken token) {
		super(message);
		this.token = token;
	}
	
	public OpenIDAuthenticationToken getOpenIDToken(){
		return token;
	}
}
