package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Order;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PurchaseAvailabeOrdersActivity extends Activity {
	private Gas api = null;
	private ListAdapter adapter = null;
	
	private ListView orderListView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.normal_available_orders);
			
			orderListView = (ListView) findViewById(R.id.ordersList);
			adapter = new SeparatedListAdapter(this);
			
			new GetAvailableOrdersTask().execute(api);
		}
	}
	
	private class GetAvailableOrdersTask extends AsyncTask<Gas, Void, Order[]>{

		@Override
		protected Order[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.orderOperations().getAvailableOrdersForPurchase();
		}
		
		@Override
		protected void onPostExecute(Order[] result) {
			if(result != null){
				adapter = new CustomAdapter(PurchaseAvailabeOrdersActivity.this, R.layout.order_item, R.id.orderName, result);
				orderListView.setAdapter(adapter);
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
			TextView orderOpenDate;
			TextView orderCloseDate;
			
			Order order = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.order_item, parent, false);
			}
			orderName = (TextView) row.findViewById(R.id.orderName);
			orderSupplier = (TextView) row.findViewById(R.id.orderSupplier);
			orderResp = (TextView) row.findViewById(R.id.orderResp);
			orderOpenDate = (TextView) row.findViewById(R.id.orderOpenDate);
			orderCloseDate = (TextView) row.findViewById(R.id.orderCloseDate);
			
			orderName.setText(order.getOrderName());
			orderSupplier.setText(order.getSupplier().getCompanyName());
			orderResp.setText(order.getMemberResp().getName() + " " + order.getMemberResp().getSurname());
			orderOpenDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateOpen()));
			orderCloseDate.setText(DateFormat.format("dd/MM/yyyy", order.getDateClose()));
			
			return row;
		}

		
		
	}
}
