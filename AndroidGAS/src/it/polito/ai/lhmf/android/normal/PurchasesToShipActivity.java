package it.polito.ai.lhmf.android.normal;

import it.polito.ai.lhmf.android.CompletedPurchaseDetailsActivity;
import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Purchase;

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
import android.widget.TextView;


public class PurchasesToShipActivity extends Activity {
	private Gas api = null;
	private CustomAdapter adapter = null;
	
	private ListView purchaseListView = null;
	private TextView noPurchases = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.shipped_purchases);
			
			purchaseListView = (ListView) findViewById(R.id.shippedPurchasesList);
			noPurchases = (TextView) findViewById(R.id.no_purchases);
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			new GetShippedPurchasesTask().execute(api);
		}
		super.onResume();
	}
	
	private class GetShippedPurchasesTask extends AsyncTask<Gas, Void, Purchase[]>{

		@Override
		protected Purchase[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.purchaseOperations().getPurchasesWithDeliveryDate();
		}
		
		@Override
		protected void onPostExecute(Purchase[] result) {
			if(result != null && result.length > 0){
				
				adapter = new CustomAdapter(PurchasesToShipActivity.this, R.layout.shipped_purchase_item, R.id.orderName, result);
				purchaseListView.setAdapter(adapter);
				
				purchaseListView.setVisibility(View.VISIBLE);
				noPurchases.setVisibility(View.GONE);
			}
			else{
				purchaseListView.setVisibility(View.GONE);
				noPurchases.setVisibility(View.VISIBLE);
			}
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
			TextView orderDeliveryDate;
			TextView purchaseCost;
			Button purchaseDetails;
			
			final Purchase purchase = getItem(position);
			Order order = purchase.getOrder();
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.shipped_purchase_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			orderSupplier = (TextView) row.findViewById(R.id.orderSupplier);
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			orderDeliveryDate = (TextView) row.findViewById(R.id.orderDeliveryDate);
			purchaseCost = (TextView) row.findViewById(R.id.purchase_tot_cost);
			purchaseDetails = (Button) row.findViewById(R.id.purchase_details_button);
			
			orderName.setText(order.getOrderName());
			orderSupplier.setText(order.getSupplier().getCompanyName());
			orderResp.setText(order.getMemberResp().getName() + " " + order.getMemberResp().getSurname());
			orderDeliveryDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateDelivery()));
			purchaseCost.setText(String.format("%.2f", purchase.getTotCost()));
			
			purchaseDetails.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), CompletedPurchaseDetailsActivity.class);
					intent.putExtra("purchase", purchase);
					startActivity(intent);
				}
			});
			
			return row;
		}
	}
}
