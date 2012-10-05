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
	private ProductInterface productInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getActiveOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getActiveOrder(HttpServletRequest request, HttpSession session)
	{
		try
		{
			return orderInterface.getActiveOrders((String) session
					.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getOldOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getOldOrderResp(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "end") long end,
			@RequestParam(value = "dateDeliveryType") int dateDeliveryType)
	{
		try
		{
			return orderInterface.getOldOrders(
					(String) session.getAttribute("username"), end,
					dateDeliveryType);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getDeliveredOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getDeliveredOrderResp(HttpServletRequest request,
			HttpSession session)
	{
		try
		{
			return orderInterface.getOrdersToDelivery((String) session
					.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.RESP + ","
			+ MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/getProductListFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromOrder(HttpServletRequest request,
			HttpSession session, @RequestParam(value = "idOrder") int idOrder)
	{
		try
		{
			return orderInterface.getProducts(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductListFromPurchase", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductListFromPurchase(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.getProducts(idPurchase,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setDeliveryDate", method = RequestMethod.POST)
	public @ResponseBody
	Integer setDeliveryDate(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "dateDelivery") long dateDelivery)
	{
		try
		{
			return orderInterface.setDeliveryDate(idOrder, (String) session.getAttribute("username"), new Date(dateDelivery));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getCompleteOrderResp", method = RequestMethod.POST)
	public @ResponseBody
	List<Order> getCompleteOrderResp(HttpServletRequest request,
			HttpSession session)
	{
		return orderInterface.getCompletedOrders((String) session
				.getAttribute("username"));
	}

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseFromOrder", method = RequestMethod.POST)
	public @ResponseBody
	List<Purchase> getPurchaseFromOrder(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder)
	{
		try
		{
			return orderInterface.getPurchasesFromOrder(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseProducts", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getPurchaseProducts(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.getProducts(idPurchase,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getPurchaseAmount", method = RequestMethod.POST)
	public @ResponseBody
	Integer getPurchaseAmount(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase,
			@RequestParam(value = "idProduct") int idProduct)
	{
		return purchaseInterface.getAmount(idPurchase, idProduct);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/setShip", method = RequestMethod.POST)
	public @ResponseBody
	Integer setPurchaseShipped(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase)
	{
		try
		{
			return purchaseInterface.setPurchaseShipped(
					(String) session.getAttribute("username"), idPurchase);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	@RequestMapping(value = "/ajax/getMembersSupplierString", method = RequestMethod.POST)
	public @ResponseBody
	List<String> getMembersSupplierString(HttpServletRequest request,
			HttpSession session)
	{
		return memberInterface.getMembersSupplierString((String) session
				.getAttribute("username"));
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
		
		List<Product> listProduct = productInterface.getProductsBySupplier(supplier);
		
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
	
	//FIXME : serve agli utenti normal o resp??
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getProductFromOrderNormal", method = RequestMethod.POST)
	public @ResponseBody
	List<Product> getProductFromOrderNormal(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "idOrderNorm") int idOrder)
	{
		try
		{
			return orderInterface.getProducts(idOrder,
					(String) session.getAttribute("username"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
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
		
		String[] idTmp = idProducts.split(",");
		String[] amountTmp = amountProduct.split(",");
		for( int i = 0; i < idTmp.length; i++) 
		{
			Product product = productInterface.getProduct(
					Integer.parseInt(idTmp[i]),
					(String) session.getAttribute("username"));
			if ((Integer.parseInt(amountTmp[i]) > product.getMaxBuy())
					|| (Integer.parseInt(amountTmp[i])) <= 0)
			{
				return -2;
			}
		}
		Purchase purchase = new Purchase();
		// FIXME testare se funziona
		if ((result = purchaseInterface.newPurchase(purchase,
				(String) session.getAttribute("username"), idOrder)) <= 0)
			return result;
		
		// setto la data odierna
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(calendar.getTime());
		Date insertedTimestamp = dateFormat.parse(sDate);
		
		for (int i = 0; i < idTmp.length; i++)
		{
			Product product = productInterface.getProduct(
					Integer.parseInt(idTmp[i]),
					(String) session.getAttribute("username"));
			PurchaseProductId id = new PurchaseProductId(
					purchase.getIdPurchase(), Integer.parseInt(idTmp[i]));
			PurchaseProduct purchaseproduct = new PurchaseProduct(id, purchase,
					product, Integer.parseInt(amountTmp[i]), insertedTimestamp);
			// Non faccio check sul valore di ritorno. In questo caso, dato che
			// l'id non e' generato ma gia' passato, se ci sono errori lancia
			// un'eccezione
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
		List<PurchaseProduct> productTmp = purchaseInterface.getPurchaseProducts(idPurchase);
		List<Product> listProduct = new ArrayList<Product>();
		for (PurchaseProduct product : productTmp)
		{
			listProduct
					.add(productInterface.getProduct(product.getId()
							.getIdProduct(), (String) session
							.getAttribute("username")));
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

	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getTotBought", method = RequestMethod.POST)
	public @ResponseBody
	List<Integer> getTotBought(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idOrder") int idOrder,
			@RequestParam(value = "idProducts") List<Integer> idProducts	) throws InvalidParametersException
	{
		return orderInterface.getBoughtAmounts(idOrder, idProducts);
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/getTotPurchaseCost", method = RequestMethod.POST)
	public @ResponseBody
	Float getTotPurchaseCost(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idPurchase") int idPurchase	) throws InvalidParametersException
	{
		String username = (String) session.getAttribute("username");
		
		return purchaseInterface.getPurchaseCost(username, idPurchase, true);
	}
}
