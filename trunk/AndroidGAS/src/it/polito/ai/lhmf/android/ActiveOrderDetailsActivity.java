package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActiveOrderDetailsActivity extends Activity {
	private Map<Integer, Integer> totalBoughtAmounts = new HashMap<Integer, Integer>();
	
	private TextView orderName = null;
	private TextView orderCost = null;
	private TextView orderProgressText = null;
	private ProgressBar orderProgressBar = null;
	private ListView productsListView = null;
	private Button backButton = null;
	
	private List<Integer> productsWithMinBuy = new ArrayList<Integer>();
	private List<Integer> minBoughtAmounts = new ArrayList<Integer>();
	
	
	private List<Integer> productsWithMaxBuy = new ArrayList<Integer>();
	private List<Integer> productsAvailability = new ArrayList<Integer>();
	
	private SeparatedListAdapter adapter = null;
	
	private Product[] orderProducts = null;
	
	private Gas api = null;
	
	private Order order;
	
	private OrderProgressTask orderProgressTask = null;
	private GetBoughtAmountsTask boughtAmountsTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		order = (Order) getIntent().getParcelableExtra("order");
		if(order != null){
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.new_purchase);
				
				productsListView = (ListView) findViewById(R.id.newPurchaseList);
				adapter = new SeparatedListAdapter(this, R.layout.list_header);
				
				orderName = (TextView) findViewById(R.id.purchase_order_name);
				orderCost = (TextView) findViewById(R.id.purchase_tot_cost);
				orderProgressText = (TextView) findViewById(R.id.order_progress_percent);
				orderProgressBar = (ProgressBar) findViewById(R.id.order_progress_bar);
				
				orderName.setText(order.getOrderName());
				orderProgressText.setText("" + 0);
				orderProgressBar.setProgress(0);
				
				backButton = (Button) findViewById(R.id.purchase_confirm);
				backButton.setEnabled(false);
				backButton.setVisibility(View.INVISIBLE);
				
				TextView title = (TextView) findViewById(R.id.title);
				title.setText(R.string.order_details_title);
				
				new GetOrderProductsTask().execute(api, order.getIdOrder());
			}
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			if(orderProgressTask == null){
				orderProgressTask= new OrderProgressTask();
				orderProgressTask.execute(api, order.getIdOrder());
			}
			
			if(boughtAmountsTask == null && orderProducts != null && orderProducts.length > 0){
				boughtAmountsTask = new GetBoughtAmountsTask();
				boughtAmountsTask.execute(api, orderProducts, order.getIdOrder());
			}
		}
			
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if(orderProgressTask != null){
			orderProgressTask.cancel(false);
			orderProgressTask = null;
		}
		
		if(boughtAmountsTask != null){
			boughtAmountsTask.cancel(false);
			boughtAmountsTask = null;
		}
		super.onPause();
	}
	
	protected Float computeTotalCoast() {
		float ret = 0.0f;
		Iterator<Entry<Integer, Integer>> it = totalBoughtAmounts.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Integer> entry = it.next();
			Integer idProduct = entry.getKey();
			Integer amount = entry.getValue();
			for(int i = 0; i < orderProducts.length; i++){
				Product p = orderProducts[i];
				if(idProduct == p.getIdProduct()){
					ret += p.getUnitCost() * amount;
					break;
				}
			}
		}
		return ret;
	}

	private class OrderProgressTask extends AsyncTask<Object, Float, Void>{

		@Override
		protected Void doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idOrder = (Integer) params[1];
			
			while(!isCancelled()){
				Float progress = gas.orderOperations().getOrderProgress(idOrder);
				if(progress != null)
					publishProgress(progress);
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
		protected void onProgressUpdate(Float... values) {
			int progress = values[0].intValue();
			orderProgressBar.setProgress(progress);
			orderProgressText.setText(String.format("%.2f", values[0]));
		}
	}
	
	private class GetOrderProductsTask extends AsyncTask<Object, Void, Product[]>{

		@Override
		protected Product[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer orderId = (Integer) params[1];
			Product[] products = gas.orderOperations().getProducts(orderId);
			List<Product> productList = new ArrayList<Product>();
			
			for(Product prod : products){
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
						section = new CustomAdapter(ActiveOrderDetailsActivity.this, R.layout.purchase_item, R.id.purchaseItemName);
						sections.put(prod.getCategory().getDescription(), section);
					}
					section.add(prod);
					
					orderProducts[i] = prod;
					i++;
				}
				
				for(String sectionName : sections.keySet()){
					adapter.addSection(sectionName, sections.get(sectionName));
				}
				
				productsListView.setAdapter(adapter);
				
				if(boughtAmountsTask == null){
					boughtAmountsTask = new GetBoughtAmountsTask();
					boughtAmountsTask.execute(api, orderProducts, order.getIdOrder());
				}
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
				totalBoughtAmounts.put(id, update[i]);
			}
			
			if(modified)
				adapter.notifyDataSetChanged();
			
			orderCost.setText(String.format("%.2f", computeTotalCoast()));
		}
	}
	
	private class CustomAdapter extends ArrayAdapter<Product>{
		private ColorStateList defaultTextViewColor = null;
		
		
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

			ImageView image;
			TextView name;
			TextView desc;
			
			TextView totalBoughtAmount;
			TextView partialCost;
			
			View availabilityLayout;
			TextView availabilityTextView;
			
			View progressLayout;
			ProgressBar pb;
			TextView productProgress;
			TextView maxProductProgress;
			
			ImageButton addButton;
			
			View editPurchaseContainer;
			ImageButton removeButton;
			ImageButton editButton;
			
			final Product product = getItem(position);
			Integer available = null;
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.purchase_item, parent, false);
			}
			
			TextView boughtAmountLabel = (TextView) row.findViewById(R.id.purchase_item_bought_amount_label);
			boughtAmountLabel.setText("Quantità acquistata: ");
			
			name = (TextView) row.findViewById(R.id.purchaseItemName);
			desc = (TextView) row.findViewById(R.id.purchaseItemDescription);
			image = (ImageView) row.findViewById(R.id.purchaseItemImage);
			totalBoughtAmount = (TextView) row.findViewById(R.id.purchaseItemDesiredAmount);
			partialCost = (TextView) row.findViewById(R.id.purchase_item_bought_cost);
			
			availabilityLayout = row.findViewById(R.id.purchaseAvailabilityLayout);
			availabilityLayout.setVisibility(View.GONE);
			
			availabilityTextView = (TextView) row.findViewById(R.id.purchaseItemAvailability);
			
			progressLayout = row.findViewById(R.id.purchaseProductProgressLayout);
			pb = (ProgressBar) row.findViewById(R.id.purchaseItemMinProgress);
			productProgress = (TextView) row.findViewById(R.id.product_progress_text);
			maxProductProgress = (TextView) row.findViewById(R.id.product_max_progress);
			
			addButton = (ImageButton) row.findViewById(R.id.purchase_item_add);
			addButton.setEnabled(false);
			addButton.setVisibility(View.INVISIBLE);
			
			editPurchaseContainer = row.findViewById(R.id.purchase_product_edit_container);
			removeButton = (ImageButton) row.findViewById(R.id.purchase_item_remove);
			removeButton.setEnabled(false);
			editButton = (ImageButton) row.findViewById(R.id.purchase_item_edit);
			editButton.setEnabled(false);
			editPurchaseContainer.setVisibility(View.GONE);
			
			if(defaultTextViewColor == null)
				defaultTextViewColor = totalBoughtAmount.getTextColors();
			
			progressLayout.setVisibility(View.GONE);
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			Integer bought = totalBoughtAmounts.get(product.getIdProduct());
			if(bought != null && bought > 0){
				totalBoughtAmount.setText(bought.toString());
				totalBoughtAmount.setTextColor(Color.GREEN);
				
				float cost = product.getUnitCost() * bought;
				
				partialCost.setText(String.format("%.2f", cost));
			}
			else{
				totalBoughtAmount.setText("0");
				totalBoughtAmount.setTextColor(defaultTextViewColor);
				partialCost.setText("0");
			}
			
			if(!product.getMaxBuy().equals(Product.NO_MIN_MAX)){
				availabilityLayout.setVisibility(View.VISIBLE);
				available = productsAvailability.get(productsWithMaxBuy.indexOf(product.getIdProduct()));
				availabilityTextView.setText(available.toString());
			}
			
			if(!product.getMinBuy().equals(Product.NO_MIN_MAX)){
				progressLayout.setVisibility(View.VISIBLE);
				int boughtAmount = minBoughtAmounts.get(productsWithMinBuy.indexOf(product.getIdProduct()));
				int minBuy = Integer.parseInt(product.getMinBuy());
				pb.setMax(minBuy);
				maxProductProgress.setText("" + minBuy);
				if(boughtAmount - minBuy > 0){
					pb.setProgress(minBuy);
					productProgress.setText("" + minBuy);
				}
				else{
					pb.setProgress(boughtAmount);
					productProgress.setText("" + boughtAmount);
				}
				
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
}
