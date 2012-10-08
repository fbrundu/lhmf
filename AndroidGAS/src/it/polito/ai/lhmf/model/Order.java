package it.polito.ai.lhmf.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Parcelable {
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmz"); //YYYY-MM-DDThh:mmTZD
		
	private Integer idOrder;
	private String orderName;
	private Date dateOpen;
	private Date dateClose;
	private Date dateDelivery;
	private Member memberResp;
	private Supplier supplier;
	private float cost;
	
	public Order(){
		setCost(0.0f);
	}
	
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
	
	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idOrder);
		dest.writeString(orderName);
		dest.writeString(df.format(dateOpen));
		dest.writeString(df.format(dateClose));
		if(dateDelivery == null)
			dest.writeString("null");
		else
			dest.writeString(df.format(dateDelivery));
		dest.writeParcelable(memberResp, flags);
		dest.writeParcelable(supplier, flags);
		dest.writeFloat(cost);
	}
	
	public static final Parcelable.Creator<Order> CREATOR = new Creator<Order>() {
		
		@Override
		public Order[] newArray(int size) {
			return new Order[size];
		}
		
		@Override
		public Order createFromParcel(Parcel source) {
			Order order = new Order();
			order.setIdOrder(source.readInt());
			order.setOrderName(source.readString());
			order.setDateOpen(source.readString());
			order.setDateClose(source.readString());
			order.setDateDelivery(source.readString());
			order.setMemberResp((Member) source.readParcelable(Member.class.getClassLoader()));
			order.setSupplier((Supplier)source.readParcelable(Supplier.class.getClassLoader()));
			order.setCost(source.readFloat());
			return order;
		}
	};
}