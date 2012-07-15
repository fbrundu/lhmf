package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductAjaxController
{
	@Autowired
	private ProductInterface productInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(HttpServletRequest request, @RequestBody Product product)
			throws InvalidParametersException
	{
		Integer idProduct = -1;
		idProduct = productInterface.newProduct(product);
		return idProduct;
	}

	@RequestMapping(value = "/ajax/getproduct", method = RequestMethod.GET)
	public @ResponseBody
	Product getProduct(HttpServletRequest request,
			@RequestBody Integer idProduct)
	{
		Product product = null;
		product = productInterface.getProduct(idProduct);
		return product;
	}

	@RequestMapping(value = "/ajax/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		productsList = productInterface.getProducts();
		return productsList;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getmyproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getMyProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		productsList = productInterface.getProductsBySupplier((String) request
				.getSession().getAttribute("username"));
		return productsList;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/updateproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateProduct(HttpServletRequest request,
			@RequestBody Product product) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = productInterface.updateProduct(product);
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/deleteproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteProduct(HttpServletRequest request,
			@RequestBody Integer idProduct) throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		rowsAffected = productInterface.deleteProduct(idProduct);
		return rowsAffected;
	}

}
