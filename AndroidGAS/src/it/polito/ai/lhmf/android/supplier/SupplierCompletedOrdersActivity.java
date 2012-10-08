package it.polito.ai.lhmf.android.supplier;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class SupplierCompletedOrdersActivity extends Activity {
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
			new GetSupplierCompletedOrdersTask().execute(api);
		}
		super.onResume();
	}
	
	private class GetSupplierCompletedOrdersTask extends AsyncTask<Gas, Void, Order[]>{

		@Override
		protected Order[] doInBackground(Gas... params) {
			Gas gas = params[0];
			
			return gas.orderOperations().getSupplierCompletedOrders();
		}
		
		@Override
		protected void onPostExecute(Order[] result) {
			if(result != null && result.length > 0){
				adapter = new CustomAdapter(SupplierCompletedOrdersActivity.this, R.layout.supplier_completed_order_item, R.id.orderName, result);
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
			TextView orderResp;
			TextView orderDeliveryDate;
			TextView orderCost;
			Button orderDetails;
			
			final Order order = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.supplier_completed_order_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			
			orderDeliveryDate = (TextView) row.findViewById(R.id.orderDeliveryDate);
			
			orderCost = (TextView) row.findViewById(R.id.order_cost_text);
			
			orderDetails = (Button) row.findViewById(R.id.order_details_button);
			
			orderName.setText(order.getOrderName());
			orderResp.setText(order.getMemberResp().getName() + " " + order.getMemberResp().getSurname());
			if(order.getDateDelivery() != null)
				orderDeliveryDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateDelivery()));
				
			orderCost.setText(String.format("%.2f", order.getCost()));
			orderCost.setTextColor(Color.GREEN);
			
			orderDetails.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), SupplierCompletedOrderDetails.class);
					intent.putExtra("order", order);
					startActivity(intent);
				}
			});
			
			return row;
		}
	}
}
