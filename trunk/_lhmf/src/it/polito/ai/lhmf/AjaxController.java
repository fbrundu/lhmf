package it.polito.ai.lhmf;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import it.polito.ai.lhmf.model.*;
import it.polito.ai.lhmf.orm.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ajax")
public class AjaxController
{
	@RequestMapping(value = "/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(HttpServletRequest request, @RequestBody Product product)
	{
		Integer idProduct = -1;
		try
		{
			idProduct = HibernateInterface.newProduct(
					(org.hibernate.Session) request
							.getAttribute("hibernate_session"), product);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return idProduct;
	}

	@RequestMapping(value = "/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		try
		{
			productsList = HibernateInterface
					.getProducts((org.hibernate.Session) request
							.getAttribute("hibernate_session"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return productsList;
	}

	@RequestMapping(value = "/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
	{
		Integer idProductCategory = -1;
		try
		{
			idProductCategory = HibernateInterface
					.newProductCategory((org.hibernate.Session) request
							.getAttribute("hibernate_session"), productCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return idProductCategory;
	}

	@RequestMapping(value = "/getproductcategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategories(HttpServletRequest request)
	{
		List<ProductCategory> productCategoriesList = null;
		try
		{
			productCategoriesList = HibernateInterface
					.getProductCategories((org.hibernate.Session) request
							.getAttribute("hibernate_session"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return productCategoriesList;
	}

	@RequestMapping(value = "/deleteproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
	{
		Integer rowsAffected = -1;
		try
		{
			rowsAffected = HibernateInterface.deleteProductCategory(
					(org.hibernate.Session) request
							.getAttribute("hibernate_session"),
					idProductCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return rowsAffected;
	}
}
