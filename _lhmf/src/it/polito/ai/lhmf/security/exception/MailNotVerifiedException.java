package it.polito.ai.lhmf.security.exception;

import org.springframework.security.core.AuthenticationException;

public class MailNotVerifiedException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3879046104695174480L;

	public MailNotVerifiedException(String msg) {
		super(msg);
	}

}
