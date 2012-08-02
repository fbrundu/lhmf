package it.polito.ai.lhmf.model;

public class ProductCategory {
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
}
