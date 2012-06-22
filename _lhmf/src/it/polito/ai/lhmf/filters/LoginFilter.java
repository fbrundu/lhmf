package it.polito.ai.lhmf.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter
{
	// paths must be joined by '|' with no spaces
	private static final String pathsIgnored = "/login|/css/.*|/img/.*|/js/.*";

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
	}

	public void destroy()
	{
	}

	public void doFilter(	ServletRequest request, 
							ServletResponse response,
							FilterChain chain) throws IOException, ServletException {
		
		//Mi ricavo l'azione e la sessione
		String refererPage = ((HttpServletRequest) request).getServletPath();
		//Mi ricavo la sessione
		HttpSession session = ((HttpServletRequest) request).getSession();
		
		session.setAttribute("member_type", "nothing");
		
		String prova = (String) session.getAttribute("member_type");
		
		if (refererPage.equals("/AuthRequest")) {
			// Controllo campi form del Login
			
			
			((HttpServletResponse) response)
			.sendRedirect(((HttpServletRequest) request)
				.getContextPath() + "/");
		}
		
		if(!refererPage.matches(pathsIgnored) || 
				(session == null || session.getAttribute("member_type") == null) ) {
			((HttpServletResponse) response)
				.sendRedirect(((HttpServletRequest) request)
						.getContextPath() + "/login");
			
		} else {
			chain.doFilter(request, response);		
		}
		
	}

}
