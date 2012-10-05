package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.model.StatisticsInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
public class StatisticsAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private SupplierInterface supplierInterface;
	@Autowired
	private OrderInterface orderInterface;
	@Autowired
	private ProductInterface productInterface;
	@Autowired
	private PurchaseInterface purchaseInterface;
	@Autowired
	private StatisticsInterface statisticsInterface;
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/statMemberType", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statMemberType(HttpServletRequest request)
	{
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		Float normalUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_NORMAL);
		Float respUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_RESP);
		Float supplierUsers = (float) (long) memberInterface.getNumberItems(MemberTypes.USER_SUPPLIER);
		Float totalUsers = (float) (long) memberInterface.getNumberItems(4)-1;
		
		normalUsers = (float) (normalUsers/totalUsers)*100;
		respUsers = (respUsers/totalUsers)*100;
		supplierUsers = ((supplierUsers/totalUsers)*100);
		
		respStat.add(normalUsers);
		respStat.add(respUsers);
		respStat.add(supplierUsers);

		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/statMemberReg", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statMemberReg(HttpServletRequest request,
			@RequestParam(value = "year", required = true) int year)
	{
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		
		Integer normalUsers;
		Integer respUsers;
		Integer supplierUsers;
		
		for(int i = 0; i <= 11; i++) {
			
			normalUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_NORMAL, i, year);
			respUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_RESP, i, year);
			supplierUsers = (int) (long) memberInterface.getNumberItemsPerMonth(MemberTypes.USER_SUPPLIER, i, year);
			
			respStat.add(normalUsers);
			respStat.add(respUsers);
			respStat.add(supplierUsers);
		}
		
		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statSupplierMoneyMonth(HttpServletRequest request,
			HttpSession session,
			@RequestParam(value = "year", required = true) int year)
	{
		return statisticsInterface.getSupplierMoneyMonth(
				(String) session.getAttribute("username"), year);
	}
	
	@SuppressWarnings("rawtypes")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierMoneyProduct", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statSupplierMoneyProduct(HttpServletRequest request, HttpSession session
										) throws InvalidParametersException
	{
		
		ArrayList<String> respStatName = new ArrayList<String>();
		ArrayList<String> respStatFloat = new ArrayList<String>();
		
		// Mi ricavo il Supplier
		String username = (String) session.getAttribute("username");
		Supplier supplier = supplierInterface.getSupplier(username);
		Product tempProduct;
		Float tempAmount;
		
		// Mi ricavo la lista dei prodotti del Supplier con accanto l'amount totale ricavato dagli ordini chiusi
		Map<Product, Float> listProduct = productInterface.getProfitOnProducts(supplier);
		
		if(listProduct.size() > 0) {
			
			Iterator it = listProduct.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry) it.next();
		        tempProduct = (Product) pairs.getKey();
		        tempAmount = (Float) pairs.getValue();
		        		
		        respStatName.add(tempProduct.getName());
		        respStatFloat.add(tempAmount.toString());
		    }
		    
		    respStatName.addAll(respStatFloat);
			
		} else {
			respStatName.add("errNoProduct");
		}

		return respStatName;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierProductList", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statSupplierProductList(HttpServletRequest request, HttpSession session
										) throws InvalidParametersException
	{
		
		ArrayList<Double> respStat = new ArrayList<Double>();
		
		// Mi ricavo il Supplier
		String username = (String) session.getAttribute("username");
		Member memberSupplier = memberInterface.getMember(username);
		
		// Mi ricavo il numero dei prodotti totali 
		Double numberProductsTot = (double) (long) productInterface.getNumberOfProductsBySupplier(memberSupplier);
		
		// Mi ricavo il numero dei prodotti totali  in lista
		Double numberProductsOnList = (double) (long) productInterface.getNumberOfProductsOnListBySupplier(memberSupplier);
		
		if(numberProductsTot != 0) {
			Double temp = (numberProductsOnList/numberProductsTot)*100;
			respStat.add(temp);
			respStat.add(100-temp);
		} else {
			respStat.add((double) 0);
			respStat.add((double) 0);
		}
		
		return respStat;
	}
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierOrderMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statSupplierOrderMonth(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "year", required = true) int year	) throws InvalidParametersException
	{
		
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		ArrayList<Integer> respStatNotShipped = new ArrayList<Integer>();
		
		// Mi ricavo il Supplier
		String username = (String) session.getAttribute("username");
		Member memberSupplier = memberInterface.getMember(username);
		
		
		for(int i = 0; i < 12; i++) {
			
			// Mi ricavo il numero di ordini conclusi (con o senza data di consegna)
			Integer numberOrdersTot = (int) (long) orderInterface.getNumberOldOrdersBySupplier(memberSupplier, year, i);
			
			// Mi ricavo il numero di ordini conclusi (con data di consegna impostata)
			Integer numberOrderShipped = (int) (long) orderInterface.getNumberOldOrdersShippedBySupplier(memberSupplier, year, i);
			
			respStat.add(numberOrderShipped);
			respStatNotShipped.add(numberOrdersTot-numberOrderShipped);
			
		}
		
		respStat.addAll(respStatNotShipped);
		
		return respStat;
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.SUPPLIER + "')")
	@RequestMapping(value = "/ajax/statSupplierOrderYear", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statSupplierOrderYear(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "year", required = true) int year	) throws InvalidParametersException
	{
		
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		// Mi ricavo il Supplier
		String username = (String) session.getAttribute("username");
		Member memberSupplier = memberInterface.getMember(username);
		
		// Mi ricavo il numero di ordini conclusi (con o senza data di consegna)
		Float numberOrdersTot = (float) (long) orderInterface.getNumberOldOrdersBySupplier(memberSupplier, year);

		// Mi ricavo il numero di ordini conclusi (con data di consegna impostata
		Float numberOrderShipped = (float) (long) orderInterface.getNumberOldOrdersShippedBySupplier(memberSupplier, year);
				
		if(numberOrdersTot != 0) {
		Float temp = (numberOrderShipped/numberOrdersTot)*100;
		
		respStat.add(temp);
		respStat.add(100-temp);
		} else {
			respStat.add((float) 0);
			respStat.add((float) 0);
		}
		
		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespOrderMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Integer> statRespOrderMonth(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier", required = true) int idSupplier,
			@RequestParam(value = "year", required = true) int year				) throws InvalidParametersException
	{
		
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		ArrayList<Integer> respStatNotShipped = new ArrayList<Integer>();
		
		// Mi ricavo il Supplier
		Member memberSupplier = memberInterface.getMember(idSupplier);
		
		
		for(int i = 0; i < 12; i++) {
			
			// Mi ricavo il numero di ordini conclusi (con o senza data di consegna)
			Integer numberOrdersTot = (int) (long) orderInterface.getNumberOldOrdersBySupplier(memberSupplier, year, i);
			
			// Mi ricavo il numero di ordini conclusi (con data di consegna impostata)
			Integer numberOrderShipped = (int) (long) orderInterface.getNumberOldOrdersShippedBySupplier(memberSupplier, year, i);
			
			respStat.add(numberOrderShipped);
			respStatNotShipped.add(numberOrdersTot-numberOrderShipped);
			
		}
		
		respStat.addAll(respStatNotShipped);
		
		return respStat;
	}
		
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespOrderYear", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statRespOrderYear(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "idSupplier", required = true) int idSupplier,
			@RequestParam(value = "year", required = true) int year				) throws InvalidParametersException
	{
		
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		// Mi ricavo il Supplier
		Member memberSupplier = memberInterface.getMember(idSupplier);
		
		// Mi ricavo il numero di ordini conclusi (con o senza data di consegna)
		Float numberOrdersTot = (float) (long) orderInterface.getNumberOldOrdersBySupplier(memberSupplier, year);

		// Mi ricavo il numero di ordini conclusi (con data di consegna impostata
		Float numberOrderShipped = (float) (long) orderInterface.getNumberOldOrdersShippedBySupplier(memberSupplier, year);
				
		if(numberOrdersTot != 0) {
		Float temp = (numberOrderShipped/numberOrdersTot)*100;
		
		respStat.add(temp);
		respStat.add(100-temp);
		} else {
			respStat.add((float) 0);
			respStat.add((float) 0);
		}
		
		return respStat;
	}
	
	@SuppressWarnings("rawtypes")
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespTopUsers", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statRespTopUsers(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		
		ArrayList<String> respStatName = new ArrayList<String>();
		ArrayList<String> respStatAmount = new ArrayList<String>();
		
		// Mi ricavo il Responsabile
		String username = (String) session.getAttribute("username");
		Member memberResp = memberInterface.getMember(username);
		
		Member tempMember;
		Float tempAmount;
		
		//Mi ricavo la lista degli utenti pi� attivi
		Map<Member, Float> topUsers = memberInterface.getTopUsers(memberResp);
		
		if(topUsers != null) {
			
			Iterator it = topUsers.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry) it.next();
		        tempMember = (Member) pairs.getKey();
		        tempAmount = (Float) pairs.getValue();
		        		
		        respStatName.add(tempMember.getName() + " " + tempMember.getSurname());
		        respStatAmount.add(tempAmount.toString());
		    }
		    
		    respStatName.addAll(respStatAmount);
			
		} else {
			respStatName.add("errNoUser");
		}

		return respStatName;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statRespMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statRespMoneyMonth(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "year", required = true) int year) throws InvalidParametersException
	{
		
		ArrayList<Double> respStat = new ArrayList<Double>();
		ArrayList<Double> statPurchaseTemp = null;
		// Mi ricavo il Membro
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		for(int i = 0; i < 12; i++) {
			
			// Mi ricavo per ogni mese e la spesa totale e la spesaMedia
			statPurchaseTemp = purchaseInterface.getSumAndAvgOfPurchasePerMonth(memberNormal, year, i);
			
			//Aggiungo alla lista totale
			respStat.addAll(statPurchaseTemp);
			
		}
		
		return respStat;
	}
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.NORMAL + "')")
	@RequestMapping(value = "/ajax/statNormMoneyMonth", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Double> statNormMoneyMonth(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "year", required = true) int year) throws InvalidParametersException
	{
		
		ArrayList<Double> respStat = new ArrayList<Double>();
		ArrayList<Double> statPurchaseTemp = null;
		// Mi ricavo il Membro
		String username = (String) session.getAttribute("username");
		Member memberNormal = memberInterface.getMember(username);
		
		for(int i = 0; i < 12; i++) {
			
			// Mi ricavo per ogni mese e la spesa totale e la spesaMedia
			statPurchaseTemp = purchaseInterface.getSumAndAvgOfPurchasePerMonth(memberNormal, year, i);
			
			//Aggiungo alla lista totale
			respStat.addAll(statPurchaseTemp);
			
		}
		
		return respStat;
	}
	
	@SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('" + MyUserDetailsService.UserRoles.NORMAL
			+ ", " + MyUserDetailsService.UserRoles.RESP + "')")
	@RequestMapping(value = "/ajax/statProdTopProduct", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<String> statProdTopProduct(HttpServletRequest request, HttpSession session) throws InvalidParametersException
	{
		
		ArrayList<String> respStatName = new ArrayList<String>();
		ArrayList<String> respStatAmount = new ArrayList<String>();
		
		//Mi ricavo la lista dei prodotti pi� venduti
		Map<Product, Long> topProduct = productInterface.getTopProduct();
		
		Product tempProduct;
		Long tempAmount;
		
		if(topProduct.size() > 0) {
			
			Iterator it = topProduct.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry) it.next();
		        tempProduct = (Product) pairs.getKey();
		        tempAmount = (Long) pairs.getValue();
		        		
		        respStatName.add(tempProduct.getName());
		        respStatAmount.add(tempAmount.toString());
		    }
		    
		    respStatName.addAll(respStatAmount);
			
		} else {
			respStatName.add("errNoProduct");
		}

		return respStatName;
	}
	
}
