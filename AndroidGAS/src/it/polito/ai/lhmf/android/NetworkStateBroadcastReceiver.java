package it.polito.ai.lhmf.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    Intent serviceIntent = new Intent(context, GasNetworkService.class);
		
		boolean serviceRunning = isServiceRunning(context);
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
	    if(activeNetworkInfo == null){
	    	Log.d("Receiver", "NetworkInfo not connected!");
	    	if(serviceRunning)
	    		context.stopService(serviceIntent);
	    }
	    else{
		    boolean isConnected = activeNetworkInfo.isConnected();
		    if(isConnected){
		    	Log.d("Receiver", "Network connected");
		    	if(serviceRunning == false)
		    		context.startService(serviceIntent);
		    }
	    }
	}
	
	private boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (GasNetworkService.class.getName().equals(serviceInfo.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}


}
