package it.polito.ai.lhmf.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Purchase implements Serializable {
	public static final String SHIPPED = "Spedizione Effettuata";
	public static final String NOT_SHIPPED = "Spedizione Non Effettuata";
	
	private Integer idPurchase;
	private String isShipped;
	private Order order;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float totCost;
	
	public Purchase(){
		setTotCost(0.0f);
	}

	public float getTotCost() {
		return totCost;
	}

	public void setTotCost(float totCost) {
		this.totCost = totCost;
	}

	public Integer getIdPurchase() {
		return idPurchase;
	}

	public void setIdPurchase(Integer idPurchase) {
		this.idPurchase = idPurchase;
	}

	public String getIsShipped() {
		return isShipped;
	}

	public void setIsShipped(String isShipped) {
		this.isShipped = isShipped;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
