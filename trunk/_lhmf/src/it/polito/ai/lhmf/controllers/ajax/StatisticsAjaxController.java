package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.model.SupplierInterface;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.Supplier;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	
	
	@PreAuthorize("hasRole('" + MyUserDetailsService.UserRoles.ADMIN + "')")
	@RequestMapping(value = "/ajax/statMemberType", method = RequestMethod.POST)
	public @ResponseBody
	ArrayList<Float> statMemberType(HttpServletRequest request) throws InvalidParametersException
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
			@RequestParam(value = "year", required = true) int year) throws InvalidParametersException
	{
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		
		Integer normalUsers;
		Integer respUsers;
		Integer supplierUsers;
		
		for(int i = 1; i <= 12; i++) {
			
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
	ArrayList<Float> statSupplierMoneyMonth(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "year", required = true) int year) throws InvalidParametersException
	{
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		// Mi ricavo il membro Supplier
		String username = (String) session.getAttribute("username");
		Member memberSupplier = memberInterface.getMember(username);
		
		// Mi ricavo la lista degli ordini passati conclusi (con data consegna impostata)
		List<Order> listOrders = orderInterface.getOldOrdersShippedBySupplier(memberSupplier, year);
		
		// Inizializzo variabili
		Calendar cal = Calendar.getInstance();
		Float price = new Float(0);
		Set<Purchase> listPurchase;
		
		// Azzero struttura che servira per memorizzare gli incassi
		for(int i = 0; i < 12; i++)
			respStat.add(price);
		
		// Itero sugli ordini
		for (Order temp : listOrders) {
			
			// Per ogni ordine estraggo il mese 
			cal.setTime(temp.getDateClose());
			int month = cal.get(Calendar.MONTH);
			// Recupero il prezzo salvato
			price = respStat.get(month);
			//Azzero parziale
			float partial = 0;
			
			//Accedo alla lista delle schede
			listPurchase = temp.getPurchases();
			
			Set<PurchaseProduct> listPurchaseProduct;
			
			//Per ogni scheda d'acquisto mi ricavo il prodotto e relativa quantità
			for (Purchase tempPurchase : listPurchase) {
				
				listPurchaseProduct = tempPurchase.getPurchaseProducts();
				Product tempProduct;
				int tempAmount;
				
				for(PurchaseProduct tempPP : listPurchaseProduct) {
					tempProduct = tempPP.getProduct();
					tempAmount = tempPP.getAmount();
					
					partial += tempAmount*(tempProduct.getUnitBlock()*tempProduct.getUnitCost());
				
				} // Fine prodotti di una scheda
			} // Fine Schede di un ordine
	
			respStat.set(month, partial+price);
			
		} // fine Ordine di un responsabile
		
		
		return respStat;
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
	ArrayList<Float> statSupplierProductList(HttpServletRequest request, HttpSession session
										) throws InvalidParametersException
	{
		
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		// Mi ricavo il Supplier
		String username = (String) session.getAttribute("username");
		Member memberSupplier = memberInterface.getMember(username);
		
		// Mi ricavo il numero dei prodotti totali 
		Integer numberProductsTot = productInterface.getNumberOfProductsBySupplier(memberSupplier);
		
		// Mi ricavo il numero dei prodotti totali  in lista
		Integer numberProductsOnList = productInterface.getNumberOfProductsOnListBySupplier(memberSupplier);
		
		if(numberProductsTot != 0) {
			Float temp = (float) (numberProductsOnList/numberProductsTot)*100;
			respStat.add(temp);
			respStat.add(100-temp);
		} else {
			respStat.add((float) 0);
			respStat.add((float) 0);
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
		
		//Mi ricavo la lista degli utenti più attivi
		Map<Member, Float> topUsers = memberInterface.getTopUsers(memberResp);
		
		if(topUsers.size() > 0) {
			
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
	
	
}
