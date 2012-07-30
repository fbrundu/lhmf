package it.polito.ai.lhmf.orm;

// Generated 30-lug-2012 15.24.51 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Member generated by hbm2java
 */
@Entity
@Table(name = "member", uniqueConstraints = {
		@UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "username") })
public class Member implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idMember;
	private MemberType memberType;
	private MemberStatus memberStatus;
	private String name;
	private String surname;
	private String username;
	private String password;
	private String regCode;
	private Date regDate;
	private String email;
	private String address;
	private String city;
	private String state;
	private String cap;
	private String tel;
	private boolean fromAdmin;
	private Set<Message> messagesForIdReceiver = new HashSet<Message>(0);
	private Set<Message> messagesForIdSender = new HashSet<Message>(0);
	private Set<Notify> notifies = new HashSet<Notify>(0);
	private Set<Log> logs = new HashSet<Log>(0);
	private Set<Order> orders = new HashSet<Order>(0);
	private Set<Purchase> purchases = new HashSet<Purchase>(0);
	private Supplier supplierByIdMember;
	private Set<Supplier> suppliersForIdMemberResp = new HashSet<Supplier>(0);

	public Member() {
	}

	public Member(MemberType memberType, MemberStatus memberStatus,
			String name, String surname, String username, String password,
			String regCode, Date regDate, String email, String address,
			String city, String state, String cap, boolean fromAdmin) {
		this.memberType = memberType;
		this.memberStatus = memberStatus;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.password = password;
		this.regCode = regCode;
		this.regDate = regDate;
		this.email = email;
		this.address = address;
		this.city = city;
		this.state = state;
		this.cap = cap;
		this.fromAdmin = fromAdmin;
	}

	public Member(MemberType memberType, MemberStatus memberStatus,
			String name, String surname, String username, String password,
			String regCode, Date regDate, String email, String address,
			String city, String state, String cap, String tel,
			boolean fromAdmin, Set<Message> messagesForIdReceiver,
			Set<Message> messagesForIdSender, Set<Notify> notifies, Set<Log> logs, Set<Order> orders,
			Set<Purchase> purchases, Supplier supplierByIdMember,
			Set<Supplier> suppliersForIdMemberResp) {
		this.memberType = memberType;
		this.memberStatus = memberStatus;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.password = password;
		this.regCode = regCode;
		this.regDate = regDate;
		this.email = email;
		this.address = address;
		this.city = city;
		this.state = state;
		this.cap = cap;
		this.tel = tel;
		this.fromAdmin = fromAdmin;
		this.messagesForIdReceiver = messagesForIdReceiver;
		this.messagesForIdSender = messagesForIdSender;
		this.notifies = notifies;
		this.logs = logs;
		this.orders = orders;
		this.purchases = purchases;
		this.supplierByIdMember = supplierByIdMember;
		this.suppliersForIdMemberResp = suppliersForIdMemberResp;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idMember", unique = true, nullable = false)
	public Integer getIdMember() {
		return this.idMember;
	}

	public void setIdMember(Integer idMember) {
		this.idMember = idMember;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_type", nullable = false)
	public MemberType getMemberType() {
		return this.memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status", nullable = false)
	public MemberStatus getMemberStatus() {
		return this.memberStatus;
	}

	public void setMemberStatus(MemberStatus memberStatus) {
		this.memberStatus = memberStatus;
	}

	@Column(name = "name", nullable = false, length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "surname", nullable = false, length = 45)
	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Column(name = "username", unique = true, nullable = false, length = 250)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 32)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "reg_code", nullable = false, length = 64)
	public String getRegCode() {
		return this.regCode;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "reg_date", nullable = false, length = 10)
	public Date getRegDate() {
		return this.regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	@Column(name = "email", unique = true, nullable = false, length = 45)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "address", nullable = false, length = 45)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "city", nullable = false, length = 45)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", nullable = false, length = 45)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "cap", nullable = false, length = 45)
	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	@Column(name = "tel", length = 45)
	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Column(name = "from_admin", nullable = false)
	public boolean isFromAdmin() {
		return this.fromAdmin;
	}

	public void setFromAdmin(boolean fromAdmin) {
		this.fromAdmin = fromAdmin;
	}

	/**
	 * Restituisce i messaggi che hanno come destinatario questo membro
	 * @return
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "memberByIdReceiver")
	public Set<Message> getMessagesForIdReceiver() {
		return this.messagesForIdReceiver;
	}

	public void setMessagesForIdReceiver(Set<Message> messagesForIdReceiver) {
		this.messagesForIdReceiver = messagesForIdReceiver;
	}

	/**
	 * Restituisce i messaggi che hanno come 'mittente'/'oggetto' questo membro
	 * @return
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "memberByIdSender")
	public Set<Message> getMessagesForIdSender() {
		return this.messagesForIdSender;
	}

	public void setMessagesForIdSender(Set<Message> messagesForIdSender) {
		this.messagesForIdSender = messagesForIdSender;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	public Set<Notify> getNotifies() {
		return this.notifies;
	}

	public void setNotifies(Set<Notify> notifies) {
		this.notifies = notifies;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	public Set<Log> getLogs() {
		return this.logs;
	}

	public void setLogs(Set<Log> logs) {
		this.logs = logs;
	}

	/**
	 * Restituisce gli ordini aperti da questo membro, se e' un responsabile
	 * @return
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * Restituisce le schede di acquisto di questo membro, se e' un utente normale
	 * @return
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	public Set<Purchase> getPurchases() {
		return this.purchases;
	}

	public void setPurchases(Set<Purchase> purchases) {
		this.purchases = purchases;
	}

	/**
	 * Restituisce i dettagli di questo fornitore, se � un Supplier
	 * @return
	 */
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "memberByIdMember")
	public Supplier getSupplierByIdMember() {
		return this.supplierByIdMember;
	}

	public void setSupplierByIdMember(Supplier supplierByIdMember) {
		this.supplierByIdMember = supplierByIdMember;
	}

	/**
	 * Restituisce i fornitori legati a questo membro, se � un responsabile
	 * @return
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "memberByIdMemberResp")
	public Set<Supplier> getSuppliersForIdMemberResp() {
		return this.suppliersForIdMemberResp;
	}

	public void setSuppliersForIdMemberResp(Set<Supplier> suppliersForIdMemberResp) {
		this.suppliersForIdMemberResp = suppliersForIdMemberResp;
	}

}
