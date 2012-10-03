package it.polito.ai.lhmf.android.service;

import it.polito.ai.lhmf.android.LoginActivity;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;

import org.springframework.social.connect.Connection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class GasNetworkService extends Service {
	private static final int LOGIN_REQUIRED_NOTIFICATION = 0;
	private static final int OK_NOTIFICATION = 1;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		
		if(conn == null){
			// Non c'è la connessione nel repo --> l'utente non ha fatto il login
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: effettuare il login", System.currentTimeMillis());
			Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS", "è necessario effettuare il login", contentIntent);
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			nm.notify(LOGIN_REQUIRED_NOTIFICATION, notification);
		}
		else{
			// La connessione c'è
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(android.R.drawable.stat_notify_sync, "Gas: tutto ok", System.currentTimeMillis());
			Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Notifica GAS 2", "okok", contentIntent);
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			nm.notify(OK_NOTIFICATION, notification);
		}
		
		return START_NOT_STICKY;
	}

}
