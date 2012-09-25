package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Product;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
			
			final Product product = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.listino_item, parent, false);
			}
			name = (TextView) row.findViewById(R.id.productItemName);
			desc = (TextView) row.findViewById(R.id.productItemDescription);
			image = (ImageView) row.findViewById(R.id.productItemImage);
			checkBox = (CheckBox) row.findViewById(R.id.prductItemCheckbox);
			checkBox.setChecked(product.getAvailability());
			
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final CheckBox checkBox = (CheckBox) v;
					boolean isChecked = checkBox.isChecked();
					
					checkBox.setChecked(!isChecked);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(Listino.this);
					builder.setCancelable(false);
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
							
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							dialog.dismiss();
						}
					});
					if(isChecked){
						builder.setMessage("Vuoi rendere disponibile il prodotto '" + product.getName() + "'?");
						
						builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								new ProductAvailabilityTask().execute(api, product, true, checkBox);
								dialog.cancel();
								dialog.dismiss();
							}
						});
					}
					else{
						builder.setMessage("Vuoi rendere indisponibile il prodotto '" + product.getName() + "'?");
						
						builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int id) {
								new ProductAvailabilityTask().execute(api, product, false, checkBox);
								dialog.cancel();
								dialog.dismiss();
							}
						});
					}
					builder.show();
					
				}
			});
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			if(!product.getImgPath().equals(Product.DEFAULT_PRODUCT_PICTURE)){
				new SingleProductImageTask().execute(api, product.getImgPath(), image);
			}
			
			row.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), ProductDetails.class);
					i.putExtra("idProduct", product.getIdProduct());
					
					startActivity(i);
					
				}
			});
			
			return row;
		}

		
		
	}
	
	private class SingleProductImageTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			String url = (String) params[1];
			ImageView iv = (ImageView) params[2];
			
			Bitmap bmp = null;
			
			byte[] imgBytes = gas.productOperations().getProductImage(url);
			if(imgBytes != null){
				BitmapFactory.Options bmpLoadOptions = new BitmapFactory.Options();
				bmpLoadOptions.inJustDecodeBounds = true;
				
				BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, bmpLoadOptions);
				int imgHeight = bmpLoadOptions.outHeight;
				int imgWidth = bmpLoadOptions.outWidth;
				
				int reqHeight = iv.getHeight();
				int reqWidth = iv.getWidth();
				
				int inSampleSize = 1;
				
				if (imgHeight > reqHeight || imgWidth > reqWidth) {
			        if (imgWidth > imgHeight) {
			            inSampleSize = Math.round((float)imgHeight / (float)reqHeight);
			        } else {
			            inSampleSize = Math.round((float)imgWidth / (float)reqWidth);
			        }
			    }
				
				bmpLoadOptions.inSampleSize = inSampleSize;
				bmpLoadOptions.inJustDecodeBounds = false;
				
				bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length, bmpLoadOptions);
				
			}
			
			Object[] ret = new Object[2];
			ret[0] = bmp;
			ret[1] = iv;
			
			
			return ret;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			Bitmap bmp = (Bitmap) result[0];
			ImageView iv = (ImageView) result[1];
			
			if(bmp != null && iv != null){
				iv.setImageBitmap(bmp);
			}
		}
	}
	
	private class ProductAvailabilityTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Product product = (Product) params[1];
			Boolean available = (Boolean) params[2];
			CheckBox checkBox = (CheckBox) params[3];
			
			Object[] ret = new Object[4];
			
			ret[0] = product;
			
			
			if(available == true)
				ret[1] = gas.productOperations().setProductAvailable(product.getIdProduct());
			else
				ret[1] = gas.productOperations().setProductUnavailable(product.getIdProduct());
			
			ret[2] = available;
			
			ret[3] = checkBox;
			
			return ret;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			Product product = (Product) result[0];
			Integer retValue = (Integer) result[1];
			Boolean available = (Boolean) result[2];
			CheckBox checkBox = (CheckBox) result[3];
			
			if(retValue != null){
				checkBox.setChecked(available);
				product.setAvailability(available);
			}
		}
		
	}
}
