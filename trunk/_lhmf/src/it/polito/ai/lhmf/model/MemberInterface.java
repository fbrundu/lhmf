package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
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
		// Recupero il memberType dell'admin
		MemberType mType = new MemberType(MemberTypes.USER_ADMIN);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType");

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

	@Transactional(readOnly = true)
	public List<String> getUsernames()
	{
		List<String> rUsernames = new LinkedList<String>();
		for (Object i : sessionFactory.getCurrentSession()
				.createQuery("from Member").list())
		{
			rUsernames.add(((Member) i).getUsername());
		}
		return rUsernames;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersResp()
	{
		// Recupero il memberType del responsabile
		MemberType mType = new MemberType(MemberTypes.USER_RESP);
		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Member where memberType = :memberType AND memberStatus = :memberStatus");

		query.setParameter("memberStatus", mStatus);
		query.setParameter("memberType", mType);
		return (List<Member>) query.list();
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
						+ "city = :city, " + "state = :state, "
						+ "cap = :cap, " + "tel = :tel, "
						+ "memberType = :memberType, " + "status = :status "
						+ "where idMember = :idMember");
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
				"delete from Member " + "where idMember = :idMember");

		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersToActivate()
	{
		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from Member where memberStatus != :memberStatus order by idMember");

		query.setParameter("memberStatus", mStatus);

		return (List<Member>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersToActivate(MemberType memberType)
	{

		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberStatus != :memberStatus AND "
						+ "memberType = :memberType order by idMember");

		query.setParameter("memberStatus", mStatus);
		query.setParameter("memberType", memberType);

		return (List<Member>) query.list();
	}

	@Transactional(readOnly = true)
	public Long getNumberItemsToActivate()
	{
		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from Member where memberStatus != :memberStatus");
		query.setParameter("memberStatus", mStatus);

		return (Long) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Long getNumberItemsToActivate(int memberType)
	{

		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);
		MemberType mType = new MemberType(memberType);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Member where memberStatus != :memberStatus AND "
						+ "memberType = :memberType");

		query.setParameter("memberStatus", mStatus);
		query.setParameter("memberType", mType);

		return (Long) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Long getNumberItems(int memberType)
	{
		Query query = null;
		if(memberType == 4)
		{
			query = sessionFactory.getCurrentSession().createQuery(
					"select count(*) from Member");
		}
		else
		{
			// Recupero il MemberStatus
			MemberType mType = new MemberType(memberType);

			query = sessionFactory.getCurrentSession().createQuery(
					"select count(*) from Member where memberType = :memberType");

			query.setParameter("memberType", mType);
		}
		return (Long) query.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Long getNumberItemsPerMonth(int memberType, int month, int year) {
		
		Query query = null;
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		Date startDate = cal.getTime();
		cal.set(year, month, 31);
		Date endDate = cal.getTime();
		
		// Recupero il MemberStatus
		MemberType mType = new MemberType(memberType);
		
		query = sessionFactory.getCurrentSession().createQuery(
				"select count(*) from Member where memberType = :memberType and regDate between :startDate and :endDate ");
		
		query.setParameter("memberType", mType);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembers(MemberType memberType)
	{

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType order by idMember");

		query.setParameter("memberType", memberType);
		return (List<Member>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersSupplier()
	{

		// Recupero il memberType del supplier
		MemberType mType = new MemberType(MemberTypes.USER_SUPPLIER);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType ");

		query.setParameter("memberType", mType);
		return (List<Member>) query.list();
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Member, Float> getTopUsers(Member memberResp) {
		
		Map<Member,Float> mList = new HashMap<Member,Float>();
		
		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select purchase.member, pp.product, sum(pp.amount)*(prod.unitBlock * prod.unitCost)" +
				"from Order as o " +
				"join o.purchases as purchase " +
				"join purchase.purchaseProducts as pp " +
				"join pp.product as prod " +
			    "where o.member = :memberResp " +
				" AND o.dateClose <= :endDate " +
				" AND o.dateDelivery is NOT NULL " +
				"group by purchase.member, pp.product ").setMaxResults(10);
		
		query.setParameter("memberResp", memberResp);
		query.setDate("endDate", endDate);
		
		Member tempMember = null;
		Float tempTot = (float) 0;
		boolean first = true;
		
		for (Iterator it = query.iterate(); it.hasNext();) {
			Object[] row = (Object[]) it.next();
			
			if(first) {
				tempMember = (Member) row[0];
				first = false;
			}
			
			if(tempMember.equals((Member) row[0])) {
				//uguale
				tempTot += (Float) row[2];
				
			} else {
				//diverso
				mList.put(tempMember, tempTot);
				tempTot = (float) 0;
				
				tempMember = (Member) row[0];
				tempTot += (Float) row[2];
			}
		}
		
		//Salvo l'ultimo elemento
		mList.put(tempMember, tempTot);
		
		//Ordino la mappa
		List<Entry<Member, Float>> entries = new ArrayList<Entry<Member, Float>>(mList.entrySet());
		Collections.sort(entries, new Comparator<Entry<Member, Float>>() {
		    public int compare(Entry<Member, Float> e1, Entry<Member, Float> e2) {
		        return e1.getValue().compareTo(e2.getValue());
		    }
		});
		Collections.reverse(entries);
		
		// Put entries back in an ordered map.
		Map<Member, Float> orderedMap = new LinkedHashMap<Member, Float>();
		for (Entry<Member, Float> entry : entries) {
		    orderedMap.put(entry.getKey(), entry.getValue());
		}	
		
		return orderedMap;
	}

}
