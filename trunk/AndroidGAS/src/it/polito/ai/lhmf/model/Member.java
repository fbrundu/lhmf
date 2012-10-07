package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Parcelable{
	private Integer idMember;
	private String name;
	private String surname;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getIdMember() {
		return idMember;
	}
	
	public void setIdMember(Integer idMember) {
		this.idMember = idMember;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idMember);
		dest.writeString(name);
		dest.writeString(surname);
	}
	
	public static final Parcelable.Creator<Member> CREATOR = new Creator<Member>() {
		
		@Override
		public Member[] newArray(int size) {
			return new Member[size];
		}
		
		@Override
		public Member createFromParcel(Parcel source) {
			Member member = new Member();
			member.setIdMember(source.readInt());
			member.setName(source.readString());
			member.setSurname(source.readString());
			return member;
		}
	};

}