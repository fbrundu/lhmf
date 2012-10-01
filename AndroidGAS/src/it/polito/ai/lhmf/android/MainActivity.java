package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.constants.MemberTypes;

import org.springframework.social.connect.Connection;

import android.app.Activity;
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
	private Gas gasApi = null;
	
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
			Intent dummyIntent = new Intent(getApplicationContext(), ActivePurchasesActivity.class);
			startActivity(dummyIntent);
			/* TODO
			gasApi = conn.getApi();
			
			new MemberTypeAsyncTask().execute(gasApi);
			*/
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
				if(gasApi != null)
					new LogoutAsyncTask().execute(gasApi);
				
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
					break;
				case MemberTypes.USER_RESP:
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
			gasApi = null;
			MainActivity.this.finish();
		}
	}
}
