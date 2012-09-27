package it.polito.ai.lhmf.controllers.android;

import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.orm.Product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AndroidOrderController {
	@Autowired
	private OrderInterface orderInterface;
	
	@RequestMapping(value = "/androidApi/getorderproducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getOrderProducts(HttpServletRequest request, @RequestParam(value="idOrder", required=true) Integer idOrder)
	{
		List<Product> productsList = null;
		productsList = orderInterface.getProducts(idOrder);
		return productsList;
	}
	
	List<Integer> getBoughtAmounts(HttpServletRequest request, @RequestParam(value="idOrder", required=true) Integer idOrder,
			 @RequestParam(value="productIds", required=true) Integer[] productIds){
		// TODO retrieve bought amounts!!! idOrder è l'ordine, productIds sono gli id dei prodotti di quell'ordine di cui si vuole ottenere la quantità acquistata
		// Prendere tutte le schede dell'ordine, guardare se contengono i prodotti, e aumentare la quantità acquistata dei singoli prodotti, messa in un array nello stesso
		// ordine degli id.
		return  null;
	}
}