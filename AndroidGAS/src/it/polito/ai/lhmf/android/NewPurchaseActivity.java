package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

//FIXME interrompere il task delle quantità già acquistate

public class NewPurchaseActivity extends Activity {
	private ListView purchaseListView = null;
	
	private List<Integer> productsWithMinBuy = new ArrayList<Integer>();
	private List<Integer> minBoughtAmounts = new ArrayList<Integer>();
	
	
	private List<Integer> productsWithMaxBuy = new ArrayList<Integer>();
	private List<Integer> productsAvailability = new ArrayList<Integer>();
	
	private SeparatedListAdapter adapter = null;
	
	private Product[] orderProducts;
	
	private Gas api = null;
	
	private int orderId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		orderId = getIntent().getIntExtra("orderId", -1);
		if(orderId != -1){
		
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.new_purchase);
				
				purchaseListView = (ListView) findViewById(R.id.newPurchaseList);
				adapter = new SeparatedListAdapter(this);
				
				new GetOrderProductsTask().execute(api, orderId);
			}
		}
	}
	
	private class GetOrderProductsTask extends AsyncTask<Object, Void, Product[]>{

		@Override
		protected Product[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer orderId = (Integer) params[1];
			Product[] products = gas.orderOperations().getOrderProducts(orderId);
			List<Product> productList = new ArrayList<Product>();
			
			for(Product prod : products){
				// Availability = TRUE in questo caso significa che il prodotto è acquistabile (non è ancora inserito nella scheda) --> corrisponde al contrario del valore delle checkBox.
				// quando l'utente aggiunge un prodotto cliccando sulla checkBox la disponibilità cambia
				prod.setAvailability(true);
				productList.add(prod);
				
				if(!prod.getMinBuy().equals(Product.NO_MIN_MAX)){
					productsWithMinBuy.add(prod.getIdProduct());
					minBoughtAmounts.add(Integer.valueOf(0));
				}
				
				if(!prod.getMaxBuy().equals(Product.NO_MIN_MAX)){
					productsWithMaxBuy.add(prod.getIdProduct());
					productsAvailability.add(Integer.valueOf(0));
				}
			}
			
			Collections.sort(productList, new Comparator<Product>() {

				@Override
				public int compare(Product lhs, Product rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});
			
			return productList.toArray(new Product[0]);
		}
		
		@Override
		protected void onPostExecute(Product[] result) {
			if(result != null && result.length > 0){
				Map<String, CustomAdapter> sections = new TreeMap<String, CustomAdapter>(new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareToIgnoreCase(rhs);
					}
				});
				
				orderProducts = new Product[result.length];
				int i = 0;
				
				for(Product prod : result){
					CustomAdapter section = sections.get(prod.getCategory().getDescription());
					if(section == null){
						section = new CustomAdapter(NewPurchaseActivity.this, R.layout.purchase_item, R.id.purchaseItemName);
						sections.put(prod.getCategory().getDescription(), section);
					}
					section.add(prod);
					
					orderProducts[i] = prod;
					i++;
				}
				
				for(String sectionName : sections.keySet()){
					adapter.addSection(sectionName, sections.get(sectionName));
				}
				
				purchaseListView.setAdapter(adapter);
				
				new GetBoughtAmountsTask().execute(api, orderProducts, orderId);
			}
		}
		
	}
	
	private class GetBoughtAmountsTask extends AsyncTask<Object, Integer[], Void>{

		@Override
		protected Void doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Product[] products = (Product[]) params[1];
			Integer orderId = (Integer) params[2];
			
			Integer[] productIds = new Integer[products.length];
			for(int i = 0; i < products.length; i++)
				productIds[i] = products[i].getIdProduct();
			
			while(!isCancelled()){
				Integer[] amounts = gas.orderOperations().getBoughtAmounts(orderId, productIds);
				if(amounts != null){
					this.publishProgress(amounts);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer[]... values) {
			boolean modified = false;
			Integer[] update = values[0];
			for(int i = 0; i < update.length; i++){
				Product current = orderProducts[i];
				Integer id = current.getIdProduct();
				if(productsWithMinBuy.contains(id)){
					int index = productsWithMinBuy.indexOf(id);
					minBoughtAmounts.remove(index);
					minBoughtAmounts.add(index, update[i]);
					modified = true;
				}
				
				if(productsWithMaxBuy.contains(current.getIdProduct())){
					int newAvailability = Integer.parseInt(current.getMaxBuy()) - update[i];
					
					int index = productsWithMaxBuy.indexOf(id);
					productsAvailability.remove(index);
					productsAvailability.add(index, newAvailability);
					
					modified = true;
				}
			}
			
			if(modified)
				adapter.notifyDataSetChanged();
		}
	}
	
	private class CustomAdapter extends ArrayAdapter<Product>{

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, Product[] objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		public CustomAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TextView name;
			TextView desc;
			View availabilityLayout;
			TextView availabilityTextView;
			ImageView image;
			CheckBox checkBox;
			ProgressBar pb;
			EditText amount;
			
			final Product product = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.purchase_item, parent, false);
			}
			name = (TextView) row.findViewById(R.id.purchaseItemName);
			desc = (TextView) row.findViewById(R.id.purchaseItemDescription);
			image = (ImageView) row.findViewById(R.id.purchaseItemImage);
			
			availabilityLayout = row.findViewById(R.id.purchaseAvailabilityLayout);
			availabilityLayout.setVisibility(View.GONE);
			
			availabilityTextView = (TextView) row.findViewById(R.id.purchaseItemAvailability);
			
			pb = (ProgressBar) row.findViewById(R.id.purchaseItemMinProgress);
			pb.setVisibility(View.GONE);
			
			amount = (EditText) row.findViewById(R.id.purchaseItemAmount);
			
			checkBox = (CheckBox) row.findViewById(R.id.purchaseItemCheckbox);
			checkBox.setChecked(!product.getAvailability());
			
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					product.setAvailability(!isChecked);
				}
			});
			
			/*
			checkBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final CheckBox checkBox = (CheckBox) v;
					boolean isChecked = checkBox.isChecked();
					
					checkBox.setChecked(!isChecked);
					
					
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPurchaseActivity.this);
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
			*/
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			if(!product.getMaxBuy().equals(Product.NO_MIN_MAX)){
				availabilityLayout.setVisibility(View.VISIBLE);
				Integer available = productsAvailability.get(productsWithMaxBuy.indexOf(product.getIdProduct()));
				availabilityTextView.setText(available.toString());
			}
			
			if(!product.getMinBuy().equals(Product.NO_MIN_MAX)){
				pb.setVisibility(View.VISIBLE);
				int boughtAmount = minBoughtAmounts.get(productsWithMinBuy.indexOf(product.getIdProduct()));
				int minBuy = Integer.parseInt(product.getMinBuy());
				pb.setMax(minBuy);
				if(boughtAmount - minBuy > 0)
					pb.setProgress(minBuy);
				else
					pb.setProgress(boughtAmount);
				
			}
			
			if(!product.getImgPath().equals(Product.DEFAULT_PRODUCT_PICTURE)){
				new SingleProductImageTask().execute(api, product.getImgPath(), image);
			}
			
			row.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(), ProductDetailsActivity.class);
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
	
//	private class ProductAvailabilityTask extends AsyncTask<Object, Void, Object[]>{
//
//		@Override
//		protected Object[] doInBackground(Object... params) {
//			Gas gas = (Gas) params[0];
//			Product product = (Product) params[1];
//			Boolean available = (Boolean) params[2];
//			CheckBox checkBox = (CheckBox) params[3];
//			
//			Object[] ret = new Object[4];
//			
//			ret[0] = product;
//			
//			
//			if(available == true)
//				ret[1] = gas.productOperations().setProductAvailable(product.getIdProduct());
//			else
//				ret[1] = gas.productOperations().setProductUnavailable(product.getIdProduct());
//			
//			ret[2] = available;
//			
//			ret[3] = checkBox;
//			
//			return ret;
//		}
//		
//		@Override
//		protected void onPostExecute(Object[] result) {
//			Product product = (Product) result[0];
//			Integer retValue = (Integer) result[1];
//			Boolean available = (Boolean) result[2];
//			CheckBox checkBox = (CheckBox) result[3];
//			
//			if(retValue != null){
//				checkBox.setChecked(available);
//				product.setAvailability(available);
//			}
//		}
//		
//	}
}
