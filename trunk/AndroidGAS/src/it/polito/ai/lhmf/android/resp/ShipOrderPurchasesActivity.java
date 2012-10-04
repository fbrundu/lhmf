package it.polito.ai.lhmf.android.resp;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Purchase;
import it.polito.ai.lhmf.model.Supplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ShipOrderPurchasesActivity extends Activity{
	private Gas api;
	
	private Order order;
	private Supplier supplier;
	
	private ListView purchasesListView = null;
	private TextView supplierName = null;
	private TextView orderName = null;
	private TextView noPurchases = null;
	
	private TextView orderTotalCost = null;
	
	private CustomAdapter adapter = null;
	
	private Purchase[] orderPurchases = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		order = (Order) getIntent().getSerializableExtra("order");
		if(order != null){
			supplier = order.getSupplier();
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.purchases_delivery);
				
				noPurchases = (TextView) findViewById(R.id.no_supplier_products);
				
				purchasesListView = (ListView) findViewById(R.id.orderPurchasesList);
				adapter = new CustomAdapter(this, R.layout.completed_purchase_item, R.id.memberName);
				
				supplierName = (TextView) findViewById(R.id.supplier_name);
				
				supplierName.setText(supplier.getCompanyName());
				
				orderName = (TextView) findViewById(R.id.order_name);
				orderName.setText(order.getOrderName());
				
				orderTotalCost = (TextView) findViewById(R.id.order_cost_text);
				
				new GetOrderPurchasesTask().execute(api, order.getIdOrder());
			}
		}
	}
	
	private Float computeTotalCoast() {
		float ret = 0.0f;
		for(Purchase p : orderPurchases){
			if(!p.isFailed())
				ret += p.getTotCost();
		}
		return ret;
	}
	
	private class GetOrderPurchasesTask extends AsyncTask<Object, Void, Purchase[]>{

		@Override
		protected Purchase[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idOrder = (Integer) params[1];
			
			return gas.orderOperations().getOrderCompletedPurchases(idOrder);
		}
		
		@Override
		protected void onPostExecute(Purchase[] res) {
			if(res != null && res.length > 0){
				Purchase[] result = res;
				
				List<Purchase> ps = new ArrayList<Purchase>();
				for(Purchase p : res)
					ps.add(p);
				
				Collections.sort(ps, new Comparator<Purchase>() {
					@Override
					public int compare(Purchase lhs, Purchase rhs) {
						int ret = lhs.getMember().getSurname().compareTo(rhs.getMember().getSurname());
						if(ret == 0)
							return lhs.getMember().getName().compareTo(rhs.getMember().getName());
						return ret;
					}
				});
				
				int i = 0;
				orderPurchases = new Purchase[result.length];
				
				for(Purchase p : ps){
					orderPurchases[i] = p;
					i++;
					adapter.add(p);
				}
				
				purchasesListView.setAdapter(adapter);
				
				purchasesListView.setVisibility(View.VISIBLE);
				noPurchases.setVisibility(View.GONE);
				
				orderTotalCost.setText(String.format("%.2f", computeTotalCoast()));
			}
			else{
				purchasesListView.setVisibility(View.GONE);
				noPurchases.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private class CustomAdapter extends ArrayAdapter<Purchase>{

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, Purchase[] objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		public CustomAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			TextView memberName;
			TextView status;

			View costLayout;
			TextView cost;
			
			Button setDeliveredButton;
			
			Purchase p = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.completed_purchase_item, parent, false);
			}
			
			memberName = (TextView) row.findViewById(R.id.memberName);
			status = (TextView) row.findViewById(R.id.status_text);
			setDeliveredButton = (Button) row.findViewById(R.id.set_delivered_button);
			costLayout = row.findViewById(R.id.costLayout);
			cost = (TextView) row.findViewById(R.id.purchase_tot_cost);
			
			memberName.setText(p.getMember().getName() + " " + p.getMember().getSurname());
			
			if(p.isFailed()){
				costLayout.setVisibility(View.GONE);
				status.setText(R.string.failedF);
				status.setTextColor(Color.RED);
				setDeliveredButton.setEnabled(false);
			}
			else{
				costLayout.setVisibility(View.VISIBLE);
				status.setText(R.string.ok);
				status.setTextColor(Color.GREEN);
				
				cost.setText(String.format("%.2f", p.getTotCost()));
			}
			
			setDeliveredButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO set purchase as delivered
					
				}
			});
			
			row.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//TODO completed purchase details
					
				}
			});
			
			return row;
		}
	}
}
