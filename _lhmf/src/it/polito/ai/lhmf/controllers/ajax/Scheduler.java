package it.polito.ai.lhmf.controllers.ajax;

import java.util.ArrayList;
import java.util.List;

import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.model.ProductInterface;
import it.polito.ai.lhmf.model.PurchaseInterface;
import it.polito.ai.lhmf.orm.Order;
import it.polito.ai.lhmf.orm.Product;
import it.polito.ai.lhmf.orm.Purchase;
import it.polito.ai.lhmf.orm.PurchaseProduct;

public class Scheduler implements Runnable
{
	
	private OrderInterface orderInterface;
	
	private PurchaseInterface purchaseInterface;
	
	private ProductInterface productInterface;
	
	public Scheduler(OrderInterface oi, PurchaseInterface pui, ProductInterface pri)
	{	
		this.orderInterface = oi;
		this.purchaseInterface = pui;
		this.productInterface = pri;
	}

	@Override
	public void run() 
	{
		List<Order> listOrders = orderInterface.getOrdersNotFailed();
		List<PurchaseProduct> listNotMin = new ArrayList<PurchaseProduct>();
		for (Order orderTmp : listOrders)
		{
			List<Purchase> listPurchase = purchaseInterface.getPurchaseFromOrder(orderTmp.getIdOrder());
			for(Purchase purchaseTmp : listPurchase)
			{
				List<PurchaseProduct> tmpList = productInterface.getPurchaseProductFromPurchase(purchaseTmp.getIdPurchase());
				for(PurchaseProduct productTmp : tmpList)
				{
					int result = productInterface.checkFailed(productTmp.getProduct().getIdProduct(), productTmp.getAmount());
					if(result == 0)
					{
						orderInterface.updateFailedWithOrder(orderTmp.getIdOrder(), productTmp.getProduct().getIdProduct());
					}
					else
					{	
						listNotMin.add(productTmp);
					}
				}
			}
		}
		for(int i = 0; i < listNotMin.size(); i++)
		{
			int amTmp = listNotMin.get(i).getAmount();
			Product tmp = productInterface.getProduct(listNotMin.get(i).getProduct().getIdProduct());
			for(int j = i + 1; j < listNotMin.size(); j++)
			{
				if(listNotMin.get(i).getProduct().getIdProduct() == listNotMin.get(j).getProduct().getIdProduct())
				{
					amTmp += listNotMin.get(j).getAmount();
					listNotMin.remove(j);
				}
				if(amTmp >= tmp.getMinBuy())
				{
					orderInterface.updateFailedWithNoOrder(listNotMin.get(i).getProduct().getIdProduct());
				}
			}
		}
	}
}
