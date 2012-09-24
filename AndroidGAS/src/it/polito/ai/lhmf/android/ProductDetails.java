package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Product;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetails extends Activity {
	private Gas api = null;
	private ImageView iv = null;
	
	private TextView name = null, desc = null, dim = null, measureUnit = null, blockUnits = null, transportCost = null, unitCost = null,
			minUnits = null, maxUnits = null, category = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_details);
		iv = (ImageView)findViewById(R.id.productImage);
		
		name = (TextView) findViewById(R.id.productName);
		desc = (TextView) findViewById(R.id.productDesc);
		dim = (TextView) findViewById(R.id.productDim);
		measureUnit = (TextView) findViewById(R.id.productMeasureUnit);
		blockUnits = (TextView) findViewById(R.id.productBlockUnits);
		transportCost = (TextView) findViewById(R.id.productTransportCost);
		unitCost = (TextView) findViewById(R.id.productUnitCost);
		minUnits = (TextView) findViewById(R.id.productMinUnits);
		maxUnits = (TextView) findViewById(R.id.productMaxUnits);
		category = (TextView) findViewById(R.id.productCategory);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
		
			int idProduct = getIntent().getIntExtra("idProduct", -1);
			if(idProduct != -1)
				new GetProductTask().execute(api, idProduct);
			
		}
	}
	
	private class GetProductTask extends AsyncTask<Object, Void, Product>{

		@Override
		protected Product doInBackground(Object... params) {
			Gas gas = (Gas)params[0];
			Integer idProduct = (Integer) params[1];
			return gas.productOperations().getProduct(idProduct);
		}
		
		@Override
		protected void onPostExecute(Product result) {
			name.setText(result.getName());
			desc.setText(result.getDescription());
			dim.setText(result.getDimension().toString());
			measureUnit.setText(result.getMeasureUnit());
			blockUnits.setText(result.getUnitBlock().toString());
			transportCost.setText(result.getTransportCost().toString());
			unitCost.setText(result.getUnitCost().toString());

			//CHECK esistenza
			minUnits.setText(result.getMinBuy().toString());
			maxUnits.setText(result.getMaxBuy().toString());
			
			category.setText(result.getCategory().getDescription());
			
			//TODO fornitore?
			
			if(!result.getImgPath().equals(Product.DEFAULT_PRODUCT_PICTURE)){
				new GetProductPictureTask().execute(api, result.getImgPath());
			}
			super.onPostExecute(result);
		}
	}
	
	private class GetProductPictureTask extends AsyncTask<Object, Void, byte[]>{

		@Override
		protected byte[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			String url = (String) params[1];
			return gas.productOperations().getProductImage(url);
		}
		
		@Override
		protected void onPostExecute(byte[] result) {
			Bitmap bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
			iv.setImageBitmap(bmp);
			super.onPostExecute(result);
		}
		
	}
}
