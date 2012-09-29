package it.polito.ai.lhmf.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idMember;
	private String name;
	private String surname;
	//private String username;
	//private Date regDate;
	//private String eMail;
//	private String address;
//	private String city;
//	private String state;
//	private String cap;
//	private String tel;
//	private String memberType;
//	private String memberStatus;
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

}