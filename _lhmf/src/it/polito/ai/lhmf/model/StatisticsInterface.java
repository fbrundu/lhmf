package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;
import it.polito.ai.lhmf.orm.Supplier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	private MemberInterface memberInterface;
	private OrderInterface orderInterface;
	private SupplierInterface supplierInterface;
	private ProductInterface productInterface;
	
	public void setSessionFactory(SessionFactory sf)
	{
		this.sessionFactory = sf;
	}
	
	public void setOrderInterface(OrderInterface orderInterface)
	{
		this.orderInterface = orderInterface;
	}
	
	public void setMemberInterface(MemberInterface memberInterface)
	{
		this.memberInterface = memberInterface;
	}
	
	public void setProductInterface(ProductInterface productInterface)
	{
		this.productInterface = productInterface;
	}
	
	public void setSupplierInterface(SupplierInterface supplierInterface)
	{
		this.supplierInterface = supplierInterface;
	}
	
	@Transactional
	public ArrayList<Float> getSupplierMoneyMonth(String supUsername, int year)
	{
		ArrayList<Float> respStat = new ArrayList<Float>();

		// Mi ricavo il membro Supplier
		Member memberSupplier = memberInterface.getMember(supUsername);

		// Mi ricavo la lista degli ordini passati conclusi (con data consegna
		// impostata)
		List<Order> listOrders = orderInterface.getOldOrdersShippedBySupplier(
				memberSupplier, year);

		// Inizializzo variabili
		Calendar cal = Calendar.getInstance();
		Float price = new Float(0);
		Set<Purchase> listPurchase;

		// Azzero struttura che servira per memorizzare gli incassi
		for (int i = 0; i < 12; i++)
			respStat.add(price);

		// Itero sugli ordini
		for (Order temp : listOrders)
		{
			// Per ogni ordine estraggo il mese
			cal.setTime(temp.getDateClose());
			int month = cal.get(Calendar.MONTH);
			// Recupero il prezzo salvato
			price = respStat.get(month);
			// Azzero parziale
			float partial = 0;

			// Accedo alla lista delle schede
			listPurchase = temp.getPurchases();

			Set<PurchaseProduct> listPurchaseProduct;

			// Per ogni scheda d'acquisto mi ricavo il prodotto e relativa
			// quantita'
			for (Purchase tempPurchase : listPurchase)
			{

				listPurchaseProduct = tempPurchase.getPurchaseProducts();
				Product tempProduct;
				int tempAmount;

				for (PurchaseProduct tempPP : listPurchaseProduct)
				{
					tempProduct = tempPP.getProduct();
					tempAmount = tempPP.getAmount();

					partial += tempAmount * (tempProduct.getUnitCost());

				} // Fine prodotti di una scheda
			} // Fine Schede di un ordine

			respStat.set(month, partial + price);

		} // fine Ordine di un responsabile

		return respStat;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional
	public ArrayList<String> supplierMoneyProduct(String supUsername)
	{
		ArrayList<String> respStatName = new ArrayList<String>();
		ArrayList<String> respStatFloat = new ArrayList<String>();
		
		// Mi ricavo il Supplier
		Supplier supplier = supplierInterface.getSupplier(supUsername);
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
	
	@Transactional
	public ArrayList<Double> supplierProductList(String supUsername)
	{
		ArrayList<Double> respStat = new ArrayList<Double>();

		// Mi ricavo il Supplier
		Member memberSupplier = memberInterface.getMember(supUsername);

		// Mi ricavo il numero dei prodotti totali
		Double numberProductsTot = (double) (long) productInterface
				.getNumberOfProductsBySupplier(memberSupplier);

		// Mi ricavo il numero dei prodotti totali in lista
		Double numberProductsOnList = (double) (long) productInterface
				.getNumberOfProductsOnListBySupplier(memberSupplier);

		if (numberProductsTot != 0)
		{
			Double temp = (numberProductsOnList / numberProductsTot) * 100;
			respStat.add(temp);
			respStat.add(100 - temp);
		}
		else
		{
			respStat.add((double) 0);
			respStat.add((double) 0);
		}

		return respStat;
	}
	
	@Transactional
	public ArrayList<Integer> supplierOrderMonth(String supUsername, int year)
	{
		ArrayList<Integer> respStat = new ArrayList<Integer>();
		ArrayList<Integer> respStatNotShipped = new ArrayList<Integer>();

		// Mi ricavo il Supplier
		Member memberSupplier = memberInterface.getMember(supUsername);

		for (int i = 0; i < 12; i++)
		{

			// Mi ricavo il numero di ordini conclusi (con o senza data di
			// consegna)
			Integer numberOrdersTot = (int) (long) orderInterface
					.getNumberOldOrdersBySupplier(memberSupplier, year, i);

			// Mi ricavo il numero di ordini conclusi (con data di consegna
			// impostata)
			Integer numberOrderShipped = (int) (long) orderInterface
					.getNumberOldOrdersShippedBySupplier(memberSupplier, year,
							i);

			respStat.add(numberOrderShipped);
			respStatNotShipped.add(numberOrdersTot - numberOrderShipped);

		}
		respStat.addAll(respStatNotShipped);
		return respStat;
	}
	
	@Transactional
	public ArrayList<Float> supplierOrderYear(String supUsername, int year)
	{
		ArrayList<Float> respStat = new ArrayList<Float>();
		
		// Mi ricavo il Supplier
		Member memberSupplier = memberInterface.getMember(supUsername);
		
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
}
