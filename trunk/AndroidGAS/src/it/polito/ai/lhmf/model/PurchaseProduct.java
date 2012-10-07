package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseProduct implements Parcelable {
	private Product product;
	private Purchase purchase;
	private Integer amount;
	private boolean failed;
	
	public PurchaseProduct(){
		setFailed(false);
	}
	
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}

	public Purchase getPurchase() {
		return purchase;
	}

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
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
		dest.writeParcelable(product, flags);
		dest.writeParcelable(purchase, flags);
		dest.writeInt(amount);
		dest.writeBooleanArray(new boolean[]{failed});
	}
	
	public static final Parcelable.Creator<PurchaseProduct> CREATOR = new Creator<PurchaseProduct>() {
		
		@Override
		public PurchaseProduct[] newArray(int size) {
			return new PurchaseProduct[size];
		}
		
		@Override
		public PurchaseProduct createFromParcel(Parcel source) {
			PurchaseProduct pp = new PurchaseProduct();
			pp.setProduct((Product) source.readParcelable(Product.class.getClassLoader()));
			pp.setPurchase((Purchase) source.readParcelable(Purchase.class.getClassLoader()));
			pp.setAmount(source.readInt());
			boolean[] failed = new boolean[1];
			source.readBooleanArray(failed);
			pp.setFailed(failed[0]);
			return pp;
		}
	};
}