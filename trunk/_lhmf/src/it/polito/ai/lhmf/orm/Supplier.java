package it.polito.ai.lhmf.orm;

// Generated 24-lug-2012 21.25.07 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Supplier generated by hbm2java
 */
@Entity
@Table(name = "supplier", catalog = "malnati_project")
public class Supplier implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idMember;
	private Member memberByIdMember;
	private Member memberByIdMemberResp;
	private String companyName;
	private String description;
	private String contactName;
	private String fax;
	private String website;
	private String paymentMethod;
	private Set<Order> orders = new HashSet<Order>(0);

	public Supplier() {
	}

	public Supplier(Member memberByIdMember, Member memberByIdMemberResp,
			String paymentMethod) {
		this.memberByIdMember = memberByIdMember;
		this.memberByIdMemberResp = memberByIdMemberResp;
		this.paymentMethod = paymentMethod;
	}

	public Supplier(Member memberByIdMember, Member memberByIdMemberResp,
			String companyName, String description, String contactName,
			String fax, String website, String paymentMethod, Set<Order> orders) {
		this.memberByIdMember = memberByIdMember;
		this.memberByIdMemberResp = memberByIdMemberResp;
		this.companyName = companyName;
		this.description = description;
		this.contactName = contactName;
		this.fax = fax;
		this.website = website;
		this.paymentMethod = paymentMethod;
		this.orders = orders;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "memberByIdMember"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "idMember", unique = true, nullable = false)
	public int getIdMember() {
		return this.idMember;
	}

	public void setIdMember(int idMember) {
		this.idMember = idMember;
	}

	/**
	 * Ritorna i dettagli di questo Supplier, visto come Member
	 * @return
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public Member getMemberByIdMember() {
		return this.memberByIdMember;
	}

	public void setMemberByIdMember(Member memberByIdMember) {
		this.memberByIdMember = memberByIdMember;
	}
	
	/**
	 * Ritorna il responsabile legato a questo Supplier
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMemberResp", nullable = false)
	public Member getMemberByIdMemberResp() {
		return this.memberByIdMemberResp;
	}

	public void setMemberByIdMemberResp(Member memberByIdMemberResp) {
		this.memberByIdMemberResp = memberByIdMemberResp;
	}

	@Column(name = "company_name", length = 45)
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "description", length = 45)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "contact_name", length = 45)
	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	@Column(name = "fax", length = 45)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "website", length = 45)
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Column(name = "payment_method", nullable = false, length = 45)
	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

}
