package it.polito.ai.lhmf.listeners;

import it.polito.ai.lhmf.model.OrderInterface;
import it.polito.ai.lhmf.util.ClosedOrdersTask;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MyServletContextListener implements ServletContextListener
{
	ScheduledThreadPoolExecutor sched = new ScheduledThreadPoolExecutor(1);

	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		WebApplicationContext webCtx = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
		OrderInterface oi = webCtx.getBean("orderDao", OrderInterface.class);
		
		ClosedOrdersTask task = new ClosedOrdersTask(oi);
		sched.scheduleAtFixedRate(task, CountDelay(), (24 * 60 * 60), TimeUnit.SECONDS);
		//sched.scheduleAtFixedRate(task, 10, 30*60, TimeUnit.SECONDS);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		sched.shutdown();
	}

	private int CountDelay()
	{
		Calendar calendar = new GregorianCalendar();
		
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		int delayInMinutes = ((59 - minutes) + (23 * 60 - hours * 60) + 2);
		
		return (delayInMinutes * 60); 
	}
	
}
