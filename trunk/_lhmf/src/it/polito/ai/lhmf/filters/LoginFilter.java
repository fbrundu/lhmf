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
	private static final String pathsIgnored = "/login|/css/.*|/img/.*";

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
	}

	public void destroy()
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		HttpSession session = ((HttpServletRequest) request).getSession();
		// if user is not going to login and he is not authenticated
		// redirects him to login
		System.out.println(((HttpServletRequest) request).getServletPath()
				.matches(pathsIgnored) + " " + pathsIgnored);
		if (!((HttpServletRequest) request).getServletPath().matches(
				pathsIgnored)
				&& (session == null || session.getAttribute("member_type") == null))
			((HttpServletResponse) response)
					.sendRedirect(((HttpServletRequest) request)
							.getContextPath() + "/login");
		// else does nothing
		else
			chain.doFilter(request, response);
	}

}
