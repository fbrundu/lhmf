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
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.PurchaseProductId;
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
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session	) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);

		return orderInterface.getActiveOrders(memberResp);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOldOrderResp(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "end") long end,
			@RequestParam(value = "dateDeliveryType") int dateDeliveryType) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		return orderInterface.getOldOrders(memberResp, end, dateDeliveryType);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDeliveredOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getDeliveredOrderResp(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		List<Order> listOrder = null;
		listOrder = orderInterface.getOrdersToDelivery(memberResp);
		
		return listOrder;
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.RESP + ","
			+ MyUserDetailsService.UserRoles.SUPPLIER +"')")
	@RequestMapping(value = "/ajax/getProductListFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromOrder(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "idOrder") int idOrder)
			throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		Member member = memberInterface.getMember(username);
		Order order = orderInterface.getOrder(idOrder);
		List<Product> listProduct = null;
		
		if (order.getSupplier().getIdMember() == member.getIdMember()
				|| order.getMember().getIdMember() == member.getIdMember())
			listProduct = orderInterface.getProducts(order.getIdOrder());

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
	@RequestMapping(value = "/ajax/getCompleteOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getCompleteOrderResp(HttpServletRequest request, HttpSession session	) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		List<Order> listOrder = orderInterface.getCompletedOrders(username);

		return listOrder;
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
	
	@RequestMapping(value = "/ajax/getSupplierByResp", method = RequestMethod.POST)
	public @ResponseBody
	Set<Supplier> getSupplierByResp(HttpServletRequest request, HttpSession session)
	{
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		Set<Supplier> respSupp = new HashSet<Supplier>();
		
		respSupp = memberResp.getSuppliersForIdMemberResp();
		
		return respSupp;
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
		List<Integer> productIds = new ArrayList<Integer>();
		String username = (String) session.getAttribute("username");
		Member resp = memberInterface.getMember(username);
		
		if(resp == null)
			return -1;
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		Date dateOpen = calendar.getTime();
		
		Date dateClose = new Date(dataCloseTime);
		
		String[] temp = idString.split(",");
		if(temp.length > 0){
			for(int i = 0; i < temp.length; i++){
				try {
					Integer id = Integer.valueOf(temp[i]);
					productIds.add(id);
				} catch(NumberFormatException e){
					e.printStackTrace();
					throw new InvalidParametersException();
				}
				
			}
			return orderInterface.createOrder(resp, idSupplier, productIds, orderName, dateOpen, dateClose);
		}
		return -1;
		
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOrdersStringNormal", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> getOrdersString(HttpServletRequest request, HttpSession session)
	{
		ArrayList<String> orderString = new ArrayList<String>();
		
		List<Order> listOrders = new ArrayList<Order>();
		listOrders = orderInterface.getOrdersNow();
		
		for (Order or : listOrders) 
		{			
			String temp = or.getIdOrder() + ", " + or.getOrderName() + " - " + or.getDateClose() + "," + or.getDateDelivery();					
			orderString.add(temp);		
		}		
		return orderString;
	}
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromOrderNormal(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrderNorm") int idOrder) throws InvalidParametersException
	{
		List<Product> ret = null;
		ret = orderInterface.getProducts(idOrder);
		return ret;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActivePurchaseNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getActivePurchaseNormal(HttpServletRequest request,
			HttpSession session) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), 0);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setNewPurchaseNorm", method = RequestMethod.POST)
	public @ResponseBody
	Integer setNewPurchase(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrderNorm") int idOrder,
			@RequestParam(value = "idProducts") String idProducts,
			@RequestParam(value = "amountProducts") String amountProduct) throws InvalidParametersException, ParseException
	{
		int result = -1;
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		String[] idTmp = idProducts.split(",");
		String[] amountTmp = amountProduct.split(",");
		for( int i = 0; i < idTmp.length; i++) 
		{
			Product product = poductInterface.getProduct(Integer.parseInt(idTmp[i]));
			if((Integer.parseInt(amountTmp[i]) > product.getMaxBuy()) || (Integer.parseInt(amountTmp[i])) <= 0)
			{
				return -2;
			}
		}
		Order order = orderInterface.getOrder(idOrder);
		
		Purchase purchase = new Purchase(order, memberNormal);
		
		if((result = purchaseInterface.newPurchase(purchase)) <= 0)
		{
			return result;
		}
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(calendar.getTime());
		Date insertedTimestamp = dateFormat.parse(sDate);
		
		for( int i = 0; i < idTmp.length; i++) 
		{
			Product product = poductInterface.getProduct(Integer.parseInt(idTmp[i]));
			PurchaseProductId id = new PurchaseProductId(purchase.getIdPurchase(), Integer.parseInt(idTmp[i]));
			PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase, product, Integer.parseInt(amountTmp[i]), insertedTimestamp);				
			//Non faccio check sul valore di ritorno. In questo caso, dato che l'id non � generato ma gi� passato, se ci sono errori lancia un'eccezione
			purchaseInterface.newPurchaseProduct(purchaseproduct);
		}		
		return 1;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseDetailsNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseDetails(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase) throws InvalidParametersException
	{
		List<PurchaseProduct> productTmp = purchaseInterface.getPurchaseProduct(idPurchase);
		List<Product> listProduct = new ArrayList<Product>();
		for(PurchaseProduct product : productTmp)
		{
			listProduct.add(poductInterface.getProduct(product.getId().getIdProduct()));
		}
		return listProduct;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldPurchaseNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getOldPurchase(HttpServletRequest request,
			HttpSession session) throws InvalidParametersException
	{
		return purchaseInterface.getPurchasesOnDate(
				(String) session.getAttribute("username"), -1);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getAmountfromPurchaseNorm", method = RequestMethod.POST)
	public @ResponseBody
	Integer getAmountfromPurchaseNorm(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct) throws InvalidParametersException
	{
		PurchaseProduct tmpPP = null;
		tmpPP = purchaseInterface.getPurchaseProductFromId(idPurchase,idProduct);
		return tmpPP.getAmount();
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProgressOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	Float getProgressOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder		) throws InvalidParametersException
	{
		return orderInterface.getProgress(idOrder);
	}
}
