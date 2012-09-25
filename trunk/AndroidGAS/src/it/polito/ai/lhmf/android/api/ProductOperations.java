package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.ProductCategory;
import android.net.Uri;

public interface ProductOperations {
	ProductCategory[] getProductCategories();
	
	Integer newProductCategory(String description);
	
	Integer newProduct(String productName, String productDescription, String productDimension, String measureUnit,
			String unitBlock, String transportCost, String unitCost, String minBuy, String maxBuy, ProductCategory category);
	
	Integer newProduct(String productName, String productDescription, String productDimension, String measureUnit,
			String unitBlock, String transportCost, String unitCost, String minBuy, String maxBuy, ProductCategory category, Uri fileUri);
	
	Product getProduct(Integer idProduct);
	
	byte[] getProductImage(String url);
	
	Product[] getMyProducts();
	
	//Product[] getProducts();
	
	Integer setProductAvailable(Integer idProduct);
	
	Integer setProductUnavailable(Integer idProduct);
}
