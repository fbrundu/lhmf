package it.polito.ai.lhmf.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Supplier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private Integer idMember;
	//private Integer idMemberResp;
	
	private String companyName;
	private String description;
	//private String contactName;
	//private String fax;
	//private String website;
	//private String paymentMethod;
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
}