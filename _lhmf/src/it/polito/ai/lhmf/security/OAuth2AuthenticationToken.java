package it.polito.ai.lhmf.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -3201654147744284642L;
	
	final private OAuth2AccessToken token;
	final private Object principal;
	final private String providerAccountId;
	final private Object providerAccountDetails;
	final private String serviceProviderId;

	OAuth2AuthenticationToken(OAuth2AccessToken token, String serviceProviderId) {
		super(new ArrayList<GrantedAuthority>(0));
		this.token = token;
		this.providerAccountId = null;
		this.providerAccountDetails = null;
		this.serviceProviderId = serviceProviderId;
		this.principal = "/" + serviceProviderId + "/" + providerAccountId;
		setAuthenticated(false);
	}

	/**
	 * Created by the authentication provider after successful authentication
	 * @param userDetails
	 * @param authorities
	 * @param token
	 */
	OAuth2AuthenticationToken(UserDetails userDetails, Collection<GrantedAuthority> authorities,
			OAuth2AccessToken token, String providerAccountId, Object providerAccountDetails, String serviceProviderId) {
		super(authorities);
		this.token = token;
		this.providerAccountId = providerAccountId;
		this.providerAccountDetails = providerAccountDetails;
		this.serviceProviderId = serviceProviderId;
		this.principal = userDetails;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	public OAuth2AccessToken getAccessToken() {
		return token;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public Object getProviderAccountDetails() {
		return providerAccountDetails;
	}

	public String getProviderAccountId() {
		return providerAccountId;
	}
}
