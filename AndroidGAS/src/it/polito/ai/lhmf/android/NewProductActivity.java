package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.ProductCategory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewProductActivity extends Activity {
	private static final String TAG = NewProductActivity.class.getSimpleName();
	private static final int PRODUCT_CAMERA_REQUEST_CODE = 100;
	private static final int PRODUCT_GALLERY_REQUEST_CODE = 200;
	
	private static final int CREATE_NEW_PRODUCT_CATEGORY = -1;
	
	private ImageView iv;
	
	private EditText name;
	private EditText desc;
	private EditText dim;
	private EditText measure;
	private EditText block;
	private EditText transportCost;
	private EditText unitCost;
	private EditText minUnits;
	private EditText maxUnits;
	
	
	private Spinner catChoose;
	private ArrayAdapter<ProductCategory> adapter;
	private TextView newCategoryLabel;
	private EditText newCategoryDescription;
	
	private Uri fileUri = null;
	
	private boolean capturedImage = false;
	private boolean fromGallery = false;
	private boolean newCat = false;
	
	private Gas api = null;
	
	private ArrayList<ProductCategory> categories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GasConnectionHolder holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api = conn.getApi();
			
			categories = new ArrayList<ProductCategory>();
			ProductCategory[] existingCategories = api.productOperations().getProductCategories();
			for(int i = 0; i < existingCategories.length; i++)
				categories.add(existingCategories[i]);
			ProductCategory createNewProductCategory = new ProductCategory();
			createNewProductCategory.setIdProductCategory(CREATE_NEW_PRODUCT_CATEGORY);
			createNewProductCategory.setDescription(getResources().getString(R.string.create_new_prod_cat));
			categories.add(createNewProductCategory);
			
			setContentView(R.layout.new_product);
			
			name = (EditText) findViewById(R.id.newProductName);
			desc = (EditText) findViewById(R.id.newProductDesc);
			dim = (EditText) findViewById(R.id.newProductDim);
			measure = (EditText) findViewById(R.id.newProductMeasureUnit);
			block = (EditText) findViewById(R.id.newProductBlockUnits);
			transportCost = (EditText) findViewById(R.id.newProductTransportCost);
			unitCost = (EditText) findViewById(R.id.newProductUnitCost);
			minUnits = (EditText) findViewById(R.id.newProductMinUnits);
			maxUnits = (EditText) findViewById(R.id.newProductMaxUnits);
			
			
			newCategoryLabel = (TextView) findViewById(R.id.newProductCatDescriptionLabel);
			newCategoryDescription = (EditText) findViewById(R.id.newProduct_CategoryDesciptionInput);
			
			catChoose = (Spinner) findViewById(R.id.newProduct_categoriesSpinner);
			adapter = new ArrayAdapter<ProductCategory>(this, android.R.layout.simple_spinner_item, categories);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			catChoose.setAdapter(adapter);
			catChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					ProductCategory selected = (ProductCategory) parent.getItemAtPosition(position);
					if(selected.getIdProductCategory() == CREATE_NEW_PRODUCT_CATEGORY){
						newCat = true;
						newCategoryLabel.setVisibility(View.VISIBLE);
						newCategoryDescription.setVisibility(View.VISIBLE);
						newCategoryDescription.requestFocus();
					}
					else{
						newCat = false;
						newCategoryLabel.setVisibility(View.GONE);
						newCategoryDescription.setVisibility(View.GONE);
					}
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					return;
				}
			});
			
			iv = (ImageView) findViewById(R.id.newProductImage);
			if(savedInstanceState != null){
				String uriS = savedInstanceState.getString("imgUri");
				if(uriS != null)
					fileUri = Uri.parse(uriS);
				capturedImage = savedInstanceState.getBoolean("newImage");
				if(capturedImage == true && uriS != null){
					iv.setImageURI(fileUri);
				}
			}
			
			ImageButton cameraButton = (ImageButton) findViewById(R.id.newProductTakePicture);
			cameraButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(fileUri != null){
						try {
							if(!fromGallery ){
								File f = new File(new URI(fileUri.toString()));
								f.delete();
							}
						} catch (URISyntaxException e) {
							//
						} finally {
							fileUri = null;
						}
					}
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					fileUri = getOutputMediaFileUri();
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					
					capturedImage = false;
					startActivityForResult(intent, PRODUCT_CAMERA_REQUEST_CODE);
				}
			});
			
			ImageButton galleryButton = (ImageButton) findViewById(R.id.newProductChoosePicture);
			galleryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(fileUri != null){
						try {
							if(!fromGallery ){
								File f = new File(new URI(fileUri.toString()));
								f.delete();
							}
						} catch (URISyntaxException e) {
							//
						} finally {
							fileUri = null;
						}
					}
					
					capturedImage = false;
					
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					startActivityForResult(intent, PRODUCT_GALLERY_REQUEST_CODE);
				}
			});
			
			Button confirmButton = (Button) findViewById(R.id.newProductConfirm);
			confirmButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean check = checkParameters();
					if(check == true){
						ProductCategory cat = null;
						boolean errors = false;
						if(newCat){
							Integer newCategoryId = api.productOperations().newProductCategory(newCategoryDescription.getText().toString());
							if(newCategoryId != -1){
								ProductCategory newCat = new ProductCategory();
								newCat.setIdProductCategory(newCategoryId);
								newCat.setDescription(newCategoryDescription.getText().toString());
								adapter.insert(newCat, adapter.getCount() - 1);
								
								cat = newCat;
							}
							else
								errors = true;
						}
						else{
							cat = (ProductCategory) catChoose.getSelectedItem();
						}
						
						if(!errors){
							Integer newProductId = api.productOperations().newProduct(name.getText().toString(), desc.getText().toString(),
									dim.getText().toString(), measure.getText().toString(), block.getText().toString(),
									transportCost.getText().toString(), unitCost.getText().toString(),
									minUnits.getText().toString(), maxUnits.getText().toString(), cat, fileUri);
							if(newProductId != -1)
								Toast.makeText(NewProductActivity.this, "Prodotto creato correttamente: " + newProductId, Toast.LENGTH_LONG).show();
							else
								Toast.makeText(NewProductActivity.this, "Errori nella creazione prodotto!", Toast.LENGTH_LONG).show();
						}
					}
					else
						Toast.makeText(NewProductActivity.this, "Sono presenti errori nei campi!", Toast.LENGTH_LONG).show();
					
				}
			});
		}
	}
	
	protected boolean checkParameters() {
		String nameString = name.getText().toString();
		String descString = desc.getText().toString();
		Integer dimInt = null;
		String measureUnit = measure.getText().toString();
		Integer unitBlock = null;
		Double transCost = null;
		Double unCost = null;
		Integer minBuy = null;
		Integer maxBuy = null;
		try{
			dimInt = Integer.valueOf(dim.getText().toString());
			unitBlock = Integer.valueOf(block.getText().toString());
			transCost = Double.valueOf(transportCost.getText().toString());
			unCost = Double.valueOf(unitCost.getText().toString());
			if(!minUnits.getText().toString().equals(""))
				minBuy = Integer.valueOf(minUnits.getText().toString());
			if(!maxUnits.getText().toString().equals(""))
				maxBuy = Integer.valueOf(maxUnits.getText().toString());
		} catch (NumberFormatException e) {
			return false;
		}
		
		if(nameString.equals("") || descString.equals("") || measureUnit.equals(""))
			return false;
		if(dimInt <= 0 || unitBlock <= 0 || transCost <= 0.0 || unCost <= 0.0)
			return false;
		
		if(!checkMinMaxBuy(minBuy, maxBuy))
			return false;
		
		if(newCat){
			if(newCategoryDescription.equals(""))
				return false;
		}
		
		return true;
	}
	
	private boolean checkMinMaxBuy(Integer minBuy, Integer maxBuy) {
		return (minBuy == null && (maxBuy == null || maxBuy > 0)) ||
				(minBuy > 0 && (maxBuy == null || maxBuy >= minBuy));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PRODUCT_CAMERA_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				capturedImage = true;
				iv.setImageURI(fileUri);
				fromGallery = false;
			}
		}
		else if(requestCode == PRODUCT_GALLERY_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				capturedImage = true;
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};
				
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex); // file path of selected image
				cursor.close();
				
				File img = new File(filePath);
				fileUri = Uri.fromFile(img);
				iv.setImageURI(fileUri);
				fromGallery = true;
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(fileUri != null)
			outState.putString("imgUri", fileUri.toString());
		outState.putBoolean("newImage", capturedImage);
	}
	
	private Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}

	private File getOutputMediaFile(){
	    File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GasProductsPictures");

	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(TAG, "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "GAS_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(fileUri != null){
			try {
				if(!fromGallery){
					File f = new File(new URI(fileUri.toString()));
					f.delete();
				}
			} catch (URISyntaxException e) {
				//
			} finally {
				fileUri = null;
			}
		}
	}
}
