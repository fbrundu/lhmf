package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.constants.MemberTypes;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Intent;
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
			gasApi = conn.getApi();
			memberType = gasApi.userOperations().getMemberType();
			Toast.makeText(this, "Member type: " + memberType, Toast.LENGTH_LONG).show();
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
				// TODO Auto-generated method stub
				
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
				if(gasApi != null){
					gasApi.logout();
					holder.destroy();
					holder = null;
					gasApi = null;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
