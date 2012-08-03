package it.polito.ai.lhmf.android.api;

import android.net.Uri;
import it.polito.ai.lhmf.model.ProductCategory;

public interface ProductOperations {
	ProductCategory[] getProductCategories();
	
	Integer newProductCategory(String description);
	
	Integer newProduct(String productName, String productDescription, String productDimension, String measureUnit,
			String unitBlock, String transportCost, String unitCost, String minBuy, String maxBuy, ProductCategory category);
	
	Integer newProduct(String productName, String productDescription, String productDimension, String measureUnit,
			String unitBlock, String transportCost, String unitCost, String minBuy, String maxBuy, ProductCategory category, Uri fileUri);
}
