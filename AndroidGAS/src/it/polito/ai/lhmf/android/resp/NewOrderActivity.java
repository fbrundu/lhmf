package it.polito.ai.lhmf.android.resp;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.util.SeparatedListAdapter;
import it.polito.ai.lhmf.model.Supplier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
	
	private EditText orderName = null;
	private EditText orderCloseDate = null;
	
	private SeparatedListAdapter adapter = null;
	
	private List<Integer> chosenProducts = new ArrayList<Integer>();
	
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
				
				//new GetSupplierProductsTask().execute(api, supplier.getIdMember());
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
}
