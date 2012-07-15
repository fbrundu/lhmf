package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberType;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class MemberInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newMember(Member member) throws InvalidParametersException
	{
		if (member == null)
			throw new InvalidParametersException();

		return (Integer) sessionFactory.getCurrentSession().save(member);
	}

	@Transactional(readOnly = true)
	public Member getMember(Integer idMember)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where idMember = :idMember");
		query.setParameter("idMember", idMember);
		return (Member) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Member getMember(String username)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where username = :username");
		query.setParameter("username", username);
		return (Member) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Member getMemberByEmail(String email)
	{
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where email = :email");
		query.setParameter("email", email);
		return (Member) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Member getMemberAdmin()
	{
		//Recupero il memberType dell'admin
		MemberType mType = new MemberType(MemberTypes.USER_ADMIN);
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where memberType = :memberType");
		
		query.setParameter("memberType", mType);
		return (Member) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembers()
	{
		return sessionFactory.getCurrentSession().createQuery("from Member")
				.list();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateMember(Member member)
			throws InvalidParametersException
	{
		if (member == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Member "
						+ "set name = :name, "
						+ "surname = :surname, "
						+ "username = :username, "
						// + "password = :password"
						// + "regCode = :regCode" + "regDate = :regDate"
						+ "email = :email, " + "address = :address, "
						+ "city = :city, " + "state = :state, " + "cap = :cap, "
						+ "tel = :tel, " + "memberType = :memberType, "
						+ "status = :status " + "where idMember = :idMember");
		query.setParameter("name", member.getName());
		query.setParameter("surname", member.getSurname());
		query.setParameter("username", member.getUsername());
		// query.setParameter("password", member.getPassword());
		// query.setParameter("regCode", member.getRegCode());
		// query.setParameter("regDate", member.getRegDate());
		query.setParameter("email", member.getEmail());
		query.setParameter("address", member.getAddress());
		query.setParameter("city", member.getCity());
		query.setParameter("state", member.getState());
		query.setParameter("cap", member.getCap());
		query.setParameter("tel", member.getTel());
		query.setParameter("memberType", member.getMemberType());
		query.setParameter("status", member.getMemberStatus());
		query.setParameter("idMember", member.getIdMember());

		return (Integer) query.executeUpdate();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer deleteMember(Integer idMember)
			throws InvalidParametersException
	{
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Member" + "where idMember = :idMember");

		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}
}
