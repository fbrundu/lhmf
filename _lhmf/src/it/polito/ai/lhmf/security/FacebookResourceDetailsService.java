package it.polito.ai.lhmf.security;

import java.util.Collections;

import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetailsService;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

public class FacebookResourceDetailsService implements OAuth2ProtectedResourceDetailsService{
	private final String RESOURCE_ID = "facebook";
	private final AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
	
	public FacebookResourceDetailsService(){
		resource.setAccessTokenUri("https://graph.facebook.com/oauth/access_token");
		resource.setAuthenticationScheme(AuthenticationScheme.query);
		resource.setClientAuthenticationScheme(AuthenticationScheme.form);
		resource.setClientId("151891021601788");
		resource.setClientSecret("fc14f97110642dc7f46844d757d29229");
		resource.setGrantType("authorization_code");
		resource.setId(RESOURCE_ID);
		resource.setPreEstablishedRedirectUri(null);
		resource.setScope(Collections.singletonList("email"));
		resource.setTokenName("oauth_token");
		resource.setUserAuthorizationUri("https://www.facebook.com/dialog/oauth");
	}
	
	public FacebookResourceDetailsService(boolean mobile){
		this();
		if(mobile)
			resource.setUserAuthorizationUri("https://www.facebook.com/dialog/oauth?display=touch");
	}
	
	@Override
	public OAuth2ProtectedResourceDetails loadProtectedResourceDetailsById(
			String id) throws IllegalArgumentException {
		if(id.equals(RESOURCE_ID))
			return resource;
		return null;
	}

}
