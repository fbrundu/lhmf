package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.MemberStatus;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class MemberStatusInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Transactional(readOnly=true)
	public MemberStatus getMemberStatus(int id)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from MemberStatus where idMemberStatus = :id");
		query.setInteger("id", id);
		return (MemberStatus) query.uniqueResult();
	}
}
