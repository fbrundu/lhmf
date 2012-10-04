package it.polito.ai.lhmf.util;

import it.polito.ai.lhmf.model.OrderInterface;

public class ClosedOrdersTask implements Runnable
{
	
	private OrderInterface orderInterface;
	
	public ClosedOrdersTask(OrderInterface oi)
	{	
		this.orderInterface = oi;
	}

	@Override
	public void run() 
	{
		System.out.println("****************************************************");
		System.out.println("****************************************************");
		System.out.println("****************************************************");
		System.out.println("************** CHECKING CLOSED ORDERS **************");
		System.out.println("****************************************************");
		System.out.println("****************************************************");
		System.out.println("****************************************************");
		
		orderInterface.checkClosedOrders();
	}
}
