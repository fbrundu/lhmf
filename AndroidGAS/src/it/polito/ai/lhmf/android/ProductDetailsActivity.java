package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.android.resp.NewOrderActivity;
import it.polito.ai.lhmf.model.Product;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetailsActivity extends Activity {
	private Gas api = null;
	private ImageView iv = null;
	private ProgressDialog pDialog = null;
	private boolean fromNotify;
	
	private Product product;
	
	private TextView name = null, desc = null, supplier = null, dim = null, measureUnit = null, blockUnits = null, transportCost = null, unitCost = null,
			minUnits = null, maxUnits = null, category = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_details);
		iv = (ImageView)findViewById(R.id.productImage);
		
		name = (TextView) findViewById(R.id.productName);
		desc = (TextView) findViewById(R.id.productDesc);
		supplier = (TextView) findViewById(R.id.productSupplier);
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
			if(idProduct != -1){
				fromNotify = getIntent().getBooleanExtra("fromNotify", false);
				pDialog = ProgressDialog.show(this, "", "Caricamento", true);
				new GetProductTask().execute(api, idProduct);
			}
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(!fromNotify)
			return false;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_order, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(fromNotify && item.getItemId() == R.id.create_order && product != null){
			Intent intent = new Intent(this, NewOrderActivity.class);
			intent.putExtra("supplier", product.getSupplier());
			startActivity(intent);
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
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
			if(result != null){
				product = result;
				name.setText(result.getName());
				desc.setText(result.getDescription());
				supplier.setText(result.getSupplier().getCompanyName());
				dim.setText(result.getDimension().toString());
				measureUnit.setText(result.getMeasureUnit());
				blockUnits.setText(result.getUnitBlock().toString());
				transportCost.setText(result.getTransportCost().toString());
				unitCost.setText(result.getUnitCost().toString());
	
				minUnits.setText(result.getMinBuy());
				maxUnits.setText(result.getMaxBuy());
				
				category.setText(result.getCategory().getDescription());
				
				if(!result.getImgPath().equals(Product.DEFAULT_PRODUCT_PICTURE)){
					new GetProductPictureTask().execute(api, result.getImgPath());
				}
			}
		}
	}
	
	private class GetProductPictureTask extends AsyncTask<Object, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			String url = (String) params[1];
			
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
			
			return bmp;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if(result != null){
				iv.setImageBitmap(result);
			}
			pDialog.dismiss();
		}
		
	}
}
