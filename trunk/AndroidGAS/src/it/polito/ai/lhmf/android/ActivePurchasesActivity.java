package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Purchase;

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


public class ActivePurchasesActivity extends Activity {
	private Gas api = null;
	private CustomAdapter adapter = null;
	
	private ListView purchaseListView = null;
	
	private List<Integer> orderIds = null;
	private float[] ordersProgress = null;
	
	private OrdersProgressesTask orderProgressesTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.purchase_list);
			
			TextView title = (TextView) findViewById(R.id.pruchase_list_title);
			title.setText("Schede attive");
			
			purchaseListView = (ListView) findViewById(R.id.purchaseList);
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			new GetActivePurchasesTask().execute(api);
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
	
	private class GetActivePurchasesTask extends AsyncTask<Gas, Void, Purchase[]>{

		@Override
		protected Purchase[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.purchaseOperations().getActivePurchases();
		}
		
		@Override
		protected void onPostExecute(Purchase[] result) {
			if(result != null && result.length > 0){
				orderIds = new ArrayList<Integer>(result.length);
				ordersProgress = new float[result.length];
				for(int i = 0; i < result.length; i++){
					orderIds.add(result[i].getOrder().getIdOrder());
					ordersProgress[i] = 0.0f;
				}
				
				if(orderProgressesTask == null){
					orderProgressesTask = new OrdersProgressesTask();
					orderProgressesTask.execute(api, new ArrayList<Integer>(orderIds));
				}
				
				adapter = new CustomAdapter(ActivePurchasesActivity.this, R.layout.purchase_list_item, R.id.orderName, result);
				purchaseListView.setAdapter(adapter);
			}
			//TODO else mostrare che non ci sono schede attive
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
	
	private class CustomAdapter extends ArrayAdapter<Purchase>{
		
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, Purchase[] result) {
			super(context, resource, textViewResourceId, result);
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
			TextView purchaseCost;
			ProgressBar orderPb;
			TextView orderProgressText;
			Button purchaseDetails;
			
			final Purchase purchase = getItem(position);
			Order order = purchase.getOrder();
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.purchase_list_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			orderSupplier = (TextView) row.findViewById(R.id.orderSupplier);
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			orderOpenDate = (TextView) row.findViewById(R.id.orderOpenDate);
			orderCloseDate = (TextView) row.findViewById(R.id.orderCloseDate);
			purchaseCost = (TextView) row.findViewById(R.id.purchase_tot_cost);
			orderPb = (ProgressBar) row.findViewById(R.id.order_progress_bar);
			orderProgressText = (TextView) row.findViewById(R.id.order_progress_percent);
			purchaseDetails = (Button) row.findViewById(R.id.purchase_details_button);
			
			orderName.setText(order.getOrderName());
			orderSupplier.setText(order.getSupplier().getCompanyName());
			orderResp.setText(order.getMemberResp().getName() + " " + order.getMemberResp().getSurname());
			orderOpenDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateOpen()));
			orderCloseDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateClose()));
			purchaseCost.setText(String.format("%.2f", purchase.getTotCost()));
			
			float progress = ordersProgress[orderIds.indexOf(order.getIdOrder())];
			int progressInt = (int) progress; 
			orderPb.setProgress(progressInt);
			orderProgressText.setText(String.format("%.2f", progress));
			
			purchaseDetails.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), EditPurchaseActivity.class);
					intent.putExtra("purchase", purchase);
					startActivity(intent);
					
				}
			});
			
			return row;
		}

		
		
	}
}
