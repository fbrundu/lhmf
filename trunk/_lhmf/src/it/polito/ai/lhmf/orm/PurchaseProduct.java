package it.polito.ai.lhmf.orm;



import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PurchaseProduct generated by hbm2java
 */
@Entity
@Table(name = "purchase_product")
public class PurchaseProduct implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PurchaseProductId id;
	private Purchase purchase;
	private Product product;
	private int amount;
	private Date insertedTimestamp;

	public PurchaseProduct() {
	}

	public PurchaseProduct(PurchaseProductId id, Purchase purchase,
			Product product, int amount, Date insertedTimestamp) {
		this.id = id;
		this.purchase = purchase;
		this.product = product;
		this.amount = amount;
		this.insertedTimestamp = insertedTimestamp;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "idPurchase", column = @Column(name = "idPurchase", nullable = false)),
			@AttributeOverride(name = "idProduct", column = @Column(name = "idProduct", nullable = false)) })
	public PurchaseProductId getId() {
		return this.id;
	}

	public void setId(PurchaseProductId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idPurchase", nullable = false, insertable = false, updatable = false)
	public Purchase getPurchase() {
		return this.purchase;
	}

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProduct", nullable = false, insertable = false, updatable = false)
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Column(name = "amount", nullable = false)
	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inserted_timestamp", nullable = false, length = 19)
	public Date getInsertedTimestamp() {
		return this.insertedTimestamp;
	}

	public void setInsertedTimestamp(Date insertedTimestamp) {
		this.insertedTimestamp = insertedTimestamp;
	}

}
