package it.polito.ai.lhmf.security;

import it.polito.ai.lhmf.exceptions.FacebookNeedsRegistration;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.security.MyUserDetailsService.UserRoles;
import it.polito.ai.lhmf.security.exception.MailNotVerifiedException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.context.OAuth2ClientContext;
import org.springframework.security.oauth2.client.context.OAuth2ClientContextHolder;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.transaction.annotation.Transactional;


public class FacebookAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	public static final String FACEBOOK_USERID_PREFIX = "oauth2:facebook:";
	
	public static final String RESOURCE_ID = "facebook";
	/**
	 * @see #setFilterProcessesUrl(String)
	 */
	protected UserDetailsService userDetailsService;
	
	public static final String DEFAULT_PROTECTED_PATH = "/j_spring_facebook_security_check";
	
	private OAuth2ProtectedResourceDetailsService protectedResourceDetailsService;
	private OAuth2RestTemplate facebookRest;
	
	private SessionFactory sessionFactory;
	
	protected FacebookAuthenticationFilter() {
		super(DEFAULT_PROTECTED_PATH);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		OAuth2ProtectedResourceDetails resource = protectedResourceDetailsService.loadProtectedResourceDetailsById(RESOURCE_ID);
		OAuth2ClientContext context = OAuth2ClientContextHolder.getContext();
		if (context == null) {
			throw new IllegalStateException("No OAuth 2 security context has been established. Unable to access resource '" + resource.getId() + "'.");
		}
		
		ObjectNode result = facebookRest.getForObject("https://graph.facebook.com/me/", ObjectNode.class);
		
		UserDetails userDetails = loadFacebookUserDetails(result);
		if(!userDetails.isEnabled())
			throw new DisabledException("L'account non è ancora stato abilitato dall'amministratore");
		else
			return createSuccessfulAuthentication(userDetails, new OAuth2AuthenticationToken(context.getAccessToken(resource), RESOURCE_ID));
	}
	
	@SuppressWarnings("unchecked")
	protected Authentication createSuccessfulAuthentication(UserDetails userDetails, OAuth2AuthenticationToken auth) {
		return new OAuth2AuthenticationToken(userDetails, (Collection<GrantedAuthority>) userDetails.getAuthorities(), auth.getAccessToken(), auth.getProviderAccountId(), null, auth.getServiceProviderId());
	}
	
	public void setProtectedResourceDetailsService(
			OAuth2ProtectedResourceDetailsService protectedResourceDetailsService) {
		this.protectedResourceDetailsService = protectedResourceDetailsService;
		this.facebookRest = new OAuth2RestTemplate(protectedResourceDetailsService.loadProtectedResourceDetailsById(RESOURCE_ID));
	}
	
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Transactional(readOnly=true)
	private UserDetails loadFacebookUserDetails(ObjectNode values){
		JsonNode idNode = values.get("id");
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Member where username = :username");
		query.setParameter("username", FACEBOOK_USERID_PREFIX + idNode.getTextValue());
		
		Member m = (Member) query.uniqueResult();
		if(m == null)
			throw new FacebookNeedsRegistration("Facebook login successfull but not registered", values);
		else{
			List<SimpleGrantedAuthority> roles = null;
			MemberType type = m.getMemberType();
			if(type.getIdMemberType() == MemberTypes.USER_NORMAL)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.NORMAL));
			else if(type.getIdMemberType() == MemberTypes.USER_RESP)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.RESP));
			else if(type.getIdMemberType() == MemberTypes.USER_ADMIN)
				roles = Collections.singletonList(new SimpleGrantedAuthority(UserRoles.ADMIN));
			MemberStatus status = m.getMemberStatus();
			if (status.getIdMemberStatus() == MemberStatuses.NOT_VERIFIED)
				throw new MailNotVerifiedException("Il tuo indirizzo e-mail non è ancora stato verificato. Verifica la tua casella di posta e attiva la tua mail seguendo le istruzioni nel messaggio che ti abbiamo mandato");
			else if(status.getIdMemberStatus() == MemberStatuses.VERIFIED_DISABLED)
				throw new DisabledException("L'account non è ancora stato abilitato dall'amministratore");
			return new User(m.getUsername(), m.getPassword(), true, true, true, true, roles);
		}
	}
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}

}
