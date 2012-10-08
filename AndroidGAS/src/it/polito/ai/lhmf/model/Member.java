package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Parcelable{
	private Integer idMember;
	private String name;
	private String surname;
	private String email;
	private String address;
	private String city;
	private String state;
	private String cap;
	private String tel;
	private Integer memberTypeId;
	private Integer memberStatusId;
	
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
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getMemberTypeId() {
		return memberTypeId;
	}

	public void setMemberTypeId(Integer memberTypeId) {
		this.memberTypeId = memberTypeId;
	}

	public Integer getMemberStatusId() {
		return memberStatusId;
	}

	public void setMemberStatusId(Integer memberStatusId) {
		this.memberStatusId = memberStatusId;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		else if(!(o instanceof Member))
			return false;
		else
			return idMember.equals(((Member)o).getIdMember());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idMember);
		dest.writeString(name);
		dest.writeString(surname);
		dest.writeString(email);
		dest.writeString(address);
		dest.writeString(city);
		dest.writeString(state);
		dest.writeString(cap);
		dest.writeString(tel);
		dest.writeInt(memberTypeId);
		dest.writeInt(memberStatusId);
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
			member.setEmail(source.readString());
			member.setAddress(source.readString());
			member.setCity(source.readString());
			member.setState(source.readString());
			member.setCap(source.readString());
			member.setTel(source.readString());
			member.setMemberTypeId(source.readInt());
			member.setMemberStatusId(source.readInt());
			return member;
		}
	};
}