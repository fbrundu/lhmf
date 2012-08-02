package it.polito.ai.lhmf.android.api;

import android.net.Uri;
import it.polito.ai.lhmf.model.ProductCategory;

public interface ProductOperations {
	ProductCategory[] getProductCategories();
	
	Integer newProductCategory(String description);
	
	Integer newProduct(String productName, String productDescription, Integer productDimension, String measureUnit,
			Integer unitBlock, Float transportCost, Float unitCost, Integer minBuy, Integer maxBuy, ProductCategory category);
	
	Integer newProduct(String productName, String productDescription, Integer productDimension, String measureUnit,
			Integer unitBlock, Float transportCost, Float unitCost, Integer minBuy, Integer maxBuy, ProductCategory category, Uri fileUri);
}
