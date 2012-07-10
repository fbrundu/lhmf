package it.polito.ai.lhmf.security;

import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;

public class FacebookAccessTokenProvider extends AuthorizationCodeAccessTokenProvider{
	/**
	 * Override the access token provider in order to add the custom Token message converter. 
	 * Spring OAUTH framework doesn't handle text/plain responses, so i provided my own.
	 */
	@Override
	public void setMessageConverters(
			List<HttpMessageConverter<?>> messageConverters) {
		messageConverters.add(new FacebookAccessTokenMessageConverter());
		super.setMessageConverters(messageConverters);
	}
}
