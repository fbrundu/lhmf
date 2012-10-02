package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.PurchaseProduct;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Purchase;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditPurchaseActivity extends Activity {
	private static final int EDIT_PURCHASE_ITEM_DIALOG = 0;
	private static final int REMOVE_PURCHASE_CONFIRM_DIALOG = EDIT_PURCHASE_ITEM_DIALOG + 1;
	
	private Map<Integer, Integer> myBoughtAmounts = new HashMap<Integer, Integer>();
	
	private TextView orderName = null;
	private TextView purchaseCost = null;
	private TextView orderProgressText = null;
	private ProgressBar orderProgressBar = null;
	private ListView purchaseListView = null;
	private Button backButton = null;
	
	private List<Integer> productsWithMinBuy = new ArrayList<Integer>();
	private List<Integer> minBoughtAmounts = new ArrayList<Integer>();
	
	
	private List<Integer> productsWithMaxBuy = new ArrayList<Integer>();
	private List<Integer> productsAvailability = new ArrayList<Integer>();
	
	private SeparatedListAdapter adapter = null;
	
	private Product[] orderProducts = null;
	
	private Gas api = null;
	
	private Order order;
	private Purchase purchase;
	
	private OrderProgressTask orderProgressTask = null;
	private GetBoughtAmountsTask boughtAmountsTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		purchase = (Purchase) getIntent().getSerializableExtra("purchase");
		if(purchase != null){
			order = purchase.getOrder();
			
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.new_purchase);
				
				purchaseListView = (ListView) findViewById(R.id.newPurchaseList);
				
				orderName = (TextView) findViewById(R.id.purchase_order_name);
				purchaseCost = (TextView) findViewById(R.id.purchase_tot_cost);
				orderProgressText = (TextView) findViewById(R.id.order_progress_percent);
				orderProgressBar = (ProgressBar) findViewById(R.id.order_progress_bar);
				
				orderName.setText(order.getOrderName());
				orderProgressText.setText("" + 0);
				orderProgressBar.setProgress(0);
				
				backButton = (Button) findViewById(R.id.purchase_confirm);
				backButton.setEnabled(false);
				backButton.setVisibility(View.INVISIBLE);
				
				TextView title = (TextView) findViewById(R.id.title);
				title.setText(R.string.edit_purchase_title);
				
				new GetPurchaseAndOrderProductsTask().execute(api, purchase.getIdPurchase(), order.getIdOrder());
			}
		}
	}
	
	private void createListAdapters(){
		SeparatedListAdapter mainAdapter = new SeparatedListAdapter(this, R.layout.list_header_big);
		SeparatedListAdapter boughtAdapter = null;
		SeparatedListAdapter availableAdapter = null;
		
		Map<String, CustomAdapter> boughtSections = new TreeMap<String, CustomAdapter>(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		
		Map<String, CustomAdapter> availableSections = new TreeMap<String, CustomAdapter>(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		
		for(Product product : orderProducts){
			boolean isAvailable = true;
			for(Integer id : myBoughtAmounts.keySet()){
				if(product.getIdProduct() == id){
					isAvailable = false;
					break;
				}
			}
			if(isAvailable){
				CustomAdapter notBoughtSection = availableSections.get(product.getCategory().getDescription());
				if(notBoughtSection == null){
					notBoughtSection = new CustomAdapter(EditPurchaseActivity.this, R.layout.purchase_item, R.id.purchaseItemName);
					availableSections.put(product.getCategory().getDescription(), notBoughtSection);
				}
				notBoughtSection.add(product);
			}
			else{
				CustomAdapter boughtSection = boughtSections.get(product.getCategory().getDescription());
				if(boughtSection == null){
					boughtSection = new CustomAdapter(EditPurchaseActivity.this, R.layout.purchase_item, R.id.purchaseItemName);
					boughtSections.put(product.getCategory().getDescription(), boughtSection);
				}
				boughtSection.add(product);
			}
		}
		
		if(boughtSections.size() > 0){
			boughtAdapter = new SeparatedListAdapter(EditPurchaseActivity.this, R.layout.list_header);
			
			for(String sectionName : boughtSections.keySet()){
				boughtAdapter.addSection(sectionName, boughtSections.get(sectionName));
			}
			
			mainAdapter.addSection("Prodotti acquistati", boughtAdapter);
		}
		
		if(availableSections.size() > 0){
			availableAdapter = new SeparatedListAdapter(EditPurchaseActivity.this, R.layout.list_header);
			
			for(String sectionName : availableSections.keySet()){
				availableAdapter.addSection(sectionName, availableSections.get(sectionName));
			}
			
			mainAdapter.addSection("Prodotti disponibili", availableAdapter);
		}
		
		this.purchaseListView.setAdapter(mainAdapter);
		
		this.adapter = mainAdapter;
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == EDIT_PURCHASE_ITEM_DIALOG){
			LayoutInflater inflater = getLayoutInflater();
			View content = inflater.inflate(R.layout.purchase_item_dialog, null);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setPositiveButton("Ok", null);
			
			builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			builder.setView(content);
			
			AlertDialog dialog = builder.create();
			
			return dialog;
		}
		else if(id == REMOVE_PURCHASE_CONFIRM_DIALOG){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle("Conferma eliminazione scheda");
			
			builder.setMessage("");
			
			builder.setPositiveButton("Ok", null);
			
			builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			return builder.create();
		}
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		
		if(id == EDIT_PURCHASE_ITEM_DIALOG){
			final Product product = (Product) args.getSerializable("product");
			final int availability = args.getInt("availability", -1);
			
			if(product != null){
				
				AlertDialog aDialog = (AlertDialog) dialog;
				
				View availabiltyLayout = aDialog.findViewById(R.id.availability_layout);
				TextView availabilityText = (TextView) aDialog.findViewById(R.id.product_availability);
				
				if(availability != -1){
					availabilityText.setText("" + availability);
					availabiltyLayout.setVisibility(View.VISIBLE);
				}
				else
					availabiltyLayout.setVisibility(View.GONE);
						
				
				TextView name = (TextView) aDialog.findViewById(R.id.product_name);
				final EditText amount = (EditText) aDialog.findViewById(R.id.product_amount);
				
				final Integer alreadyBought = myBoughtAmounts.get(product.getIdProduct());
				if(alreadyBought != null)
					amount.setText(alreadyBought.toString());
				else
					amount.setText("1");
				amount.selectAll();
				
				name.setText(product.getName());
				
				
				aDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String amountString = amount.getText().toString();
						if(!amountString.equals("")){
							int amount = Integer.parseInt(amountString);
							if(amount > 0){
								Integer requiredChange = 0;
								if(alreadyBought != null)
									requiredChange = amount - alreadyBought;
								else
									requiredChange = amount;
								
								if(availability == -1 || requiredChange <= availability){
									boolean createNew = true;
									if(alreadyBought != null)
										createNew = false;
										
									new UpdatePurchaseProductAsyncTask().execute(api, purchase.getIdPurchase(), product.getIdProduct(), amount, createNew);
									dialog.cancel();
								}
								else
									Toast.makeText(EditPurchaseActivity.this, "La quantità scelta è superiore alla disponibilità", Toast.LENGTH_LONG).show();
							}
							else
								Toast.makeText(EditPurchaseActivity.this, "Impostare un valore maggiore di 0", Toast.LENGTH_LONG).show();
						}
						else
							Toast.makeText(EditPurchaseActivity.this, "Impostare un valore maggiore di 0", Toast.LENGTH_LONG).show();
					}
				});
			}
		}
		else if(id == REMOVE_PURCHASE_CONFIRM_DIALOG){
			Product product = (Product) args.getSerializable("product");
			
			AlertDialog aDialog = (AlertDialog) dialog;
			aDialog.setMessage("Rimuovendo il prodotto '" + product.getName() + "' si procedera all'eliminazione dell'intera scheda." +
					" Continuare?");
			
			final Integer idProduct = product.getIdProduct();
			final Integer idPurchase = purchase.getIdPurchase();
			
			aDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new RemovePurchaseProductAsyncTask().execute(api, idPurchase, idProduct, true);
					dialog.cancel();
				}
			});
		}
		else
			super.onPrepareDialog(id, dialog, args);
	}
	
	protected Float computeTotalCoast() {
		float ret = 0.0f;
		Iterator<Entry<Integer, Integer>> it = myBoughtAmounts.entrySet().iterator();
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
	
	private class RemovePurchaseProductAsyncTask extends AsyncTask<Object, Void, Integer[]>{

		@Override
		protected Integer[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idPurchase = (Integer) params[1];
			Integer idProduct = (Integer) params[2];
			
			Integer ret[] = new Integer[2];
			ret[0] = idProduct;
			ret[1] = gas.purchaseOperations().removePurchaseProduct(idPurchase, idProduct);
			
			return ret;
		}
		
		@Override
		protected void onPostExecute(Integer[] result) {
			Integer idProduct = result[0];
			Integer res = result[1];
			
			if(res == null || res < 1){
				Toast.makeText(EditPurchaseActivity.this, "Errore durante la modifica della scheda", Toast.LENGTH_LONG).show();
			}
			else{
				myBoughtAmounts.remove(idProduct);
				if(myBoughtAmounts.size() == 0)
					EditPurchaseActivity.this.finish();
				else{
					purchaseCost.setText(String.format("%.2f", computeTotalCoast()));
				
					createListAdapters();
				}
			}
		}
		
	}
	
	private class UpdatePurchaseProductAsyncTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idPurchase = (Integer) params[1];
			Integer idProduct = (Integer) params[2];
			Integer amount = (Integer) params[3];
			Boolean createNew = (Boolean) params[4];
			
			Object[] retValue = new Object[4];
			retValue[0] = idProduct;
			retValue[1] = amount;
			retValue[2] = createNew;
			
			if(createNew)
				retValue[3] = gas.purchaseOperations().newPurchaseProduct(idPurchase, idProduct, amount);
			else
				retValue[3] = gas.purchaseOperations().updatePurchaseProduct(idPurchase, idProduct, amount);
			
			return retValue;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			Integer idProduct = (Integer) result[0];
			Integer amount = (Integer) result[1];
			Boolean createNew = (Boolean) result[2];
			Integer res = (Integer) result[3];
			
			if(res == null || res < 1){
				Toast.makeText(EditPurchaseActivity.this, "Errore durante la modifica della scheda", Toast.LENGTH_LONG).show();
			}
			else{
				myBoughtAmounts.put(idProduct, amount);
				purchaseCost.setText(String.format("%.2f", computeTotalCoast()));
				if(createNew)
					createListAdapters();
				else
					adapter.notifyDataSetChanged();
			}
			
			super.onPostExecute(result);
		}
		
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
	
	private class GetPurchaseAndOrderProductsTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer purchaseId = (Integer) params[1];
			Integer orderId = (Integer) params[2];
			
			PurchaseProduct[] purchaseProducts = gas.purchaseOperations().getPurchaseProductsForNormal(purchaseId);
			
			Product[] orderProducts = gas.orderOperations().getOrderProducts(orderId);
			List<Product> productList = new ArrayList<Product>();
			
			for(Product prod : orderProducts){
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
			
			
			Object[] ret = new Object[2];
			ret[0] = purchaseProducts;
			ret[1] = productList.toArray(new Product[0]);
			return ret;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			PurchaseProduct[] bought = (PurchaseProduct[]) result[0];
			Product[] oProducts = (Product[]) result[1];
			
			if(bought != null && oProducts != null &&  bought.length > 0 && oProducts.length > 0){
				orderProducts = new Product[oProducts.length];
				int i = 0;
				for(Product p : oProducts){
					orderProducts[i] = p;
					i++;
				}
				
				for(PurchaseProduct pp : bought){
					Product bp = pp.getProduct();
					
					myBoughtAmounts.put(bp.getIdProduct(), pp.getAmount());
				}
				
				purchaseCost.setText(String.format("%.2f", computeTotalCoast()));
				
				createListAdapters();
				
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
			}
			
			if(modified)
				adapter.notifyDataSetChanged();
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
			
			TextView myBoughtAmount;
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
			myBoughtAmount = (TextView) row.findViewById(R.id.purchaseItemDesiredAmount);
			partialCost = (TextView) row.findViewById(R.id.purchase_item_bought_cost);
			
			availabilityLayout = row.findViewById(R.id.purchaseAvailabilityLayout);
			availabilityLayout.setVisibility(View.GONE);
			
			availabilityTextView = (TextView) row.findViewById(R.id.purchaseItemAvailability);
			
			progressLayout = row.findViewById(R.id.purchaseProductProgressLayout);
			pb = (ProgressBar) row.findViewById(R.id.purchaseItemMinProgress);
			productProgress = (TextView) row.findViewById(R.id.product_progress_text);
			maxProductProgress = (TextView) row.findViewById(R.id.product_max_progress);
			
			addButton = (ImageButton) row.findViewById(R.id.purchase_item_add);
			
			editPurchaseContainer = row.findViewById(R.id.purchase_product_edit_container);
			removeButton = (ImageButton) row.findViewById(R.id.purchase_item_remove);
			editButton = (ImageButton) row.findViewById(R.id.purchase_item_edit);
			
			if(defaultTextViewColor == null)
				defaultTextViewColor = myBoughtAmount.getTextColors();
			
			progressLayout.setVisibility(View.GONE);
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			Integer bought = myBoughtAmounts.get(product.getIdProduct());
			if(bought != null){
				myBoughtAmount.setText(bought.toString());
				myBoughtAmount.setTextColor(Color.GREEN);
				
				float cost = product.getUnitCost() * bought;
				
				partialCost.setText(String.format("%.2f", cost));
				
				editPurchaseContainer.setVisibility(View.VISIBLE);
				addButton.setVisibility(View.GONE);
			}
			else{
				editPurchaseContainer.setVisibility(View.GONE);
				addButton.setVisibility(View.VISIBLE);
				addButton.setEnabled(true);
				myBoughtAmount.setText("0");
				myBoughtAmount.setTextColor(defaultTextViewColor);
				partialCost.setText("0");
			}
			
			if(!product.getMaxBuy().equals(Product.NO_MIN_MAX)){
				availabilityLayout.setVisibility(View.VISIBLE);
				available = productsAvailability.get(productsWithMaxBuy.indexOf(product.getIdProduct()));
				availabilityTextView.setText(available.toString());
				
				if(bought == null && available == 0)
					addButton.setEnabled(false);
			}
			
			final Integer finalAvailable = available;
			
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
			
			/**
			 * final Product product = (Product) args.getSerializable("product");
				final int availability = args.getInt("availability", -1);
			 */
			
			addButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					
					args.putSerializable("product", product);
					if(finalAvailable != null)
						args.putInt("availability", finalAvailable);
					
					showDialog(EDIT_PURCHASE_ITEM_DIALOG, args);
					
				}
			});
			
			editButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					
					args.putSerializable("product", product);
					if(finalAvailable != null)
						args.putInt("availability", finalAvailable);
					
					showDialog(EDIT_PURCHASE_ITEM_DIALOG, args);
				}
			});
			
			removeButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(myBoughtAmounts.size() == 1){
						//This is the last product. Ask user if he wants to remove the purchase
						Bundle args = new Bundle();
						
						args.putSerializable("product", product);
						showDialog(REMOVE_PURCHASE_CONFIRM_DIALOG, args);
					}
					else
						new RemovePurchaseProductAsyncTask().execute(api, purchase.getIdPurchase(), product.getIdProduct());
					
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
