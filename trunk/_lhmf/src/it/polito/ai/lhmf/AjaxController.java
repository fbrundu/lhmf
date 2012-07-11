package it.polito.ai.lhmf;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.LogInterface;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.ProductCategory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax")
public class AjaxController
{
	@Autowired
	private LogInterface logInterface;
	
	@Autowired
	private ProductInterface productInterface;
	
	//private Order
	
	@Autowired
	private ProductCategoryInterface productCategoryInterface;
	
	@RequestMapping(value = "/getlogs", method = RequestMethod.GET)
	public @ResponseBody
	List<Log> getLogs(HttpServletRequest request,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end)
	{
		List<Log> logs = null;
		logs = logInterface.getLogs(start, end);
		return logs;
	}

	@RequestMapping(value = "/newproduct", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProduct(HttpServletRequest request, @RequestBody Product product)
	{
		Integer idProduct = -1;
		try {
			idProduct = productInterface.newProduct(
					product);
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idProduct;
	}

	@RequestMapping(value = "/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		productsList = productInterface.getProducts();
		return productsList;
	}

	@RequestMapping(value = "/newproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer newProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
	{
		Integer idProductCategory = -1;
		try {
			idProductCategory = productCategoryInterface
					.newProductCategory(productCategory);
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idProductCategory;
	}

	@RequestMapping(value = "/getproductcategories", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategories(HttpServletRequest request)
	{
		List<ProductCategory> productCategoriesList = null;
		productCategoriesList = productCategoryInterface.getProductCategories();
		return productCategoriesList;
	}

	@RequestMapping(value = "/updateproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer updateProductCategory(HttpServletRequest request,
			@RequestBody ProductCategory productCategory)
	{
		Integer rowsAffected = -1;
		try {
			rowsAffected = productCategoryInterface
					.updateProductCategory(productCategory);
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowsAffected;
	}

	@RequestMapping(value = "/deleteproductcategory", method = RequestMethod.POST)
	public @ResponseBody
	Integer deleteProductCategory(HttpServletRequest request,
			@RequestBody Integer idProductCategory)
	{
		Integer rowsAffected = -1;
		try {
			rowsAffected = productCategoryInterface.deleteProductCategory(idProductCategory);
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowsAffected;
	}
	
	@RequestMapping(value = "/neworder", method = RequestMethod.POST)
	public @ResponseBody
	Integer newOrder(HttpServletRequest request, @RequestBody Order order)
	{
		Integer idOrder = -1;
		try {
			idOrder = orderI
			
			idProduct = productInterface.newProduct(
					product);
		} catch (InvalidParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idOrder;
	}
}
