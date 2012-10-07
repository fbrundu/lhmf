package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Supplier implements Parcelable {
	private Integer idMember;
	private String companyName;
	private String description;
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getIdMember() {
		return idMember;
	}
	
	public void setIdMember(Integer idMember) {
		this.idMember = idMember;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idMember);
		dest.writeString(companyName);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<Supplier> CREATOR = new Creator<Supplier>() {
		
		@Override
		public Supplier[] newArray(int size) {
			return new Supplier[size];
		}
		
		@Override
		public Supplier createFromParcel(Parcel source) {
			Supplier supplier = new Supplier();
			
			supplier.setIdMember(source.readInt());
			supplier.setCompanyName(source.readString());
			supplier.setDescription(source.readString());
			
			return supplier;
		}
	};
}