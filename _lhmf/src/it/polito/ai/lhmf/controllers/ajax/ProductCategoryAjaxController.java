package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductCategoryAjaxController
{
	@Autowired
	private ProductInterface productInterface;
	@Autowired
	private ProductCategoryInterface productCategoryInterface;

	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.SUPPLIER
			+ ", " + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(
			HttpServletRequest request,
			@RequestParam(value = "description", required = true) String description)
	{
		try
		{
			return productCategoryInterface.newProductCategory(description);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@RequestMapping(value = "/ajax/getproductcategory", method = RequestMethod.GET)
	public @ResponseBody
	ProductCategory getProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
	{
		try
		{
			return productCategoryInterface.getProductCategory(idProductCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/ajax/getproductcategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategories(HttpServletRequest request)
	{
		return productCategoryInterface.getProductCategories();
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmycategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getMyProductCategories(HttpServletRequest request)
	{
		try
		{
			return productInterface.getProductCategoriesBySupplier((String) request
					.getSession().getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/ajax/updateproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
	{
		try
		{
			return productCategoryInterface
					.updateProductCategory(productCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/ajax/deleteproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
	{
		try
		{
			return productCategoryInterface
					.deleteProductCategory(idProductCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
