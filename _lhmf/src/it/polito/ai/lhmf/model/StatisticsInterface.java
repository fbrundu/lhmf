package it.polito.ai.lhmf.model;

import org.hibernate.SessionFactory;

public class StatisticsInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
}
