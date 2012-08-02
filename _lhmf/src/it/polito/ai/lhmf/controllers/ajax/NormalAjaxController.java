package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NormalAjaxController
{
	@Autowired
	private OrderInterface orderInterface;
	
	@Autowired
	private PurchaseInterface purchaseInterface;
	
	@Autowired
	private MemberInterface memberInterface;
	
	@Autowired
	private ProductInterface productInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getActiveOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{		
		List<Order> listOrder = null;
		listOrder = orderInterface.getOrdersNow();
		return listOrder;
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getActivePurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getActivePurchase(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberNormal = memberInterface.getMember(username);
		List<Order> orderTmp = null;
		orderTmp = orderInterface.getOrdersNow();
		List<Purchase> listPurchase = null;
		listPurchase = purchaseInterface.getPurchasesOnDate(memberNormal.getIdMember(), orderTmp); 
		return listPurchase;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getOldPurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getOldPurchase(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberNormal = memberInterface.getMember(username);
		List<Order> orderTmp = null;
		orderTmp = orderInterface.getOrdersPast();
		List<Purchase> listPurchase = null;
		listPurchase = purchaseInterface.getPurchasesOnDate(memberNormal.getIdMember(), orderTmp); 
		return listPurchase;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getProductListFromOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder) throws InvalidParametersException
	{
		Order order = orderInterface.getOrder(idOrder);
		List<Product> listProduct = new ArrayList<Product>(order.getProducts());
		return listProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/getPurchaseDetails", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseDetails(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		List<PurchaseProduct> productTmp = purchaseInterface.getPurchaseProduct(idPurchase);
		List<Product> listProduct = new ArrayList<Product>();
		for(PurchaseProduct product : productTmp)
		{
			listProduct.add(productInterface.getProduct(product.getId().getIdProduct()));
		}
		return listProduct;
	}
	
}
