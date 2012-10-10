package it.polito.ai.lhmf.android.service;

import it.polito.ai.lhmf.android.ActiveOrderDetailsActivity;
import it.polito.ai.lhmf.android.CompletedPurchaseDetailsActivity;
import it.polito.ai.lhmf.android.LoginActivity;
import it.polito.ai.lhmf.android.ProductDetailsActivity;
import it.polito.ai.lhmf.android.admin.ActivateMembersActivity;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.normal.EditPurchaseActivity;
import it.polito.ai.lhmf.android.normal.NewPurchaseActivity;
import it.polito.ai.lhmf.android.resp.SetOrderDeliveryActivity;
import it.polito.ai.lhmf.android.supplier.SupplierCompletedOrderDetails;
import it.polito.ai.lhmf.model.Member;
import it.polito.ai.lhmf.model.Notify;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Purchase;
import it.polito.ai.lhmf.model.constants.MemberStatuses;

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
	public static final int ORDER_CLOSED_NOTIFICATION_PREFIX = 4; //Resp
	public static final int ORDER_DELIVERY_SET_NOTIFICATION_PREFIX = 5; //Normale, Resp
	public static final int NEW_MEMBER_NOTIFICATION_PREFIX = 6; //Admin 
	public static final int ORDER_PROGRESS_UPDATE_NOTIFICATION_PREFIX = 7;  //Da usare solo in android per notifiche 7,8,9
	public static final int ORDER_50_NOTIFICATION_PREFIX = 7; //Normale, Resp
	public static final int ORDER_75_NOTIFICATION_PREFIX = 8; //Normale, Resp
	public static final int ORDER_90_NOTIFICATION_PREFIX = 9; //Normale, Resp
	public static final int ORDER_CLOSED_PARTICIPANT_NOTIFICATION_PREFIX = 10; //normale, resp (partecipanti all'ordine)
	public static final int SUPPLIER_NEW_ORDER_NOTIFICATION_PREFIX = 11; //Supplier
	public static final int SUPPLIER_ORDER_CLOSED_NOTIFICATION_PREFIX = 12; //Supplier
	public static final int SUPPLIER_ORDER_DELIVERY_SET_NOTIFICATION_PREFIX = 13; //Supplier
	
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
		am.set(AlarmManager.RTC_WAKEUP, curCal.getTimeInMillis() + 10*1000, pIntent);
		//am.set(AlarmManager.RTC_WAKEUP, curCal.getTimeInMillis() + 10*60*1000, pIntent);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		
		if(conn == null){
			// Non c'è la connessione nel repo --> l'utente non ha fatto il login
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: effettuare il login", System.currentTimeMillis());
			Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Effettuare il login", contentIntent);
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
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
	
	private static void prepareNotificationFlasgs(Notification notification){
		if(notification != null){
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
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
							productAvailabilityChangedNotification(text);
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
						case ORDER_CLOSED_PARTICIPANT_NOTIFICATION_PREFIX:
							orderClosedParticipantNotification(text);
							break;
						case SUPPLIER_NEW_ORDER_NOTIFICATION_PREFIX:
							supplierNewOrderNotification(text);
							break;
						case SUPPLIER_ORDER_CLOSED_NOTIFICATION_PREFIX:
							supplierOrderClosedNotification(text);
							break;
						case SUPPLIER_ORDER_DELIVERY_SET_NOTIFICATION_PREFIX:
							supplierOrderDeliverySetNotification(text);
							break;
						default:
							break;
					}
				}
			}
		}

		private void productAvailabilityChangedNotification(String text) {
			Integer productId = null;
			try {
				productId = Integer.valueOf(text);
				Product p = api.productOperations().getProduct(productId);
				boolean available = p.getAvailability();
				Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Modifica disponibilità prodotto", System.currentTimeMillis());
				Intent notificationIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
				notificationIntent.putExtra("idProduct", productId);
				notificationIntent.setAction("it.polito.ai.lhmf.availability." + productId);
				if(available)
					notificationIntent.putExtra("fromNotify", true);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				if(available)
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Il prodotto " + p.getName() +
							" è nuovamente disponibile per la creazione di ordini.", contentIntent);
				else
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Il prodotto " + p.getName() +
							" non è al momento disponibile per la creazione di nuovi ordini.", contentIntent);
				prepareNotificationFlasgs(notification);
				
				Integer notifyId = Integer.valueOf(PRODUCT_AVAILABILITY_CHANGED_NOTIFICATION_PREFIX + "" + productId);
				nm.notify(notifyId, notification);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void supplierOrderDeliverySetNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Order order = api.orderOperations().getOrder(orderId);
				if(order != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Data di consegna ordine impostata", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), SupplierCompletedOrderDetails.class);
					notificationIntent.putExtra("order", order);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.supplier.delivery." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Data di consegna impostata per l'ordine " + order.getOrderName(), contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(SUPPLIER_ORDER_DELIVERY_SET_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void supplierOrderClosedNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Order order = api.orderOperations().getOrder(orderId);
				if(order != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Ordine completato", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), SupplierCompletedOrderDetails.class);
					notificationIntent.putExtra("order", order);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.supplier.closed." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "L'ordine " + order.getOrderName() + " è completato", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(SUPPLIER_ORDER_CLOSED_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void supplierNewOrderNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Order order = api.orderOperations().getOrder(orderId);
				if(order != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Nuovo ordine", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), ActiveOrderDetailsActivity.class);
					notificationIntent.putExtra("order", order);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.supplier.new." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Nuovo ordine", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(SUPPLIER_NEW_ORDER_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void orderClosedParticipantNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Purchase purchase = api.purchaseOperations().getMyPurchase(orderId);
				if(purchase != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Ordine chiuso", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), CompletedPurchaseDetailsActivity.class);
					notificationIntent.putExtra("purchase", purchase);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.participant.closed." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "L'ordine " + purchase.getOrder().getOrderName() + " è stato chiuso", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(ORDER_CLOSED_PARTICIPANT_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void orderProgressNotification(String text, double progress) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Purchase purchase = api.purchaseOperations().getMyPurchase(orderId);
				if(purchase != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: progresso ordine cambiato", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), EditPurchaseActivity.class);
					notificationIntent.putExtra("purchase", purchase);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.progress." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Il progresso dell'ordine " + purchase.getOrder().getOrderName() +
							" ha superato il " + String.format("%.2f", progress * 100) + "%", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(ORDER_PROGRESS_UPDATE_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
				else{
					Order order = api.orderOperations().getOrder(orderId);
					if(order != null){
						Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: progresso ordine cambiato", System.currentTimeMillis());
						Intent notificationIntent = new Intent(getApplicationContext(), NewPurchaseActivity.class);
						notificationIntent.putExtra("order", order);
						notificationIntent.putExtra("fromNotify", true);
						notificationIntent.setAction("it.polito.ai.lhmf.progress." + orderId);
						notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Il progresso dell'ordine " + order.getOrderName() +
								" ha superato il " + String.format("%.2f", progress * 100) + "%", contentIntent);
						prepareNotificationFlasgs(notification);
						
						Integer notifyId = Integer.valueOf(ORDER_PROGRESS_UPDATE_NOTIFICATION_PREFIX + "" + orderId);
						nm.notify(notifyId, notification);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}

		private void newMemberNotification(String text) {
			Integer memberId;
			try {
				memberId = Integer.valueOf(text);
				Member member = api.userOperations().getMember(memberId);
				if(member != null && member.getMemberStatusId() == MemberStatuses.VERIFIED_DISABLED){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Richiesta di attivazione utente", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), ActivateMembersActivity.class);
					notificationIntent.setAction("it.polito.ai.lhmf.activate");
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Richiesta di attivazione utente", contentIntent);
					prepareNotificationFlasgs(notification);
					
					nm.notify(NEW_MEMBER_NOTIFICATION_PREFIX, notification);
				}
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}

		private void orderDeliverySetNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Purchase purchase = api.purchaseOperations().getMyPurchase(orderId);
				if(purchase != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Data di consegna ordine impostata", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), CompletedPurchaseDetailsActivity.class);
					notificationIntent.putExtra("purchase", purchase);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.delivery." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Data di consegna impostata per l'ordine " 
									+ purchase.getOrder().getOrderName(), contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(ORDER_DELIVERY_SET_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}

		private void orderClosedNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Order order = api.orderOperations().getOrder(orderId);
				if(order != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Ordine chiuso", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), SetOrderDeliveryActivity.class);
					notificationIntent.putExtra("order", order);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.closed." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "L'ordine " + order.getOrderName() + " è stato chiuso", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(ORDER_CLOSED_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}

		private void newOrderNotification(String text) {
			Integer orderId = null;
			try {
				orderId = Integer.valueOf(text);
				Order order = api.orderOperations().getOrder(orderId);
				if(order != null){
					Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Nuovo ordine", System.currentTimeMillis());
					Intent notificationIntent = new Intent(getApplicationContext(), NewPurchaseActivity.class);
					notificationIntent.putExtra("order", order);
					notificationIntent.putExtra("fromNotify", true);
					notificationIntent.setAction("it.polito.ai.lhmf.neworder." + orderId);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Nuovo ordine", contentIntent);
					prepareNotificationFlasgs(notification);
					
					Integer notifyId = Integer.valueOf(NEW_ORDER_NOTIFICATION_PREFIX + "" + orderId);
					nm.notify(notifyId, notification);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
		}

		private void newProductNotification(String text) {
			Integer productId = null;
			try {
				productId = Integer.valueOf(text);
				Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: Nuovo prodotto", System.currentTimeMillis());
				Intent notificationIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
				notificationIntent.putExtra("idProduct", productId);
				notificationIntent.putExtra("fromNotify", true);
				notificationIntent.setAction("it.polito.ai.lhmf.newproduct." + productId);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "Nuovo prodotto", contentIntent);
				prepareNotificationFlasgs(notification);
				
				Integer notifyId = Integer.valueOf(NEW_PRODUCT_NOTIFICATION_PREFIX + "" + productId);
				nm.notify(notifyId, notification);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
		}
	}

}
