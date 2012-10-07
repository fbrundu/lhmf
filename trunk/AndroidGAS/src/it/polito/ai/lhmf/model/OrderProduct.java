package it.polito.ai.lhmf.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderProduct implements Parcelable{
	private Order order;
	private Product product;
	private Boolean failed;
	
	public Order getOrder() {
		return order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Boolean isFailed() {
		return failed;
	}

	public void setFailed(Boolean failed) {
		this.failed = failed;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(order, flags);
		dest.writeParcelable(product, flags);
		dest.writeBooleanArray(new boolean[]{failed});
	}
	
	public static final Parcelable.Creator<OrderProduct> CREATOR = new Creator<OrderProduct>() {
		
		@Override
		public OrderProduct[] newArray(int size) {
			return new OrderProduct[size];
		}
		
		@Override
		public OrderProduct createFromParcel(Parcel source) {
			OrderProduct op = new OrderProduct();
			op.setOrder((Order) source.readParcelable(Order.class.getClassLoader()));
			op.setProduct((Product) source.readParcelable(Product.class.getClassLoader()));
			boolean[] failed = new boolean[1];
			source.readBooleanArray(failed);
			op.setFailed(failed[0]);
			return op;
		}
	};

}