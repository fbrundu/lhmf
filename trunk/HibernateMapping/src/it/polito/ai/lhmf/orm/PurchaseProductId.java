package it.polito.ai.lhmf.orm;

// Generated 25-lug-2012 11.32.27 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * PurchaseProductId generated by hbm2java
 */
@Embeddable
public class PurchaseProductId implements java.io.Serializable {

	private int idPurchase;
	private int idProduct;

	public PurchaseProductId() {
	}

	public PurchaseProductId(int idPurchase, int idProduct) {
		this.idPurchase = idPurchase;
		this.idProduct = idProduct;
	}

	@Column(name = "idPurchase", nullable = false)
	public int getIdPurchase() {
		return this.idPurchase;
	}

	public void setIdPurchase(int idPurchase) {
		this.idPurchase = idPurchase;
	}

	@Column(name = "idProduct", nullable = false)
	public int getIdProduct() {
		return this.idProduct;
	}

	public void setIdProduct(int idProduct) {
		this.idProduct = idProduct;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PurchaseProductId))
			return false;
		PurchaseProductId castOther = (PurchaseProductId) other;

		return (this.getIdPurchase() == castOther.getIdPurchase())
				&& (this.getIdProduct() == castOther.getIdProduct());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIdPurchase();
		result = 37 * result + this.getIdProduct();
		return result;
	}

}
