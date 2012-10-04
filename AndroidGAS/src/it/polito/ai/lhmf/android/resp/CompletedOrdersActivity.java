package it.polito.ai.lhmf.android.resp;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;

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


public class CompletedOrdersActivity extends Activity {
	private Gas api = null;
	private CustomAdapter adapter = null;
	
	private ListView orderListView = null;
	
	private TextView noOrders = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.active_orders);
			
			TextView title = (TextView) findViewById(R.id.title);
			title.setText(R.string.completed_orders);
			
			orderListView = (ListView) findViewById(R.id.ordersList);
			
			noOrders = (TextView) findViewById(R.id.no_orders_available);
			noOrders.setText("Non ci sono ordini da visualizzare");
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			new GetCompletedOrdersTask().execute(api);
		}
		super.onResume();
	}
	
	private class GetCompletedOrdersTask extends AsyncTask<Gas, Void, Order[]>{

		@Override
		protected Order[] doInBackground(Gas... params) {
			Gas gas = params[0];
			
			return gas.orderOperations().getRespCompletedOrders();
		}
		
		@Override
		protected void onPostExecute(Order[] result) {
			if(result != null && result.length > 0){
				adapter = new CustomAdapter(CompletedOrdersActivity.this, R.layout.order_available_item, R.id.orderName, result);
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
			TextView respLabel;
			TextView orderOpenDate;
			TextView orderCloseDate;
			TextView orderProgressLabel;
			View orderProgressLayout;
			Button orderDetails;
			
			final Order order = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.order_available_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			orderSupplier = (TextView) row.findViewById(R.id.orderSupplier);
			
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			orderResp.setVisibility(View.GONE);
			respLabel = (TextView) row.findViewById(R.id.respLabel);
			respLabel.setVisibility(View.GONE);
			
			orderOpenDate = (TextView) row.findViewById(R.id.orderOpenDate);
			orderCloseDate = (TextView) row.findViewById(R.id.orderCloseDate);
			
			orderProgressLabel = (TextView) row.findViewById(R.id.order_progress_label);
			orderProgressLabel.setVisibility(View.GONE);
			
			orderProgressLayout = row.findViewById(R.id.orderProgressLayout);
			orderProgressLayout.setVisibility(View.GONE);
			
			orderDetails = (Button) row.findViewById(R.id.order_purchase_button);
			
			orderName.setText(order.getOrderName());
			orderSupplier.setText(order.getSupplier().getCompanyName());
			orderOpenDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateOpen()));
			orderCloseDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateClose()));
			
			orderDetails.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), SetOrderDeliveryActivity.class);
					intent.putExtra("order", order);
					startActivity(intent);
				}
			});
			
			return row;
		}
	}
}
