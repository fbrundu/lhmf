package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.OrderProduct;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Purchase;
import it.polito.ai.lhmf.model.PurchaseProduct;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class CompletedPurchaseDetailsActivity extends Activity{
	private Gas api;
	
	private Purchase purchase;
	private Order order;
	
	private ListView productsListView = null;
	private TextView userName = null;
	private TextView orderName = null;
	private TextView status = null;
	private TextView noProducts = null;
	
	private View purchaseCostLayout = null;
	private TextView purchaseTotalCost = null;
	
	private SeparatedListAdapter adapter = null;
	
	private PurchaseProduct[] purchaseProducts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		purchase = (Purchase) intent.getParcelableExtra("purchase");
		if(purchase != null){
			order = purchase.getOrder();
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.completed_purchase_details);
				
				noProducts = (TextView) findViewById(R.id.no_purchase_products);
				noProducts.setText("Non ci sono prodotti da visualizzare");
				
				productsListView = (ListView) findViewById(R.id.purchaseProductsList);
				adapter = new SeparatedListAdapter(this, R.layout.list_header);
				
				userName = (TextView) findViewById(R.id.user_name);
				
				userName.setText(purchase.getMember().getName() + " " + purchase.getMember().getSurname());
				
				orderName = (TextView) findViewById(R.id.purchase_order_name);
				orderName.setText(order.getOrderName());
				
				status = (TextView) findViewById(R.id.status_text);
				
				purchaseCostLayout = findViewById(R.id.purchaseCostLayout);
				purchaseTotalCost = (TextView) findViewById(R.id.purchase_tot_cost);
				
				if(purchase.isFailed()){
					status.setText(R.string.failedF);
					status.setTextColor(Color.RED);
					purchaseCostLayout.setVisibility(View.GONE);
				}
				else{
					status.setText(R.string.ok);
					status.setTextColor(Color.GREEN);
					purchaseTotalCost.setText(String.format("%.2f", purchase.getTotCost()));
				}
					
				new GetCompletedPurchaseProductsTask().execute(api, purchase.getIdPurchase(), order.getIdOrder());
			}
		}
	}
	
	private class GetCompletedPurchaseProductsTask extends AsyncTask<Object, Void, PurchaseProduct[]>{

		@Override
		protected PurchaseProduct[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idPurchase = (Integer) params[1];
			Integer idOrder = (Integer) params[2];
			
			PurchaseProduct[] pps = gas.purchaseOperations().getPurchaseProducts(idPurchase);
			if(pps == null || pps.length == 0)
				return null;
			
			OrderProduct[] ops = gas.orderOperations().getOrderProducts(idOrder);
			if(ops == null || ops.length == 0)
				return null;
			
			for(PurchaseProduct pp : pps){
				for(OrderProduct op : ops){
					if(op.getProduct().getIdProduct() == pp.getProduct().getIdProduct()){
						if(op.isFailed())
							pp.setFailed(true);
						break;
					}
				}
			}
			
			return pps;
		}
		
		@Override
		protected void onPostExecute(PurchaseProduct[] res) {
			if(res != null && res.length > 0){
				PurchaseProduct[] result = res;
				
				Map<String, CustomAdapter> sections = new TreeMap<String, CustomAdapter>(new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareToIgnoreCase(rhs);
					}
				});
				
				purchaseProducts = new PurchaseProduct[result.length];
				int i = 0;
				
				for(PurchaseProduct prod : result){
					CustomAdapter section = sections.get(prod.getProduct().getCategory().getDescription());
					if(section == null){
						section = new CustomAdapter(CompletedPurchaseDetailsActivity.this, R.layout.order_product_static, R.id.productName);
						sections.put(prod.getProduct().getCategory().getDescription(), section);
					}
					section.add(prod);
					
					purchaseProducts[i] = prod;
					i++;
				}
				
				for(String sectionName : sections.keySet()){
					adapter.addSection(sectionName, sections.get(sectionName));
				}
				
				productsListView.setAdapter(adapter);
				
				productsListView.setVisibility(View.VISIBLE);
				noProducts.setVisibility(View.GONE);
			}
			else{
				productsListView.setVisibility(View.GONE);
				noProducts.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private class CustomAdapter extends ArrayAdapter<PurchaseProduct>{

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, PurchaseProduct[] objects) {
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
			
			TextView status;
			View costLayout;
			TextView cost;
			TextView boughtAmount;
			
			PurchaseProduct pp = getItem(position);
			final Product product = pp.getProduct();
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.order_product_static, parent, false);
			}
			
			name = (TextView) row.findViewById(R.id.productName);
			desc = (TextView) row.findViewById(R.id.productDescription);
			image = (ImageView) row.findViewById(R.id.productImage);
			
			status = (TextView) row.findViewById(R.id.status_text);
			costLayout = row.findViewById(R.id.cost_layout);
			cost = (TextView) row.findViewById(R.id.order_item_bought_cost);
			boughtAmount = (TextView) row.findViewById(R.id.order_item_bought_amount);
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
			Integer bought = pp.getAmount();
			boughtAmount.setText(bought.toString());
			
			if(pp.isFailed()){
				costLayout.setVisibility(View.GONE);
				status.setText(R.string.failed);
				status.setTextColor(Color.RED);
			}
			else{
				costLayout.setVisibility(View.VISIBLE);
				status.setText(R.string.ok);
				status.setTextColor(Color.GREEN);
				
				cost.setText(String.format("%.2f", bought * product.getUnitCost()));
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
