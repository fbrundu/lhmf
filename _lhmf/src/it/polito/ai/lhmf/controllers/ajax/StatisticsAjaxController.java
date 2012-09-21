package it.polito.ai.lhmf.controllers.ajax;

import it.polito.ai.lhmf.exceptions.InvalidParametersException;
import it.polito.ai.lhmf.model.MemberInterface;
import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.security.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Calendar;
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
public class StatisticsAjaxController
{
	@Autowired
	private MemberInterface memberInterface;
	@Autowired
	private OrderInterface orderInterface;
	
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
		
		// Mi ricavo la lista degli ordini passati conclusi (con data consegna non impostata)
		List<Order> listOrders = orderInterface.getOldOrdersBySupplier(memberSupplier, year);
		
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
			price = respStat.get(month-1);
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
	
			respStat.set(month-1, partial+price);
			
		} // fine Ordine di un responsabile
		
		
		return respStat;
	}
	
	
}
