package it.polito.ai.lhmf.security;

import it.polito.ai.lhmf.exceptions.FacebookNeedsRegistration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class FacebookRegistrationRequiredHandler extends SimpleUrlAuthenticationFailureHandler{
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		if(exception instanceof AccessTokenRequiredException)
			throw exception;
		
		boolean registrationRedirect = false;
		if(exception instanceof FacebookNeedsRegistration){
			FacebookNeedsRegistration e = (FacebookNeedsRegistration) exception;
			ObjectNode values = e.getFacebookValuesNode();
			request.getSession().setAttribute("FACEBOOK_VALUES", values);
			registrationRedirect = true;
		}
		if(registrationRedirect){
			DefaultRedirectStrategy red = new DefaultRedirectStrategy();
			red.sendRedirect(request, response, "/facebook_signup");
		}
		else
			super.onAuthenticationFailure(request, response, exception);
	}
}
