package it.polito.ai.lhmf.android;

import org.springframework.social.connect.Connection;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Product;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Listino extends Activity {
	private ListView productListView = null;
	private ProgressDialog pDialog = null;
	
	private Gas api = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.listino);
			
			productListView = (ListView) findViewById(R.id.productList);
			
			new GetProductsTask().execute(api);
		}
	}
	
	private class GetProductsTask extends AsyncTask<Gas, Void, Product[]>{

		@Override
		protected Product[] doInBackground(Gas... params) {
			Gas gas = params[0];
			return gas.productOperations().getMyProducts();
		}
		
		@Override
		protected void onPostExecute(Product[] result) {
			if(result != null){
				ListAdapter adapter = new CustomAdapter(Listino.this, R.layout.listino_item, R.id.productName, result);
				productListView.setAdapter(adapter);
			}
			//super.onPostExecute(result);
		}
		
	}
	
	private class CustomAdapter extends ArrayAdapter<Product>{

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, Product[] objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TextView name;
			TextView desc;
			ImageView image;
			CheckBox checkBox;
			
			Product product = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.listino_item, parent, false);
			}
			name = (TextView) row.findViewById(R.id.productItemName);
			desc = (TextView) row.findViewById(R.id.productItemDescription);
			image = (ImageView) row.findViewById(R.id.productItemImage);
			checkBox = (CheckBox) row.findViewById(R.id.prductItemCheckbox);
			checkBox.setChecked(product.getAvailability());
			
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Change product availability
					
				}
			});
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			if(!product.getImgPath().equals(Product.DEFAULT_PRODUCT_PICTURE)){
				new SingleProductImageTask().execute(api, product.getImgPath(), image);
			}
			
			return row;
		}

		
		
	}
	
	private class SingleProductImageTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			String url = (String) params[1];
			ImageView iv = (ImageView) params[2];
			
			Object[] ret = new Object[2];
			ret[0] = gas.productOperations().getProductImage(url);
			ret[1] = iv;
			return null;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			byte[] bytes = (byte[]) result[0];
			ImageView iv = (ImageView) result[1];
			
			if(bytes != null && iv != null){
				Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, result.length);
				iv.setImageBitmap(bmp);
			}
		}
	}
}
