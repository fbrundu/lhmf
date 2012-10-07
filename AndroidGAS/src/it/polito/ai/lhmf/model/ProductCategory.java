package it.polito.ai.lhmf.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductCategory implements Parcelable{
	private Integer idProductCategory;
	private String description;
	
	public Integer getIdProductCategory() {
		return idProductCategory;
	}
	
	public void setIdProductCategory(Integer idProductCategory) {
		this.idProductCategory = idProductCategory;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString(){
		return description;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idProductCategory);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<ProductCategory> CREATOR = new Creator<ProductCategory>() {
		
		@Override
		public ProductCategory[] newArray(int size) {
			return new ProductCategory[size];
		}
		
		@Override
		public ProductCategory createFromParcel(Parcel source) {
			ProductCategory pc = new ProductCategory();
			pc.setIdProductCategory(source.readInt());
			pc.setDescription(source.readString());
			return pc;
		}
	};
}
