package it.polito.ai.lhmf.android;

import it.polito.ai.lhmf.android.admin.LogActivity;
import it.polito.ai.lhmf.android.admin.ProductsActivity;
import it.polito.ai.lhmf.android.admin.UsersActivity;
import it.polito.ai.lhmf.android.api.GASConnectionFactory;
import it.polito.ai.lhmf.android.api.Gas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int PRODUCT_PICTURE_REQUEST_CODE = 100;
	
	private ImageView iv;
	private Uri fileUri = null;
	private boolean capturedImage = false;
	private Integer memberType = null;
	private Gas gasApi = null;
	private ConnectionRepository repo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TabHost th = getTabHost();
		
		Context ctx = getApplicationContext();
        SQLiteOpenHelper repositoryHelper = new SQLiteConnectionRepositoryHelper(ctx);
        ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
        String appId="androidGas";
        GASConnectionFactory factory = new GASConnectionFactory(appId);
        connectionFactoryRegistry.addConnectionFactory(factory);
        TextEncryptor enc = AndroidEncryptors.noOpText();
		
		repo = new SQLiteConnectionRepository(repositoryHelper, connectionFactoryRegistry, enc);
		
		Connection<Gas> conn = repo.findPrimaryConnection(Gas.class);
		if(conn == null){
			//TODO user login
		}
		else{
			gasApi = conn.getApi();
			memberType = gasApi.userOperations().getMemberType();
		}
		
		
		//TODO check user role
//		TabSpec logSpec = th.newTabSpec("Consultazione log");
//		logSpec.setIndicator("Consultazione log", getResources().getDrawable(android.R.drawable.ic_menu_agenda));
//		Intent logIntent = new Intent(this, LogActivity.class);
//		logSpec.setContent(logIntent);
//		
//		TabSpec userSpec = th.newTabSpec("Gestione utenti");
//		userSpec.setIndicator("Gestione utenti");
//		Intent userIntent = new Intent(this, UsersActivity.class);
//		userSpec.setContent(userIntent);
//		
//		TabSpec productsSpec = th.newTabSpec("Prodotti");
//		productsSpec.setIndicator("Prodotti");
//		Intent productIntent = new Intent(this, ProductsActivity.class);
//		productsSpec.setContent(productIntent);
//		
//		th.addTab(logSpec);
//		th.addTab(userSpec);
//		th.addTab(productsSpec);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(gasApi != null){
			gasApi.logout();
			repo.removeConnections("gas");
			gasApi = null;
		}
	}
	
	
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.main);
//		
//		iv = (ImageView) findViewById(R.id.imageView1);
//		if(savedInstanceState != null){
//			String uriS = savedInstanceState.getString("imgUri");
//			if(uriS != null)
//				fileUri = Uri.parse(uriS);
//			capturedImage = savedInstanceState.getBoolean("newImage");
//			if(capturedImage == true && uriS != null){
//				iv.setImageURI(fileUri);
//			}
//		}
//		
//		ImageButton cameraButton = (ImageButton)findViewById(R.id.cameraButton);
//		cameraButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(fileUri != null){
//					try {
//						File f = new File(new URI(fileUri.toString()));
//						f.delete();
//					} catch (URISyntaxException e) {
//						//
//					} finally {
//						fileUri = null;
//					}
//				}
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				
//				fileUri = getOutputMediaFileUri();
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//				
//				capturedImage = false;
//				startActivityForResult(intent, PRODUCT_PICTURE_REQUEST_CODE);
//			}
//		});
//		
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
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == PRODUCT_PICTURE_REQUEST_CODE){
//			if(resultCode == RESULT_OK){
//				capturedImage = true;
//				iv.setImageURI(fileUri);
//			}
//			/*
//			else if(resultCode == RESULT_CANCELED){
//				// User cancelled the image capture
//			}
//			else{
//				// Image capture failed, advise user
//			}
//			*/
//		}
//	}
//	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if(fileUri != null)
//			outState.putString("imgUri", fileUri.toString());
//		outState.putBoolean("newImage", capturedImage);
//	}
//	
//	/** Create a file Uri for saving an image or video */
//	private Uri getOutputMediaFileUri(){
//	      return Uri.fromFile(getOutputMediaFile());
//	}
//
//	/** Create a File for saving an image or video */
//	private File getOutputMediaFile(){
//	    // To be safe, you should check that the SDCard is mounted
//	    // using Environment.getExternalStorageState() before doing this.
//
//	    File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GasProductsPictures");
//	    // This location works best if you want the created images to be shared
//	    // between applications and persist after your app has been uninstalled.
//
//	    // Create the storage directory if it does not exist
//	    if (! mediaStorageDir.exists()){
//	        if (! mediaStorageDir.mkdirs()){
//	            Log.d(TAG, "failed to create directory");
//	            return null;
//	        }
//	    }
//
//	    // Create a media file name
//	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//	        "GAS_"+ timeStamp + ".jpg");
//
//	    return mediaFile;
//	}
//	
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		if(fileUri != null){
//			try {
//				File f = new File(new URI(fileUri.toString()));
//				f.delete();
//			} catch (URISyntaxException e) {
//				//
//			} finally {
//				fileUri = null;
//			}
//		}
//	}
}
