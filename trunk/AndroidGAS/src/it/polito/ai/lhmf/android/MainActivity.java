package it.polito.ai.lhmf.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.normal.ActivePurchasesActivity;
import it.polito.ai.lhmf.android.normal.PurchaseAvailabeOrdersActivity;
import it.polito.ai.lhmf.android.resp.ActiveOrdersActivity;
import it.polito.ai.lhmf.android.resp.CompletedOrdersActivity;
import it.polito.ai.lhmf.android.resp.NewOrderActivity;
import it.polito.ai.lhmf.android.supplier.ListinoActivity;
import it.polito.ai.lhmf.android.supplier.NewProductActivity;
import it.polito.ai.lhmf.model.Supplier;
import it.polito.ai.lhmf.model.constants.MemberTypes;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Integer memberType = null;
	private GasConnectionHolder holder;
	private Gas api = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		holder = new GasConnectionHolder(getApplicationContext());
		
		Connection<Gas> conn = holder.getConnection();
		if(conn == null){
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(loginIntent);
			this.finish();
		}
		else{
			//Intent dummyIntent = new Intent(getApplicationContext(), ActivePurchasesActivity.class);
			//startActivity(dummyIntent);
			api = conn.getApi();
			
			new MemberTypeAsyncTask().execute(api);
		}
	}
	
	private void prepareSupplierActivity() {
		setContentView(R.layout.supplier);
		Button newProduct = (Button) findViewById(R.id.newProductButton);
		newProduct.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newProductIntent = new Intent(MainActivity.this, NewProductActivity.class);
				startActivity(newProductIntent);
			}
		});
		
		Button listino = (Button) findViewById(R.id.listinoButton);
		listino.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent listinoIntent = new Intent(MainActivity.this, ListinoActivity.class);
				startActivity(listinoIntent);
			}
		});
		
	}
	
	private void prepareNormalActivity() {
		setContentView(R.layout.normal);
		
		Button newPurchase = (Button) findViewById(R.id.newPurchaseButton);
		newPurchase.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent availableOrdersIntent = new Intent(MainActivity.this, PurchaseAvailabeOrdersActivity.class);
				startActivity(availableOrdersIntent);
			}
		});
		
		Button activePurchases = (Button) findViewById(R.id.activePurchasesButton);
		activePurchases.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent activePurchasesIntent = new Intent(MainActivity.this, ActivePurchasesActivity.class);
				startActivity(activePurchasesIntent);
			}
		});
	}
	
	private void prepareRespActivity(){
		setContentView(R.layout.resp);
		
		Button newOrder = (Button) findViewById(R.id.new_order_button);
		newOrder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GetRespSuppliersAsyncTask().execute(api);
			}
		});
		
		Button activeOrders = (Button) findViewById(R.id.active_orders_button);
		activeOrders.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent activeOrdersIntent = new Intent(MainActivity.this, ActiveOrdersActivity.class);
				startActivity(activeOrdersIntent);
			}
		});
		
		Button completedOrders = (Button) findViewById(R.id.completed_orders_button);
		completedOrders.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent completedOrdersIntent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
				startActivity(completedOrdersIntent);
			}
		});
		
		Button shippedOrders = (Button) findViewById(R.id.shipped_orders_button);
		shippedOrders.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button orderHistory = (Button) findViewById(R.id.order_history_button);
		orderHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		Button newPurchase = (Button) findViewById(R.id.newPurchaseButton);
		newPurchase.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent availableOrdersIntent = new Intent(MainActivity.this, PurchaseAvailabeOrdersActivity.class);
				startActivity(availableOrdersIntent);
			}
		});
		
		Button activePurchases = (Button) findViewById(R.id.activePurchasesButton);
		activePurchases.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent activePurchasesIntent = new Intent(MainActivity.this, ActivePurchasesActivity.class);
				startActivity(activePurchasesIntent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.logout:
				if(api != null)
					new LogoutAsyncTask().execute(api);
				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class MemberTypeAsyncTask extends AsyncTask<Gas, Void, Integer>{

		@Override
		protected Integer doInBackground(Gas... params) {
			return params[0].userOperations().getMemberType();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			memberType = result;
			//FIXME togliere il toast
			Toast.makeText(MainActivity.this, "Member type: " + memberType, Toast.LENGTH_LONG).show();
			switch(memberType){
				case MemberTypes.USER_NORMAL:
					prepareNormalActivity();
					break;
				case MemberTypes.USER_RESP:
					prepareRespActivity();
					break;
				case MemberTypes.USER_ADMIN:
					break;
				case MemberTypes.USER_SUPPLIER:
					prepareSupplierActivity();
					break;
			}
		}
	}
	
	private class LogoutAsyncTask extends AsyncTask<Gas, Void, Void>{

		@Override
		protected Void doInBackground(Gas... params) {
			params[0].logout();
			return null;
		}
		 @Override
		protected void onPostExecute(Void result) {
			holder.destroy();
			holder = null;
			api = null;
			MainActivity.this.finish();
		}
	}
	
	private class GetRespSuppliersAsyncTask extends AsyncTask<Gas, Void, Supplier[]>{

		@Override
		protected Supplier[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.orderOperations().getMySuppliers();
		}
		
		@Override
		protected void onPostExecute(final Supplier[] result) {
			if(result != null && result.length > 0){
				final List<String> supplierNames = new ArrayList<String>();
				for(int i = 0; i < result.length; i++){
					supplierNames.add(result[i].getCompanyName());
				}
				
				Collections.sort(supplierNames, new Comparator<String>() {

					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareToIgnoreCase(rhs);
					}
				});
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Seleziona il fornitore");
				builder.setItems(supplierNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String supplierName = supplierNames.get(which);
						for(int i = 0; i < result.length; i++){
							if(result[i].getCompanyName().equals(supplierName)){
								Intent intent = new Intent(getApplicationContext(), NewOrderActivity.class);
								intent.putExtra("supplier", result[i]);
								startActivity(intent);
								break;
							}
						}
					}
				});
				builder.show();
			}
			else{
				Toast.makeText(MainActivity.this, "Non ci sono fornitori", Toast.LENGTH_LONG).show();
			}
		}
	}
}
