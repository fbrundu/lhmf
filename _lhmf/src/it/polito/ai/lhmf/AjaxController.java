package it.polito.ai.lhmf;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.LogInterface;
import it.polito.ai.lhmf.model.ProductCategoryInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Log;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.ProductCategory;
import it.polito.ai.lhmf.orm.Purchase;

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
	
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private PurchaseInterface purchaseInterface;
	
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
		try 
		{
			idOrder = orderInterface.newOrder(order);
		}
		catch (InvalidParametersException e) 
		{
			e.printStackTrace();
		}
		return idOrder;
	}
	
	@RequestMapping(value = "/getpastorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getPastOrders(HttpServletRequest request)
	{
		List<Order> orderList = null;
		orderList = orderInterface.getPastOrders(); 
		return orderList;
	}
	
	@RequestMapping(value = "/getactiveorders", method = RequestMethod.GET)
	public @ResponseBody
	List<Order> getActiveOrders(HttpServletRequest request)
	{
		List<Order> orderList = null;
		orderList = orderInterface.getActiveOrders(); 
		return orderList;
	}
	
	@RequestMapping(value = "/newpurchase", method = RequestMethod.POST)
	public @ResponseBody
	Integer newPurchase(HttpServletRequest request, @RequestBody Purchase purchase)
	{
		Integer idPurchase = -1;
		try 
		{
			idPurchase = purchaseInterface.newPurchase(purchase);
		}
		catch (InvalidParametersException e) 
		{
			e.printStackTrace();
		}
		return idPurchase;
	}
	/*
	@RequestMapping(value = "/getpastpurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getPastPurchase(HttpServletRequest request)
	{
		List<Purchase> purchaseList = null;
		purchaseList = purchaseInterface.getPastPurchase(); 
		return purchaseList;
	}
	
	@RequestMapping(value = "/getactivepurchase", method = RequestMethod.GET)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request)
	{
		List<Purchase> purchaseList = null;
		purchaseList = purchaseIterface.getActivePurchase(); 
		return purchaseList;
	}
	*/
}
