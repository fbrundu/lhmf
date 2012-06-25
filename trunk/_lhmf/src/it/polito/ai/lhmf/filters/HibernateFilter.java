package it.polito.ai.lhmf.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Servlet Filter implementation class HibernateFilter
 */
public class HibernateFilter implements Filter
{
	private SessionFactory sf;

	/**
	 * Default constructor.
	 */
	public HibernateFilter()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		if(requiresHibernateSession((HttpServletRequest)request)){
			Session hibernateSession = sf.getCurrentSession();
			request.setAttribute("hibernate_session", hibernateSession);
			Transaction tx = null;
			try
			{
				tx = hibernateSession.beginTransaction();
				chain.doFilter(request, response);
				tx.commit();
			}
			catch (Throwable ex)
			{
				if (tx != null)
					tx.rollback();
				throw new ServletException(ex);
			}
			finally
			{
				if (hibernateSession != null && hibernateSession.isOpen())
					hibernateSession.close();
			}
		}
		else
			chain.doFilter(request, response);
	}

	private static boolean requiresHibernateSession(HttpServletRequest request) {
		String path = request.getServletPath();
		if(path.matches("/css/.*|/img/.*|/js/.*"))
			return false;
		return true;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException
	{
		try
		{
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.buildServiceRegistry();
			sf = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (Throwable ex)
		{
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

}
