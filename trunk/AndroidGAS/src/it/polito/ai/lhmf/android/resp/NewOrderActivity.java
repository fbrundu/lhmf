package it.polito.ai.lhmf.android.resp;

import it.polito.ai.lhmf.android.ProductDetailsActivity;
import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Product;
import it.polito.ai.lhmf.model.Supplier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewOrderActivity extends Activity implements DatePickerDialog.OnDateSetListener{
	private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private Gas api;
	
	private Supplier supplier;
	
	private Button confirmButton = null;
	private ListView productsListView = null;
	private TextView supplierName = null;
	private TextView noProducts = null;
	
	private EditText orderName = null;
	private EditText orderCloseDate = null;
	
	private SeparatedListAdapter adapter = null;
	
	private List<Integer> chosenProducts = new ArrayList<Integer>();
	
	private Product[] supplierProducts = null;
	
	private Long closeDate = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		supplier = (Supplier) getIntent().getSerializableExtra("supplier");
		if(supplier != null){
			GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
			Connection<Gas> conn = holder.getConnection();
			if(conn != null){
				api  = conn.getApi();
				setContentView(R.layout.new_order);
				
				noProducts = (TextView) findViewById(R.id.no_supplier_products);
				
				productsListView = (ListView) findViewById(R.id.supplierProductsList);
				adapter = new SeparatedListAdapter(this, R.layout.list_header);
				
				supplierName = (TextView) findViewById(R.id.supplier_name);
				
				supplierName.setText(supplier.getCompanyName());
				
				orderName = (EditText) findViewById(R.id.order_name);
				
				orderCloseDate = (EditText) findViewById(R.id.order_close_date);
				
				orderCloseDate.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						int month = cal.get(Calendar.MONTH);
						int day = cal.get(Calendar.DAY_OF_MONTH);
						DatePickerDialog dialog = new DatePickerDialog(NewOrderActivity.this, NewOrderActivity.this, year, month, day + 1);
						dialog.show();
					}
				});
				
				confirmButton = (Button) findViewById(R.id.order_confirm);
				
				confirmButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(closeDate != null){
							String name = orderName.getText().toString();
							if(name != null && !name.equals("")){
								if(chosenProducts.size() > 0){
									new NewOrderAsyncTask().execute(api, supplier.getIdMember(), new ArrayList<Integer>(chosenProducts), name, closeDate);
								}
								else{
									Toast.makeText(NewOrderActivity.this, "Selzionare almeno un prodotto", Toast.LENGTH_LONG).show();
								}
							}
							else{
								Toast.makeText(NewOrderActivity.this, "Impostare il nome dell'ordine", Toast.LENGTH_LONG).show();
							}
							
						}
						else{
							Toast.makeText(NewOrderActivity.this, "Impostare la data di chiusura", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				new GetAvailableSupplierProductsTask().execute(api, supplier.getIdMember());
			}
		}
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	private class GetAvailableSupplierProductsTask extends AsyncTask<Object, Void, Product[]>{

		@Override
		protected Product[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idSupplier = (Integer) params[1];
			return gas.productOperations().getAvailableSupplierProducts(idSupplier);
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
				
				supplierProducts = new Product[result.length];
				int i = 0;
				
				for(Product prod : result){
					CustomAdapter section = sections.get(prod.getCategory().getDescription());
					if(section == null){
						section = new CustomAdapter(NewOrderActivity.this, R.layout.order_product_item, R.id.productName);
						sections.put(prod.getCategory().getDescription(), section);
					}
					section.add(prod);
					
					supplierProducts[i] = prod;
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
	
	private class NewOrderAsyncTask extends AsyncTask<Object, Void, Integer>{

		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Integer idSupplier = (Integer) params[1];
			List<Integer> productIds = (List<Integer>) params[2];
			String name = (String) params[3];
			Long closeDate = (Long) params[4];
			
			return gas.orderOperations().newOrder(idSupplier, productIds, name, closeDate);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(result == null || result <= 0){
				Toast.makeText(NewOrderActivity.this, "Errori nella creazione dell'ordine", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Ordine creato correttamente", Toast.LENGTH_LONG).show();
				NewOrderActivity.this.finish();
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
			
			closeDate = tmpDate.getTime();
			
			orderCloseDate.setText(df.format(tmpDate));
		}
		else
			Toast.makeText(this, "Selezionare una data futura", Toast.LENGTH_LONG).show();
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

			ImageView image;
			TextView name;
			TextView desc;
			
			CheckBox addCheckBox;
			
			final Product product = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.order_product_item, parent, false);
			}
			
			name = (TextView) row.findViewById(R.id.productName);
			desc = (TextView) row.findViewById(R.id.productDescription);
			image = (ImageView) row.findViewById(R.id.productImage);
			
			addCheckBox = (CheckBox) row.findViewById(R.id.order_add_product_checkbox);
			
			name.setText(product.getName());
			
			desc.setText(product.getDescription());
			
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
			
			addCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked)
						chosenProducts.add(product.getIdProduct());
					else
						chosenProducts.remove(product.getIdProduct());
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
