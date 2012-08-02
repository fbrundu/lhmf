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
	private static final String TAG = MainActivity.class.getSimpleName();
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
					// TODO Auto-generated method stub
					
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
									Integer.valueOf(dim.getText().toString()), measure.getText().toString(), Integer.valueOf(block.getText().toString()),
									Float.valueOf(transportCost.getText().toString()), Float.valueOf(unitCost.getText().toString()),
									Integer.valueOf(minUnits.getText().toString()), Integer.valueOf(maxUnits.getText().toString()), cat, fileUri);
							if(newProductId != -1)
								Toast.makeText(NewProductActivity.this, "Prodotto creato correttamente: " + newProductId, Toast.LENGTH_LONG).show();
							else
								Toast.makeText(NewProductActivity.this, "Errori nella creazione prodotto!", Toast.LENGTH_LONG).show();
						}
					}
					
				}
			});
		}
		
//		Button sendButton = (Button) findViewById(R.id.sendButton);
//		sendButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(fileUri != null){
//					int bytesRead, bytesAvailable, bufferSize;
//					byte[] buffer;
//					int maxBufferSize = 1*1024*1024;
//					
//					DataOutputStream outputStream = null;
//					DataInputStream inputStream = null;
//					
//					String lineEnd = "\r\n";
//					String twoHyphens = "--";
//					String boundary =  "*****";
//					
//					URL url = null;
//					try {
//						url = new URL("http://gasproject.net:8080/_lhmf/productPhoto");
//					} catch (MalformedURLException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					HttpURLConnection connection;
//					try {
//						File f = new File(new URI(fileUri.toString()));
//						FileInputStream fileInputStream = new FileInputStream(f);
//						
//						connection = (HttpURLConnection)url.openConnection();
//						connection.setDoInput(true);
//						connection.setDoOutput(true);
//						connection.setUseCaches(false);
//						
//						try {
//							connection.setRequestMethod("POST");
//						} catch (ProtocolException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//						connection.setRequestProperty("Connection", "Keep-Alive");
//						connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
//						
//						outputStream = new DataOutputStream( connection.getOutputStream() );
//						outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//						outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + f.getName() +"\"" + lineEnd);
//						outputStream.writeBytes(lineEnd);
//						
//						bytesAvailable = fileInputStream.available();
//						bufferSize = Math.min(bytesAvailable, maxBufferSize);
//						buffer = new byte[bufferSize];
//						
//						// Read file
//						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//						while (bytesRead > 0)
//						{
//							outputStream.write(buffer, 0, bufferSize);
//							bytesAvailable = fileInputStream.available();
//							bufferSize = Math.min(bytesAvailable, maxBufferSize);
//							bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//						}
//
//						outputStream.writeBytes(lineEnd);
//						outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//						
//						// Responses from the server (code and message)
//						int serverResponseCode = connection.getResponseCode();
//						String serverResponseMessage = connection.getResponseMessage();
//						
//						fileInputStream.close();
//						outputStream.flush();
//						outputStream.close();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (URISyntaxException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//				
//			}
//		});
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
			minBuy = Integer.valueOf(minUnits.getText().toString());
			maxBuy = Integer.valueOf(maxUnits.getText().toString());
		} catch (NumberFormatException e) {
			return false;
		}
		
		if(nameString.equals("") || descString.equals("") || measureUnit.equals(""))
			return false;
		if(dimInt <= 0 || unitBlock <= 0 || transCost <= 0.0 || unCost <= 0.0)
			return false;
		
		//TODO è giusto? sono obbligatori min e max units?
		if(minBuy <= 0 || maxBuy <= 0 || minBuy > maxBuy)
			return false;
		
		if(newCat){
			if(newCategoryDescription.equals(""))
				return false;
		}
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PRODUCT_CAMERA_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				capturedImage = true;
				iv.setImageURI(fileUri);
				fromGallery = false;
			}
			/*
			else if(resultCode == RESULT_CANCELED){
				// User cancelled the image capture
			}
			else{
				// Image capture failed, advise user
			}
			*/
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
	
	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GasProductsPictures");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
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
