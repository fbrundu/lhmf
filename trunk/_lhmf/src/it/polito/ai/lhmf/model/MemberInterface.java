package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.MemberStatus;
import it.polito.ai.lhmf.orm.MemberType;
import it.polito.ai.lhmf.orm.Supplier;

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
import org.springframework.ui.Model;

public class MemberInterface {
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;

	private MemberStatusInterface memberStatusInterface;
	private MessageInterface messageInterface;
	private SupplierInterface supplierInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}

	public void setMemberStatusInterface(
			MemberStatusInterface memberStatusInterface)
	{
		this.memberStatusInterface = memberStatusInterface;
	}

	public void setMessageInterface(MessageInterface messageInterface)
	{
		this.messageInterface = messageInterface;
	}

	public void setSupplierInterface(SupplierInterface supplierInterface)
	{
		this.supplierInterface = supplierInterface;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer newMember(Member member, boolean checkMail, boolean byAdmin)
			throws InvalidParametersException {
		if (member == null)
			throw new InvalidParametersException();

		Integer memberId = (Integer) sessionFactory.getCurrentSession().save(
				member);
		if (!byAdmin) {
			if (checkMail) {
				// TODO Inviare qui la mail con il codice di registrazione.
				/*
				 * SendEmail emailer = new SendEmail(); boolean isSupplier =
				 * false; emailer.sendNormalRegistration(firstname + " " +
				 * lastname, regCode, memberId, email, isSupplier);
				 */
			} else {
				// Mandare messaggio all'admin
				messageInterface.newMessageToAdmin(this.getMemberAdmin(),
						member,
						"Utente richiede l'attivazione dell'account\n\n"
								+ "Id: " + member.getIdMember() + " - "
								+ member.getName() + " " + member.getSurname()
								+ "\n" + "Email: " + member.getEmail() + "\n");
			}
		}
		return memberId;
	}

	@Transactional(readOnly = true)
	public void authMail(Integer idMember, String regCode, Model model)
			throws InvalidParametersException {
		if (idMember == null || regCode == null)
			throw new InvalidParametersException();

		Member m = this.getMember(idMember);

		if (m == null || !m.getRegCode().equals(regCode))
			throw new InvalidParametersException();

		if (!m.getMemberStatus().equals(
				memberStatusInterface.getMemberStatus(MemberStatuses.ENABLED))) {
			if (m.isFromAdmin())
				m.setMemberStatus(memberStatusInterface
						.getMemberStatus(MemberStatuses.ENABLED));
			else
				m.setMemberStatus(memberStatusInterface
						.getMemberStatus(MemberStatuses.VERIFIED_DISABLED));

			model.addAttribute("firstname", m.getName());

			sessionFactory.getCurrentSession().save(m);
			if (!m.isFromAdmin()) {
				model.addAttribute("active", false);
				messageInterface.newMessageToAdmin(
						this.getMemberAdmin(),
						m,
						"Utente richiede l'attivazione dell'account\n\n"
								+ "Id: " + m.getIdMember() + " - "
								+ m.getName() + " " + m.getSurname() + "\n"
								+ "Email: " + m.getEmail() + "\n");
			} else
				model.addAttribute("active", true);
		} else
			model.addAttribute("active", true);
	}

	@Transactional(readOnly = true)
	public Member getMember(Integer idMember) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where idMember = :idMember");
		query.setParameter("idMember", idMember);
		return (Member) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Member getMember(String username) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where username = :username");
		query.setParameter("username", username);
		return (Member) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Member getMemberByEmail(String email) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where email = :email");
		query.setParameter("email", email);
		return (Member) query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public boolean isMemberPresentByEmail(String email) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where email = :email");
		query.setParameter("email", email);
		return query.uniqueResult() != null;
	}

	@Transactional(readOnly = true)
	public boolean isMemberPresentByUsername(String username) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member " + "where username = :username");
		query.setParameter("username", username);
		return query.uniqueResult() != null;
	}

	@Transactional(readOnly = true)
	public Member getMemberAdmin() {
		// Recupero il memberType dell'admin
		MemberType mType = new MemberType(MemberTypes.USER_ADMIN);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType");

		query.setParameter("memberType", mType);
		return (Member) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembers() {
		return sessionFactory.getCurrentSession().createQuery("from Member")
				.list();
	}

	@Transactional(readOnly = true)
	public List<String> getUsernames() {
		List<String> rUsernames = new LinkedList<String>();
		for (Object i : sessionFactory.getCurrentSession()
				.createQuery("from Member").list()) {
			rUsernames.add(((Member) i).getUsername());
		}
		return rUsernames;
	}

	@Transactional(readOnly = true)
	public List<String> getUsernamesExceptMe(String me) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where username != :me");
		query.setParameter("me", me);
		List<String> rUsernames = new LinkedList<String>();
		for (Object i : query.list()) {
			rUsernames.add(((Member) i).getUsername());
		}
		return rUsernames;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getUsersForMessageExceptMe(String me) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where username != :me");
		query.setParameter("me", me);
		Map<String, String> rUsernames = new HashMap<String, String>();
		for (Object i : query.list())
			rUsernames.put(((Member) i).getUsername(), ((Member) i).getName()
					+ " " + ((Member) i).getSurname());
		return rUsernames;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersResp() {
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

	@Transactional(readOnly = true)
	public List<String> getMembersRespString() {
		ArrayList<String> respString = new ArrayList<String>();

		List<Member> listMember = new ArrayList<Member>();
		listMember = this.getMembersResp();

		for (Member m : listMember) {

			String temp = m.getIdMember() + "," + m.getName() + " "
					+ m.getSurname();
			respString.add(temp);
		}

		return respString;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer activateMember(Integer idMember)
			throws InvalidParametersException {
		if (idMember == null)
			throw new InvalidParametersException();
		Query query = sessionFactory.getCurrentSession().createQuery(
				"update Member set status = 2 " + "where idMember = :idMember");

		return (Integer) query.uniqueResult();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer updateMember(Member member)
			throws InvalidParametersException {
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
			throws InvalidParametersException {
		if (idMember == null)
			throw new InvalidParametersException();

		Query query = sessionFactory.getCurrentSession().createQuery(
				"delete from Member " + "where idMember = :idMember");

		query.setParameter("idMember", idMember);

		return (Integer) query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersToActivate() {
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
	public List<Member> getMembersToActivate(MemberType memberType) {
		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberStatus != :memberStatus AND "
						+ "memberType = :memberType order by idMember");

		query.setParameter("memberStatus", mStatus);
		query.setParameter("memberType", memberType);

		return (List<Member>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersToActivate(MemberType memberType,
			Integer page, Integer itemsPerPage) {
		// Recupero il MemberStatus
		MemberStatus mStatus = new MemberStatus(MemberStatuses.ENABLED);

		Query query = null;
		if (memberType != null) {
			query = sessionFactory.getCurrentSession().createQuery(
					"from Member where memberStatus != :memberStatus AND "
							+ "memberType = :memberType order by idMember");

			query.setParameter("memberType", memberType);
		} else {
			query = sessionFactory.getCurrentSession().createQuery(
					"from Member where memberStatus != :memberStatus "
							+ "order by idMember");
		}
		query.setParameter("memberStatus", mStatus);

		List<Member> l = (List<Member>) query.list();

		int startIndex = page * itemsPerPage;
		int endIndex = l.size() < startIndex + itemsPerPage ? l.size()
				: startIndex + itemsPerPage;

		return l.subList(startIndex, endIndex);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembers(MemberType memberType, Integer page,
			Integer itemsPerPage) {
		Query query = null;
		if (memberType != null) {
			query = sessionFactory.getCurrentSession().createQuery(
					"from Member " + "where memberType = :memberType"
							+ " order by idMember");
			query.setParameter("memberType", memberType);
		} else {
			query = sessionFactory.getCurrentSession().createQuery(
					"from Member " + "order by idMember");
		}

		List<Member> l = (List<Member>) query.list();

		int startIndex = page * itemsPerPage;
		int endIndex = l.size() < startIndex + itemsPerPage ? l.size()
				: startIndex + itemsPerPage;

		return l.subList(startIndex, endIndex);
	}

	@Transactional(readOnly = true)
	public Long getNumberItemsToActivate() {
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
	public Long getNumberItemsToActivate(int memberType) {

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
	public Long getNumberItems(int memberType) {
		Query query = null;
		if (memberType == 4) {
			query = sessionFactory.getCurrentSession().createQuery(
					"select count(*) from Member");
		} else {
			// Recupero il MemberStatus
			MemberType mType = new MemberType(memberType);

			query = sessionFactory
					.getCurrentSession()
					.createQuery(
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

		query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select count(*) from Member where memberType = :memberType and regDate between :startDate and :endDate ");

		query.setParameter("memberType", mType);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembers(MemberType memberType) {

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType order by idMember");

		query.setParameter("memberType", memberType);
		return (List<Member>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Member> getMembersSupplier() {

		// Recupero il memberType del supplier
		MemberType mType = new MemberType(MemberTypes.USER_SUPPLIER);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType ");

		query.setParameter("memberType", mType);
		return (List<Member>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getMembersSupplierString(String username)
	{
		// Recupero il memberType del supplier
		MemberType mType = new MemberType(MemberTypes.USER_SUPPLIER);
		Member resp = getMember(username);

		Query query = sessionFactory.getCurrentSession().createQuery(
				"from Member where memberType = :memberType ");
		query.setParameter("memberType", mType);
		List<String> ss = new ArrayList<String>();
		List<Member> ls = (List<Member>) query.list();
		for (Member m : ls)
		{
			Supplier s = supplierInterface.getSupplier(m.getIdMember());
			if (s.getMemberByIdMemberResp().getIdMember() == resp.getIdMember())
			{
				String temp = m.getIdMember() + "," + m.getName() + " "
						+ m.getSurname() + "," + s.getCompanyName();
				ss.add(temp);
			}
		}
		return ss;
	}

	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Map<Member, Float> getTopUsers(Member memberResp) {

		Map<Member, Float> mList = new HashMap<Member, Float>();

		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select purchase.member, pp.product, sum(pp.amount)*(prod.unitCost)"
								+ "from Order as o "
								+ "join o.purchases as purchase "
								+ "join purchase.purchaseProducts as pp "
								+ "join pp.product as prod "
								+ "where o.member = :memberResp "
								+ " AND o.dateClose <= :endDate "
								+ " AND o.dateDelivery is NOT NULL "
								+ "group by purchase.member, pp.product ")
				.setMaxResults(10);

		query.setParameter("memberResp", memberResp);
		query.setDate("endDate", endDate);

		Member tempMember = null;
		Float tempTot = (float) 0;
		boolean first = true;
		if (query.list().size() == 0)
			return null;

		for (Iterator it = query.iterate(); it.hasNext();) {
			Object[] row = (Object[]) it.next();

			if (first) {
				tempMember = (Member) row[0];
				first = false;
			}

			if (tempMember.equals((Member) row[0])) {
				// uguale
				tempTot += (Float) row[2];

			} else {
				// diverso
				mList.put(tempMember, tempTot);
				tempTot = (float) 0;

				tempMember = (Member) row[0];
				tempTot += (Float) row[2];
			}
		}

		// Salvo l'ultimo elemento
		mList.put(tempMember, tempTot);

		// Ordino la mappa
		List<Entry<Member, Float>> entries = new ArrayList<Entry<Member, Float>>(
				mList.entrySet());
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
