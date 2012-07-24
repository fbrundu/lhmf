package it.polito.ai.lhmf.orm;

// Generated 8-giu-2012 18.52.32 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Table;

/**
 * Purchase generated by hbm2java
 */
@Entity
@Table(name = "purchase")
public class Purchase implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idPurchase;
	private Order order;
	private Member member;
	private boolean isShipped;
	private Set<PurchaseProduct> purchaseProducts = new HashSet<PurchaseProduct>(
			0);

	public Purchase()
	{
	}

	public Purchase(Order order, Member member, boolean isShipped) {
		this.order = order;
		this.member = member;
		this.isShipped = isShipped;
	}

	public Purchase(Order order, Member member, boolean isShipped,
			Set<PurchaseProduct> purchaseProducts) {
		this.order = order;
		this.member = member;
		this.isShipped = isShipped;
		this.purchaseProducts = purchaseProducts;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idPurchase", unique = true, nullable = false)
	public Integer getIdPurchase()
	{
		return this.idPurchase;
	}

	public void setIdPurchase(Integer idPurchase)
	{
		this.idPurchase = idPurchase;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idOrder", nullable = false)
	public Order getOrder()
	{
		return this.order;
	}

	public void setOrder(Order order)
	{
		this.order = order;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMember", nullable = false)
	public Member getMember()
	{
		return this.member;
	}

	public void setMember(Member member)
	{
		this.member = member;
	}
	
	@Column(name = "isShipped", nullable = false)
	public boolean isIsShipped() {
		return this.isShipped;
	}

	public void setIsShipped(boolean isShipped) {
		this.isShipped = isShipped;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase")
	public Set<PurchaseProduct> getPurchaseProducts()
	{
		return this.purchaseProducts;
	}

	public void setPurchaseProducts(Set<PurchaseProduct> purchaseProducts)
	{
		this.purchaseProducts = purchaseProducts;
	}

}
