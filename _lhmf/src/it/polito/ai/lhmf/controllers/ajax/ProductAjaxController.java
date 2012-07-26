package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Supplier;
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
public class ProductAjaxController
{
	@Autowired
	private ProductInterface productInterface;

	@Autowired
	private ProductCategoryInterface productCategoryInterface;

	@Autowired
	private SupplierInterface supplierInterface;

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(
			HttpServletRequest request,
			@RequestParam(value = "productName", required = true) String productName,
			@RequestParam(value = "productDescription", required = true) String productDescription,
			@RequestParam(value = "productDimension", required = true) int productDimension,
			@RequestParam(value = "measureUnit", required = true) String measureUnit,
			@RequestParam(value = "unitBlock", required = true) int unitBlock,
			@RequestParam(value = "transportCost", required = true) float transportCost,
			@RequestParam(value = "unitCost", required = true) float unitCost,
			@RequestParam(value = "minBuy", required = true) int minBuy,
			@RequestParam(value = "maxBuy", required = true) int maxBuy,
			@RequestParam(value = "productCategory", required = true) int idProductCategory)
			throws InvalidParametersException
	{
		Integer idProduct = -1;
		Supplier s = supplierInterface.getSupplier((String) request
				.getSession().getAttribute("username"));
		ProductCategory pc = productCategoryInterface
				.getProductCategory(idProductCategory);
		if (s != null && pc != null && !productName.equals("")
				&& !productDescription.equals("") && productDimension > 0
				&& !measureUnit.equals("") && unitBlock > 0
				&& transportCost > 0 && unitCost > 0 && minBuy > 0
				&& maxBuy >= minBuy)
		{
			Product p = new Product();
			p.setName(productName);
			p.setDescription(productDescription);
			p.setDimension(productDimension);
			p.setMeasureUnit(measureUnit);
			p.setUnitBlock(unitBlock);
			p.setTransportCost(transportCost);
			p.setUnitCost(unitCost);
			p.setMinBuy(minBuy);
			p.setMaxBuy(maxBuy);
			p.setAvailability(false);
			p.setSupplier(s);
			p.setProductCategory(pc);
			idProduct = productInterface.newProduct(p);
		}
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
	@RequestMapping(value = "/ajax/setproductavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductAvailable(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		if (idProduct != null && idProduct > 0)
		{
			Supplier s = supplierInterface.getSupplier((String) request
					.getSession().getAttribute("username"));
			Product p = productInterface.getProduct(idProduct);

			if (s != null && p != null
					&& p.getSupplier().getIdMember() == s.getIdMember())
				rowsAffected = productInterface.setProductAvailable(idProduct);
		}
		return rowsAffected;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/setproductunavailable", method = RequestMethod.GET)
	public @ResponseBody
	Integer setProductUnavailable(
			HttpServletRequest request,
			@RequestParam(value = "idProduct", required = true) Integer idProduct)
			throws InvalidParametersException
	{
		Integer rowsAffected = -1;
		if (idProduct != null && idProduct > 0)
		{
			Supplier s = supplierInterface.getSupplier((String) request
					.getSession().getAttribute("username"));
			Product p = productInterface.getProduct(idProduct);

			if (s != null && p != null
					&& p.getSupplier().getIdMember() == s.getIdMember())
				rowsAffected = productInterface.setProductUnavailable(idProduct);
		}
		return rowsAffected;
	}
	
	// TODO il prodotto può essere aggiornato solo dal suo supplier
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

	// TODO il prodotto può essere cancellato solo dal suo supplier
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
