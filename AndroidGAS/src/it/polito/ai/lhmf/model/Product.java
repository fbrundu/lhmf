package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Parcelable {
	public static final String DEFAULT_PRODUCT_PICTURE = "img/prd/noproduct.jpg";
	public static final String NO_MIN_MAX = "No";
	
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
	private Supplier supplier;
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

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idProduct);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeInt(dimension);
		dest.writeString(measureUnit);
		dest.writeInt(unitBlock);
		dest.writeBooleanArray(new boolean[] {availability});
		dest.writeFloat(transportCost);
		dest.writeFloat(unitCost);
		dest.writeString(minBuy);
		dest.writeString(maxBuy);
		dest.writeString(imgPath);
		dest.writeParcelable(supplier, flags);
		dest.writeParcelable(category, flags);
	}
	
	public static final Parcelable.Creator<Product> CREATOR = new Creator<Product>() {
		
		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
		
		@Override
		public Product createFromParcel(Parcel source) {
			Product p = new Product();
			p.setIdProduct(source.readInt());
			p.setName(source.readString());
			p.setDescription(source.readString());
			p.setDimension(source.readInt());
			p.setMeasureUnit(source.readString());
			p.setUnitBlock(source.readInt());
			boolean[] availability = new boolean[1];
			source.readBooleanArray(availability);
			p.setAvailability(availability[0]);
			p.setTransportCost(source.readFloat());
			p.setUnitCost(source.readFloat());
			p.setMinBuy(source.readString());
			p.setMaxBuy(source.readString());
			p.setImgPath(source.readString());
			p.setSupplier((Supplier) source.readParcelable(Supplier.class.getClassLoader()));
			p.setCategory((ProductCategory) source.readParcelable(ProductCategory.class.getClassLoader()));
			return p;
		}
	};
}