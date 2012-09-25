package it.polito.ai.lhmf.android.api.impl;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.ProductOperations;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.ProductCategory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.net.Uri;

public class ProductsTemplate implements ProductOperations {
	private RestTemplate template;

	public ProductsTemplate(RestTemplate restTemplate) {
		template = restTemplate;
	}

	@Override
	public ProductCategory[] getProductCategories() {
		try {
			ProductCategory[] res = template.getForObject(URI.create(Gas.baseApiUrl + "getproductcategories"), ProductCategory[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer newProductCategory(String description) {
		Integer newCategoryId = -1;
		
		MultiValueMap<String, String> value = new LinkedMultiValueMap<String, String>();
		value.add("description", description);
		
		try {
			newCategoryId = template.postForObject(URI.create(Gas.baseApiUrl + "newproductcategory"), value, Integer.class);
			return newCategoryId;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
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
		
		// Controllo solo se sono impostati. I controlli sugli eventuali valori sono già stati fatti prima di chiamare la funzione
		if(minBuy != null && !minBuy.equals(""))
			value.add("minBuy", minBuy);
		if(maxBuy != null && !maxBuy.equals(""))
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
				return null;
			} catch(RestClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		else{
			try {
				newProductId = template.postForObject(URI.create(Gas.baseApiUrl + "newproduct"), value, Integer.class);
			} catch (RestClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		return newProductId;
	}

	@Override
	public Product getProduct(Integer idProduct) {
		if(idProduct != null){
			try {
				Product ret = template.getForObject(Gas.baseApiUrl + "getproduct?idProduct={id}", Product.class, idProduct);
			
				return ret;
			} catch(RestClientException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public byte[] getProductImage(String url) {
		if(url != null){
			try {
				byte[] ret = template.getForObject(Gas.baseUrl + url, byte[].class);
				return ret;
			} catch (RestClientException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Product[] getMyProducts() {
		try {
			Product[] res = template.getForObject(URI.create(Gas.baseApiUrl + "getmyproducts"), Product[].class);
			return res;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer setProductAvailable(Integer idProduct) {
		try {
			Integer ret = template.getForObject(Gas.baseApiUrl + "setproductavailable?idProduct={id}", Integer.class, idProduct);
			if(ret == -1)
				return null;
			else
				return ret;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer setProductUnavailable(Integer idProduct) {
		try {
			Integer ret = template.getForObject(Gas.baseApiUrl + "setproductunavailable?idProduct={id}", Integer.class, idProduct);
			if(ret == -1)
				return null;
			else
				return ret;
		} catch(RestClientException e){
			e.printStackTrace();
			return null;
		}
	}
}
