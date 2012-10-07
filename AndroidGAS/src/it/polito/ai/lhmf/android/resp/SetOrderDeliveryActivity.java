package it.polito.ai.lhmf.android.resp;

import it.polito.ai.lhmf.android.ProductDetailsActivity;
import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Order;
import it.polito.ai.lhmf.model.OrderProduct;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Supplier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SetOrderDeliveryActivity extends Activity implements DatePickerDialog.OnDateSetListener{
	private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private Map<Integer, Integer> boughtAmounts = new HashMap<Integer, Integer>();
	
	private Gas api;
	
	private Order order;
	private Supplier supplier;
	
	private Button confirmButton = null;
	private ListView productsListView = null;
	private TextView supplierName = null;
	private TextView orderName = null;
	private TextView noProducts = null;
	
	private TextView orderTotalCost = null;
	
	private EditText orderDeliveryDate = null;
	
	private SeparatedListAdapter adapter = null;
	
	private OrderProduct[] orderProducts = null;
	
	private Long deliveryDate = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		order = (Order) getIntent().getParcelableExtra("order");
		if(order != null){
			supplier = order.getSupplier();
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.set_order_delivery);
				
				noProducts = (TextView) findViewById(R.id.no_supplier_products);
				
				productsListView = (ListView) findViewById(R.id.orderProductsList);
				adapter = new SeparatedListAdapter(this, R.layout.list_header);
				
				supplierName = (TextView) findViewById(R.id.supplier_name);
				
				supplierName.setText(supplier.getCompanyName());
				
				orderName = (TextView) findViewById(R.id.order_name);
				orderName.setText(order.getOrderName());
				
				orderTotalCost = (TextView) findViewById(R.id.order_cost_text);
				
				orderDeliveryDate = (EditText) findViewById(R.id.order_delivery_date);
				
				orderDeliveryDate.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						int month = cal.get(Calendar.MONTH);
						int day = cal.get(Calendar.DAY_OF_MONTH);
						DatePickerDialog dialog = new DatePickerDialog(SetOrderDeliveryActivity.this, SetOrderDeliveryActivity.this, year, month, day + 1);
						dialog.show();
					}
				});
				
				confirmButton = (Button) findViewById(R.id.order_confirm);
				
				confirmButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(deliveryDate != null){
							new SetDeliveryDateAsyncTask().execute(api, deliveryDate, order.getIdOrder());
						}
						else{
							Toast.makeText(SetOrderDeliveryActivity.this, "Impostare la data di consegna", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				new GetOrderProductsTask().execute(api, order.getIdOrder());
			}
		}
	}
	
	private Float computeTotalCoast() {
		float ret = 0.0f;
		Iterator<Entry<Integer, Integer>> it = boughtAmounts.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Integer> entry = it.next();
			Integer idProduct = entry.getKey();
			Integer amount = entry.getValue();
			for(int i = 0; i < orderProducts.length; i++){
				OrderProduct p = orderProducts[i];
				if(idProduct == p.getProduct().getIdProduct() && !p.isFailed()){
					ret += p.getProduct().getUnitCost() * amount;
					break;
				}
			}
		}
		return ret;
	}
	
	private class SetDeliveryDateAsyncTask extends AsyncTask<Object, Void, Integer>{

		@Override
		protected Integer doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Long deliveryDate = (Long) params[1];
			Integer idOrder = (Integer) params[2];
			
			return gas.orderOperations().setDeliveryDate(idOrder, deliveryDate);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(result == null || result < 1){
				Toast.makeText(SetOrderDeliveryActivity.this, "Errori durante l'impostazione della data di consegna", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Data di consegna impostata correttamente", Toast.LENGTH_LONG).show();
				SetOrderDeliveryActivity.this.finish();
			}
		}
		
	}
	
	private class GetOrderProductsTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idOrder = (Integer) params[1];
			
			OrderProduct[] ops = gas.orderOperations().getOrderProducts(idOrder);
			
			if(ops == null || ops.length == 0)
				return null;
			
			Integer[] productIds = new Integer[ops.length];
			for(int i = 0; i < ops.length; i++)
				productIds[i] = ops[i].getProduct().getIdProduct();
			
			Integer[] amounts = gas.orderOperations().getBoughtAmounts(idOrder, productIds);
			if(amounts == null)
				return null;
			
			Object[] ret = new Object[2];
			ret[0] = ops;
			ret[1] = amounts;
			
			return ret;
		}
		
		@Override
		protected void onPostExecute(Object[] res) {
			if(res != null){
				OrderProduct[] result = (OrderProduct[]) res[0];
				Integer[] amounts = (Integer[]) res[1];
				
				if(result != null && result.length > 0 && amounts != null && amounts.length == result.length){
					Map<String, CustomAdapter> sections = new TreeMap<String, CustomAdapter>(new Comparator<String>() {
						@Override
						public int compare(String lhs, String rhs) {
							return lhs.compareToIgnoreCase(rhs);
						}
					});
					
					orderProducts = new OrderProduct[result.length];
					int i = 0;
					
					for(OrderProduct prod : result){
						CustomAdapter section = sections.get(prod.getProduct().getCategory().getDescription());
						if(section == null){
							section = new CustomAdapter(SetOrderDeliveryActivity.this, R.layout.order_product_static, R.id.productName);
							sections.put(prod.getProduct().getCategory().getDescription(), section);
						}
						section.add(prod);
						
						orderProducts[i] = prod;
						boughtAmounts.put(prod.getProduct().getIdProduct(), amounts[i]);
						i++;
					}
					
					for(String sectionName : sections.keySet()){
						adapter.addSection(sectionName, sections.get(sectionName));
					}
					
					productsListView.setAdapter(adapter);
					
					productsListView.setVisibility(View.VISIBLE);
					noProducts.setVisibility(View.GONE);
					
					orderTotalCost.setText(String.format("%.2f", computeTotalCoast()));
				}
				else{
					productsListView.setVisibility(View.GONE);
					noProducts.setVisibility(View.VISIBLE);
				}
			}
			else{
				productsListView.setVisibility(View.GONE);
				noProducts.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Date now = Calendar.getInstance().getTime();
		
		Date tmpDate = new Date(year - 1900, monthOfYear, dayOfMonth, 0, 0, 0);
		
		if(tmpDate.after(now)){
			tmpDate.setHours(23);
			tmpDate.setMinutes(59);
			tmpDate.setSeconds(59);
			
			tmpDate.setTime(tmpDate.getTime() + 999);
			
			deliveryDate = tmpDate.getTime();
			
			orderDeliveryDate.setText(df.format(tmpDate));
		}
		else
			Toast.makeText(this, "Selezionare una data futura", Toast.LENGTH_LONG).show();
	}
	
	private class CustomAdapter extends ArrayAdapter<OrderProduct>{

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, OrderProduct[] objects) {
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
			
			OrderProduct op = getItem(position);
			final Product product = op.getProduct();
			
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
			
			Integer bought = boughtAmounts.get(product.getIdProduct());
			boughtAmount.setText(bought.toString());
			
			if(op.isFailed()){
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
