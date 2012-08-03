package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.ProductOperations;
import it.polito.ai.lhmf.model.ProductCategory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

public class ProductsTemplate implements ProductOperations {
	private RestTemplate template;

	public ProductsTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}

	@Override
	public ProductCategory[] getProductCategories() {
		ProductCategory[] res = template.getForObject(URI.create(Gas.baseApiUrl + "getproductcategories"), ProductCategory[].class);
		return res;
	}

	@Override
	public Integer newProductCategory(String description) {
		Integer newCategoryId = -1;
		
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("description", description);
		
		newCategoryId = template.postForObject(URI.create(Gas.baseApiUrl + "newproductcategory"), value, Integer.class);
		return newCategoryId;
	}

	@Override
	public Integer newProduct(String productName, String productDescription,
			String productDimension, String measureUnit, String unitBlock,
			String transportCost, String unitCost, String minBuy,
			String maxBuy, ProductCategory category) {
		return newProduct(productName, productDescription, productDimension, measureUnit,
				unitBlock, transportCost, unitCost, minBuy, maxBuy, category, null);
	}

	@Override
	public Integer newProduct(String productName, String productDescription,
			String productDimension, String measureUnit, String unitBlock,
			String transportCost, String unitCost, String minBuy,
			String maxBuy, ProductCategory category, Uri pictureUri) {
		Integer newProductId = -1;
		
		MultiValueMap<String, Object> value = new LinkedMultiValueMap<String, Object>();
		value.add("productName", productName);
		value.add("productDescription", productDescription);
		value.add("productDimension", productDimension);
		value.add("measureUnit", measureUnit);
		value.add("unitBlock", unitBlock);
		value.add("transportCost", transportCost);
		value.add("unitCost", unitCost);
		value.add("minBuy", minBuy);
		value.add("maxBuy", maxBuy);
		value.add("productCategory", Integer.toString(category.getIdProductCategory()));
		if(pictureUri != null){
			File f;
			try {
				f = new File(new URI(pictureUri.toString()));
				Resource picture = new FileSystemResource(f);
				value.add("picture", picture);
				newProductId = template.postForObject(URI.create(Gas.baseApiUrl + "newproductwithpicture"), value, Integer.class);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		else
			newProductId = template.postForObject(URI.create(Gas.baseApiUrl + "newproduct"), value, Integer.class);
		return newProductId;
	}
}
