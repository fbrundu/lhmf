package it.polito.ai.lhmf.android.service;

import it.polito.ai.lhmf.android.LoginActivity;
import it.polito.ai.lhmf.android.ProductDetailsActivity;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Notify;

import java.util.Calendar;
import java.util.List;

import org.springframework.social.connect.Connection;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

public class GasNetworkService extends Service {
	public static final int LOGIN_REQUIRED_NOTIFICATION = -1;  //Tutti
	public static final int BASIC_NOTIFICATION = 0; //Tutti
	public static final int NEW_PRODUCT_NOTIFICATION_PREFIX = 1; //Resp
	public static final int NEW_ORDER_NOTIFICATION_PREFIX = 2; //Normale, Resp
	public static final int PRODUCT_AVAILABILITY_CHANGED_NOTIFICATION_PREFIX = 3; //Resp 
	public static final int ORDER_CLOSED_NOTIFICATION_PREFIX = 4; //Normale, Resp ??? TODO io direi solo resp, poi quando setta la consegna va agli altri
	public static final int ORDER_DELIVERY_SET_NOTIFICATION_PREFIX = 5; //Normale, Resp
	public static final int NEW_MEMBER_NOTIFICATION_PREFIX = 6; //Admin 
	public static final int ORDER_50_NOTIFICATION_PREFIX = 7; //Normale, Resp
	public static final int ORDER_75_NOTIFICATION_PREFIX = 8; //Normale, Resp
	public static final int ORDER_90_NOTIFICATION_PREFIX = 9; //Normale, Resp
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!isConnectionActive())
			return START_NOT_STICKY; //The service will be started by the broadcast receiver when the connection will be activated;
		
		Intent serviceStartIntent = new Intent(getApplicationContext(), GasNetworkService.class);
		
		PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, serviceStartIntent, 0);
		
		Calendar curCal = Calendar.getInstance();
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, curCal.getTimeInMillis() + 30*1000, pIntent);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		
		if(conn == null){
			// Non c'� la connessione nel repo --> l'utente non ha fatto il login
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: effettuare il login", System.currentTimeMillis());
			Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Effettuare il login", contentIntent);
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			nm.notify(LOGIN_REQUIRED_NOTIFICATION, notification);
		}
		else{
			Gas gas = conn.getApi();
			
			Thread t = new Thread(new GetNotifiesTask(gas));
			t.start();
		}
		
		return START_NOT_STICKY;
	}

	private boolean isConnectionActive() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
	    if(activeNetworkInfo == null || activeNetworkInfo.isConnected() == false){
	    	return false;
	    }
		return true;
	}
	
	private class GetNotifiesTask implements Runnable{
		private NotificationManager nm;
		
		private Gas api;
		public GetNotifiesTask(Gas gas) {
			api = gas;
			nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		@Override
		public void run() {
			List<Notify> newNotifies = api.notifyOperations().getNewNotifies();
			if(newNotifies != null && newNotifies.size() > 0){
				for(Notify n : newNotifies){
					String text = n.getText();
					switch(n.getNotifyCategory()){
						case BASIC_NOTIFICATION:
							break;
						case NEW_PRODUCT_NOTIFICATION_PREFIX:
							newProductNotification(text);
							break;
						case NEW_ORDER_NOTIFICATION_PREFIX:
							newOrderNotification(text);
							break;
						case PRODUCT_AVAILABILITY_CHANGED_NOTIFICATION_PREFIX:
							break;
						case ORDER_CLOSED_NOTIFICATION_PREFIX:
							orderClosedNotification(text);
							break;
						case ORDER_DELIVERY_SET_NOTIFICATION_PREFIX:
							orderDeliverySetNotification(text);
							break;
						case NEW_MEMBER_NOTIFICATION_PREFIX:
							newMemberNotification(text);
							break;
						case ORDER_50_NOTIFICATION_PREFIX:
							orderProgressNotification(text, 0.5);
							break;
						case ORDER_75_NOTIFICATION_PREFIX:
							orderProgressNotification(text, 0.75);
							break;
						case ORDER_90_NOTIFICATION_PREFIX:
							orderProgressNotification(text, 0.9);
							break;
					}
				}
			}
			
		}

		private void orderProgressNotification(String text, double d) {
			// TODO Auto-generated method stub
			
		}

		private void newMemberNotification(String text) {
			// TODO Auto-generated method stub
			
		}

		private void orderDeliverySetNotification(String text) {
			// TODO Auto-generated method stub
			
		}

		private void orderClosedNotification(String text) {
			// TODO Auto-generated method stub
			
		}

		private void newOrderNotification(String text) {
			// TODO Auto-generated method stub
			
		}

		private void newProductNotification(String text) {
			Integer productId = null;
			try {
				productId = Integer.valueOf(text);
				Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Nuovo prodotto", System.currentTimeMillis());
				Intent notificationIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
				notificationIntent.putExtra("idProduct", productId);
				notificationIntent.putExtra("fromNotify", true);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
				notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Nuovo prodotto", contentIntent);
				notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				
				Integer notifyId = Integer.valueOf(NEW_PRODUCT_NOTIFICATION_PREFIX + "" + productId);
				nm.notify(notifyId, notification);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}
		
	}

}