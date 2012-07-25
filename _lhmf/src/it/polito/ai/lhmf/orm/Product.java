package it.polito.ai.lhmf.orm;

// Generated 25-lug-2012 11.32.27 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Product generated by hbm2java
 */
@Entity
@Table(name = "product")
public class Product implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idProduct;
	private ProductCategory productCategory;
	private Supplier supplier;
	private String name;
	private String description;
	private int dimension;
	private String measureUnit;
	private Integer unitBlock;
	private boolean availability;
	private float transportCost;
	private float unitCost;
	private Integer minBuy;
	private Integer maxBuy;
	private String imgPath;
	private Set<PurchaseProduct> purchaseProducts = new HashSet<PurchaseProduct>(0);
	private Set<Order> orders = new HashSet<Order>(0);
	private Set<Message> messages = new HashSet<Message>(0);

	public Product() {
	}

	public Product(ProductCategory productCategory, Supplier supplier,
			String name, String description, int dimension, String measureUnit,
			boolean availability, float transportCost, float unitCost) {
		this.productCategory = productCategory;
		this.supplier = supplier;
		this.name = name;
		this.description = description;
		this.dimension = dimension;
		this.measureUnit = measureUnit;
		this.availability = availability;
		this.transportCost = transportCost;
		this.unitCost = unitCost;
	}

	public Product(ProductCategory productCategory, Supplier supplier,
			String name, String description, int dimension, String measureUnit,
			Integer unitBlock, boolean availability, float transportCost,
			float unitCost, Integer minBuy, Integer maxBuy, String imgPath,
			Set<PurchaseProduct> purchaseProducts, Set<Order> orders, Set<Message> messages) {
		this.productCategory = productCategory;
		this.supplier = supplier;
		this.name = name;
		this.description = description;
		this.dimension = dimension;
		this.measureUnit = measureUnit;
		this.unitBlock = unitBlock;
		this.availability = availability;
		this.transportCost = transportCost;
		this.unitCost = unitCost;
		this.minBuy = minBuy;
		this.maxBuy = maxBuy;
		this.imgPath = imgPath;
		this.purchaseProducts = purchaseProducts;
		this.orders = orders;
		this.messages = messages;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idProduct", unique = true, nullable = false)
	public Integer getIdProduct() {
		return this.idProduct;
	}

	public void setIdProduct(Integer idProduct) {
		this.idProduct = idProduct;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idCategory", nullable = false)
	public ProductCategory getProductCategory() {
		return this.productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idSupplier", nullable = false)
	public Supplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Column(name = "name", nullable = false, length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = false, length = 45)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "dimension", nullable = false)
	public int getDimension() {
		return this.dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	@Column(name = "measure_unit", nullable = false, length = 45)
	public String getMeasureUnit() {
		return this.measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	@Column(name = "unit_block")
	public Integer getUnitBlock() {
		return this.unitBlock;
	}

	public void setUnitBlock(Integer unitBlock) {
		this.unitBlock = unitBlock;
	}

	@Column(name = "availability", nullable = false)
	public boolean isAvailability() {
		return this.availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	@Column(name = "transport_cost", nullable = false, precision = 12, scale = 0)
	public float getTransportCost() {
		return this.transportCost;
	}

	public void setTransportCost(float transportCost) {
		this.transportCost = transportCost;
	}

	@Column(name = "unit_cost", nullable = false, precision = 12, scale = 0)
	public float getUnitCost() {
		return this.unitCost;
	}

	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}

	@Column(name = "min_buy")
	public Integer getMinBuy() {
		return this.minBuy;
	}

	public void setMinBuy(Integer minBuy) {
		this.minBuy = minBuy;
	}

	@Column(name = "max_buy")
	public Integer getMaxBuy() {
		return this.maxBuy;
	}

	public void setMaxBuy(Integer maxBuy) {
		this.maxBuy = maxBuy;
	}

	@Column(name = "imgPath", length = 100)
	public String getImgPath() {
		return this.imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	public Set<PurchaseProduct> getPurchaseProducts() {
		return this.purchaseProducts;
	}

	public void setPurchaseProducts(Set<PurchaseProduct> purchaseProducts) {
		this.purchaseProducts = purchaseProducts;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "products")
	public Set<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	public Set<Message> getMessages() {
		return this.messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

}
