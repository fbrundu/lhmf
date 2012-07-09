package it.polito.ai.lhmf.security;

import it.polito.ai.lhmf.exceptions.OpenIdNeedsRegistration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class OpenIdRegistrationRequiredHandler extends
		SimpleUrlAuthenticationFailureHandler
{
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException
	{
		boolean registrationRedirect = false;
		if (exception instanceof OpenIdNeedsRegistration)
		{
			OpenIdNeedsRegistration e = (OpenIdNeedsRegistration) exception;
			OpenIDAuthenticationToken token = e.getOpenIDToken();
			if (token != null
					&& token.getStatus().equals(
							OpenIDAuthenticationStatus.SUCCESS))
			{
				// Object principal = token.getPrincipal();
				request.getSession().setAttribute("OPENID_TOKEN", token);
				registrationRedirect = true;
			}
		}
		if (registrationRedirect)
		{
			DefaultRedirectStrategy red = new DefaultRedirectStrategy();
			red.sendRedirect(request, response, "/openid_signup");
		}
		else
			super.onAuthenticationFailure(request, response, exception);
	}
}
