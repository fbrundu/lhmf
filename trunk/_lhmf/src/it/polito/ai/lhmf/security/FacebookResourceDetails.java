package it.polito.ai.lhmf.security;

import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class FacebookResourceDetails extends AuthorizationCodeResourceDetails {
	/**
	 * Override OAUTH framework resource details class. The method isClientOnly returned false, causing the framework not to accept that unauthenticated
	 * users required facebook access tokens. But authentication is actually what we want to do through facebook's OAuth, so...
	 */
	@Override
	public boolean isClientOnly() {
		return true;
	}
}
