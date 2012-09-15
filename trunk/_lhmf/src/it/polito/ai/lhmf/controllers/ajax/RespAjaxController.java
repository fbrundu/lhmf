package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class RespAjaxController
{
	@Autowired
	private OrderInterface orderInterface;
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private SupplierInterface supplierInterface;
	@Autowired
	private PurchaseInterface purchaseInterface;
	@Autowired
	private ProductInterface poductInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActiveOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start") long start) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		listOrder = orderInterface.getActiveOrders(start, memberResp);
		return listOrder;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOldOrderResp(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end,
			@RequestParam(value = "dateDeliveryType") int dateDeliveryType) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		
		if(dateDeliveryType == 2) {
			//Ritornare tutti gli ordini passati
			listOrder = orderInterface.getOldOrders(memberResp, start, end);
		} else {
			 boolean settedDeliveryDate = false;
			 if (dateDeliveryType == 0)
				 settedDeliveryDate = true;
			 
			 listOrder = orderInterface.getOldOrders(memberResp, start, end, settedDeliveryDate);
			 
		}
		
		return listOrder;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDeliveredOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getDeliveredOrderResp(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "start") long start,
			@RequestParam(value = "end") long end) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		listOrder = orderInterface.getOrdersToDelivery(memberResp, start, end);
		
		return listOrder;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductListFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder) throws InvalidParametersException
	{
		Order order = orderInterface.getOrder(idOrder);
		List<Product> listProduct = new ArrayList<Product>(order.getProducts());

		return listProduct;
	}

	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setDeliveryDate", method = RequestMethod.POST)
	public @ResponseBody
	Integer setDeliveryDate(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "dateDelivery") long dateDelivery) throws InvalidParametersException
	{
		
		Order order = orderInterface.getOrder(idOrder);
		Date shipDate = new Date(dateDelivery);
		
		order.setDateDelivery(shipDate);
		
		return orderInterface.updateOrder(order);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getPurchaseFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder) throws InvalidParametersException
	{
		Order order = orderInterface.getOrder(idOrder);
		List<Purchase> listPurchase = new ArrayList<Purchase>(order.getPurchases());

		return listPurchase;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseProducts", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseProducts(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		List<Product> listProducts = purchaseInterface.getProducts(idPurchase);
		return listProducts;
	}
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseAmount", method = RequestMethod.POST)
	public @ResponseBody
	Integer getPurchaseAmount(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct) throws InvalidParametersException
	{
		return purchaseInterface.getAmount(idPurchase, idProduct);
	}
	
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setShip", method = RequestMethod.POST)
	public @ResponseBody
	Integer getPurchaseAmount(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		Purchase purchase = purchaseInterface.getPurchase(idPurchase);
		purchase.setIsShipped(true);
		return purchaseInterface.updatePurchase(purchase);
	}
	
	
	
	@RequestMapping(value = "/ajax/getMembersSupplierString", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> getMembersSupplierString(HttpServletRequest request, HttpSession session)
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		ArrayList<String> respString = new ArrayList<String>();
		
		List<Member> listSupplier = new ArrayList<Member>();
		listSupplier = memberInterface.getMembersSupplier();
		
		for (Member m : listSupplier) {
		  
			Supplier s = supplierInterface.getSupplier(m.getIdMember());
			if(s.getMemberByIdMemberResp().getIdMember() == memberResp.getIdMember()) {
				String temp = m.getIdMember() + "," + m.getName() + " " + m.getSurname() + "," + s.getCompanyName();
				respString.add(temp);	
			}
		}
		
		return respString;
	}
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromSupplier", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromSupplier(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier") int idSupplier) throws InvalidParametersException
	{
		
		Member supplier = memberInterface.getMember(idSupplier);
		
		List<Product> listProduct = poductInterface.getProductsBySupplier(supplier);
		
		return listProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setNewOrder", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier") int idSupplier,
			@RequestParam(value = "orderName") String orderName,
			@RequestParam(value = "idString") String idString,
			@RequestParam(value = "dataCloseTime") long dataCloseTime) throws InvalidParametersException, ParseException
	{
		Supplier supplier = supplierInterface.getSupplier(idSupplier);
		Member Resp = memberInterface.getMember((String) session.getAttribute("username"));
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(calendar.getTime());
		Date dateOpen = dateFormat.parse(sDate);
		Date dateClose = new Date(dataCloseTime);
			
		Order order = new Order(supplier, Resp, orderName, dateOpen, dateClose);
		
		int result = -1;
	
		String[] temp = idString.split(",");
		Set<Product> setProduct = new HashSet<Product>();
		
		for(String element:temp) 
			setProduct.add(poductInterface.getProduct(Integer.parseInt(element)));
		
		order.setProducts(setProduct);
		
		result = orderInterface.newOrder(order);
		
		return result;
		
	}
	
}