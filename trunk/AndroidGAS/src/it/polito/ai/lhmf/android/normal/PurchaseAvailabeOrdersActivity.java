package it.polito.ai.lhmf.android.normal;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PurchaseAvailabeOrdersActivity extends Activity {
	private Gas api = null;
	private CustomAdapter adapter = null;
	
	private ListView orderListView = null;
	
	private List<Integer> orderIds = null;
	private float[] ordersProgress = null;
	
	private OrdersProgressesTask orderProgressesTask = null;
	private TextView noOrders = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.normal_available_orders);
			
			orderListView = (ListView) findViewById(R.id.ordersList);
			
			noOrders = (TextView) findViewById(R.id.no_orders_available);
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			new GetAvailableOrdersTask().execute(api);
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if(orderProgressesTask != null){
			orderProgressesTask.cancel(false);
			orderProgressesTask = null;
		}
		super.onPause();
	}
	
	private class GetAvailableOrdersTask extends AsyncTask<Gas, Void, Order[]>{

		@Override
		protected Order[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.orderOperations().getAvailableOrdersForPurchase();
		}
		
		@Override
		protected void onPostExecute(Order[] result) {
			if(result != null && result.length > 0){
				orderIds = new ArrayList<Integer>(result.length);
				ordersProgress = new float[result.length];
				for(int i = 0; i < result.length; i++){
					orderIds.add(result[i].getIdOrder());
					ordersProgress[i] = 0.0f;
				}
				
				if(orderProgressesTask == null){
					orderProgressesTask = new OrdersProgressesTask();
					orderProgressesTask.execute(api, new ArrayList<Integer>(orderIds));
				}
				
				adapter = new CustomAdapter(PurchaseAvailabeOrdersActivity.this, R.layout.order_available_item, R.id.orderName, result);
				orderListView.setAdapter(adapter);
				orderListView.setVisibility(View.VISIBLE);
				noOrders.setVisibility(View.GONE);
			}
			else{
				orderListView.setVisibility(View.GONE);
				noOrders.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private class OrdersProgressesTask extends AsyncTask<Object, Float[], Void>{

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			List<Integer> ids = (List<Integer>) params[1];
			
			Integer[] orderIds = new Integer[ids.size()];
			for(int i = 0; i < ids.size(); i++)
				orderIds[i] = ids.get(i);
			
			while(!isCancelled()){
				Float[] progresses = gas.orderOperations().getOrdersProgresses(orderIds);
				if(progresses != null && progresses.length == orderIds.length){
					this.publishProgress(progresses);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Float[]... values) {
			Float[] newProgress = values[0];
			if(newProgress.length == ordersProgress.length){
				for(int i = 0; i < newProgress.length; i++)
					ordersProgress[i] = newProgress[i];
			}
			adapter.notifyDataSetChanged();
		}
		
	}
	
	private class CustomAdapter extends ArrayAdapter<Order>{
		
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, Order[] objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		public CustomAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TextView orderName;
			TextView orderSupplier;
			TextView orderResp;
			TextView orderOpenDate;
			TextView orderCloseDate;
			ProgressBar orderPb;
			TextView orderProgressText;
			Button createPurchase;
			
			final Order order = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.order_available_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			orderSupplier = (TextView) row.findViewById(R.id.orderSupplier);
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			orderOpenDate = (TextView) row.findViewById(R.id.orderOpenDate);
			orderCloseDate = (TextView) row.findViewById(R.id.orderCloseDate);
			orderPb = (ProgressBar) row.findViewById(R.id.order_progress_bar);
			orderProgressText = (TextView) row.findViewById(R.id.order_progress_percent);
			createPurchase = (Button) row.findViewById(R.id.order_purchase_button);
			
			orderName.setText(order.getOrderName());
			orderSupplier.setText(order.getSupplier().getCompanyName());
			orderResp.setText(order.getMemberResp().getName() + " " + order.getMemberResp().getSurname());
			orderOpenDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateOpen()));
			orderCloseDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateClose()));
			
			float progress = ordersProgress[orderIds.indexOf(order.getIdOrder())];
			int progressInt = (int) progress; 
			orderPb.setProgress(progressInt);
			orderProgressText.setText(String.format("%.2f", progress));
			
			createPurchase.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), NewPurchaseActivity.class);
					intent.putExtra("order", order);
					startActivity(intent);
					
				}
			});
			
			return row;
		}

		
		
	}
}
