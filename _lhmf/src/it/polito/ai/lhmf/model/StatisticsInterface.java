package it.polito.ai.lhmf.model;

import it.polito.ai.lhmf.orm.Member;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class StatisticsInterface
{
	// The session factory will be automatically injected by spring
	private SessionFactory sessionFactory;
	private MemberInterface memberInterface;
	private OrderInterface orderInterface;
	
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
	
	@Transactional()
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
}
