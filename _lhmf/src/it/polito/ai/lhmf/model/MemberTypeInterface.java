package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.MemberType;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class MemberTypeInterface
{
	//The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	@Transactional(readOnly=true)
	public MemberType getMemberType(int id)
	{
		Query query = sessionFactory.getCurrentSession().createQuery("from MemberType where idMemberType = :id");
		query.setInteger("id", id);
		return (MemberType) query.uniqueResult();
	}
}
