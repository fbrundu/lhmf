package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.orm.Product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping(value = "/ajax/newproduct", method = RequestMethod.POST)
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

	@RequestMapping(value = "/ajax/getproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(HttpServletRequest request)
	{
		List<Product> productsList = null;
		productsList = productInterface.getProducts();
		return productsList;
	}
}
