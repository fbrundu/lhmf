package it.polito.ai.lhmf.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable {
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmz"); //YYYY-MM-DDThh:mmTZD
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idOrder;
	private String orderName;
	private Date dateOpen;
	private Date dateClose;
	private Date dateDelivery;
	
	private Member memberResp;
	private Supplier supplier;
	public Integer getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public Date getDateOpen() {
		return dateOpen;
	}
	public void setDateOpen(String dateOpen){
		try {
			this.dateOpen = df.parse(dateOpen);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public Date getDateClose() {
		return dateClose;
	}
	public void setDateClose(String dateClose) {
		try {
			this.dateClose = df.parse(dateClose);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public Date getDateDelivery() {
		return dateDelivery;
	}
	public void setDateDelivery(String dateDelivery) {
		try {
			if(dateDelivery != null && !dateDelivery.equals("null")) //Il serializer degli ordini mette "null" se la data di consegna non è settata
				this.dateDelivery = df.parse(dateDelivery);
			else
				this.dateDelivery = null;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public Member getMemberResp() {
		return memberResp;
	}
	public void setMemberResp(Member memberResp) {
		this.memberResp = memberResp;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

}