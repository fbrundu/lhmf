package it.polito.ai.lhmf.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notify implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idNotify;
	private Integer notifyCategory;
	private String text;
	
	public Integer getIdNotify() {
		return idNotify;
	}

	public void setIdNotify(Integer idNotify) {
		this.idNotify = idNotify;
	}

	public Integer getNotifyCategory() {
		return notifyCategory;
	}

	public void setNotifyCategory(Integer notifyCategory) {
		this.notifyCategory = notifyCategory;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}