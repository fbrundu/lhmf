package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Purchase implements Parcelable {
	public static final String SHIPPED = "Ritiro effettuato";
	public static final String NOT_SHIPPED = "Ritiro non effettuato";
	
	private Integer idPurchase;
	private String isShipped;
	private Order order;
	private Member member;
	private Float totCost;
	private boolean failed;
	
	public Purchase(){
		setTotCost(0.0f);
		setFailed(false);
	}

	public Float getTotCost() {
		return totCost;
	}

	public void setTotCost(Float totCost) {
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

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idPurchase);
		dest.writeString(isShipped);
		dest.writeParcelable(order, flags);
		dest.writeParcelable(member, flags);
		dest.writeFloat(totCost);
		dest.writeBooleanArray(new boolean[]{failed});
	}
	
	public static final Parcelable.Creator<Purchase> CREATOR = new Creator<Purchase>() {
		
		@Override
		public Purchase[] newArray(int size) {
			return new Purchase[size];
		}
		
		@Override
		public Purchase createFromParcel(Parcel source) {
			Purchase p = new Purchase();
			p.setIdPurchase(source.readInt());
			p.setIsShipped(source.readString());
			p.setOrder((Order) source.readParcelable(Order.class.getClassLoader()));
			p.setMember((Member) source.readParcelable(Member.class.getClassLoader()));
			p.setTotCost(source.readFloat());
			boolean[] failed = new boolean[1];
			source.readBooleanArray(failed);
			p.setFailed(failed[0]);
			return p;
		}
	};
}
