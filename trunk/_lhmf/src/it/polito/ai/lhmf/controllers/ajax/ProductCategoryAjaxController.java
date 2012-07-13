package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.orm.ProductCategory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductCategoryAjaxController
{
	@Autowired
	private ProductCategoryInterface productCategoryInterface;

	@RequestMapping(value = "/ajax/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
			throws InvalidParametersException
	{
		Integer idProductCategory = -1;
		idProductCategory = productCategoryInterface
				.newProductCategory(productCategory);
		return idProductCategory;
	}

	@RequestMapping(value = "/ajax/getproductcategory", method = RequestMethod.GET)
	public @ResponseBody
	ProductCategory getProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
			throws InvalidParametersException
	{
		ProductCategory productCategory = null;
		productCategory = productCategoryInterface
				.getProductCategory(idProductCategory);
		return productCategory;
	}

	@RequestMapping(value = "/ajax/getproductcategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategories(HttpServletRequest request)
	{
		List<ProductCategory> productCategoriesList = null;
		productCategoriesList = productCategoryInterface.getProductCategories();
		return productCategoriesList;
	}

	@RequestMapping(value = "/ajax/updateproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = productCategoryInterface
				.updateProductCategory(productCategory);
		return rowsAffected;
	}

	@RequestMapping(value = "/ajax/deleteproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = productCategoryInterface
				.deleteProductCategory(idProductCategory);
		return rowsAffected;
	}
}
