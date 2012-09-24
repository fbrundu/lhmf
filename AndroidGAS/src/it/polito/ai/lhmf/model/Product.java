package it.polito.ai.lhmf.model;

public class Product {
	public static final String DEFAULT_PRODUCT_PICTURE = "img/prd/noproduct.jpg";
	
	private Integer idProduct;
	private String name;
	private String description;
	private Integer dimension;
	private String measureUnit;
	private Integer unitBlock;
	private Boolean availability;
	private Float transportCost;
	private Float unitCost;
	private String minBuy;
	private String maxBuy;
	private String imgPath;
	
	private Integer idMemberSupplier;
	private ProductCategory category;
	
	public Integer getIdProduct() {
		return idProduct;
	}
	
	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getDimension() {
		return dimension;
	}
	
	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}
	
	public String getMeasureUnit() {
		return measureUnit;
	}
	
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}
	
	public Integer getUnitBlock() {
		return unitBlock;
	}
	
	public void setUnitBlock(Integer unitBlock) {
		this.unitBlock = unitBlock;
	}
	
	public Boolean getAvailability() {
		return availability;
	}
	
	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}
	
	public Float getTransportCost() {
		return transportCost;
	}
	
	public void setTransportCost(Float transportCost) {
		this.transportCost = transportCost;
	}
	
	public Float getUnitCost() {
		return unitCost;
	}
	
	public void setUnitCost(Float unitCost) {
		this.unitCost = unitCost;
	}
	
	public String getMinBuy() {
		return minBuy;
	}
	
	public void setMinBuy(String minBuy) {
		this.minBuy = minBuy;
	}
	
	public String getMaxBuy() {
		return maxBuy;
	}
	
	public void setMaxBuy(String maxBuy) {
		this.maxBuy = maxBuy;
	}
	
	public String getImgPath() {
		return imgPath;
	}
	
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Integer getIdMemberSupplier() {
		return idMemberSupplier;
	}

	public void setIdMemberSupplier(Integer idMemberSupplier) {
		this.idMemberSupplier = idMemberSupplier;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}
}