package com.tromto.flat;

import static com.tromto.flat.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.tromto.flat.CommonUtilities.SENDER_ID;
import static com.tromto.flat.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import com.tromto.flat.library.DatabaseHandler;
import com.tromto.flat.library.UserFunctions;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	UserFunctions userFunctions;
	Button btnLogout;
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;
	EditText txtName, flat, txtPin;
	EditText txtEmail;
	private ProgressDialog pDialog;
	// Register button
	Button btnRegister, login, searchflat, registeradmin;
	//the other register side
	EditText inputPassword;
	TextView adminre;
	TextView registerErrorMsg;
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_FLAT = "flat";
	private static String KEY_ADMIN = "admin";
	private static String KEY_DUMMY = "dummy";
	private static String KEY_CREATED_AT = "created_at";
	String name, email, flatre, admin, password, dummy, pin, pid;
	int v;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make sure the device has the proper dependencies.
		
		userFunctions = new UserFunctions();
				GCMRegistrar.checkDevice(this);
				// Make sure the manifest was properly set - comment out this line
				// while developing the app, then uncomment it when it's ready.
				GCMRegistrar.checkManifest(this);	
				// Get GCM registration id
				final String regId = GCMRegistrar.getRegistrationId(this);

				// Check if regid already presents
				if (regId.equals("")) {
					// Registration is not present, register now with GCM	

        	
		setContentView(R.layout.activity_register);

		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					getResources().getString(R.string.nointernet), false);
			// stop executing code by return
			return;
		}

		// Check if GCM configuration is set
		if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
				|| SENDER_ID.length() == 0) {
			// GCM sernder id / server url is missing
			alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);
			// stop executing code by return
			 return;
		}

		/*
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent login = new Intent(getApplicationContext(), LoginActivity.class);
	        	login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(login);
	        	// Closing dashboard screen
	        	finish();
			}
		});
		*/
		registeradmin = (Button) findViewById(R.id.registeradmin);
		registeradmin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent admin = new Intent(getApplicationContext(), RegisteradminActivity.class);
				admin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				admin.putExtra("pid", pid);
	        	startActivity(admin);
	        	finish();
	
			}
		});
		Intent intent = getIntent();
        flatre = intent.getStringExtra("flatre");
        pin = intent.getStringExtra("pin");
        pid = intent.getStringExtra("pid");
        String namere = intent.getStringExtra("name");
        
        adminre = (TextView) findViewById(R.id.admin);
        adminre.setVisibility(View.GONE);
		flat = (EditText) findViewById(R.id.flat);	
		flat.setText(flatre, TextView.BufferType.NORMAL);
		adminre.setText(pin);
		searchflat = (Button) findViewById(R.id.searchflat);
        searchflat.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				String flati = flat.getText().toString();
				Intent lo = new Intent(getApplicationContext(), SearchableActivity.class);
	        	//login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				lo.putExtra("flat", flati);
	        	startActivity(lo);
	        	// Closing dashboard screen
	        	//finish();
			}
		});
        
		
		txtName = (EditText) findViewById(R.id.txtName);
		
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		
		btnRegister = (Button) findViewById(R.id.btnRegister);		
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		txtPin = (EditText) findViewById(R.id.pin);
		TextView tv2 = (TextView) findViewById(R.id.tv2);
		TextView tv3 = (TextView) findViewById(R.id.tv3);
		TextView tv4 = (TextView) findViewById(R.id.tv4);
		TextView tv5 = (TextView) findViewById(R.id.tv5);
		
		txtName.setVisibility(View.GONE);
		txtEmail.setVisibility(View.GONE);
		inputPassword.setVisibility(View.GONE);
		txtPin.setVisibility(View.GONE);
		btnRegister.setVisibility(View.GONE);
		tv2.setVisibility(View.GONE);
		tv3.setVisibility(View.GONE);
		tv4.setVisibility(View.GONE);
		tv5.setVisibility(View.GONE);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		
		if (getIntent().getExtras() != null){
			txtName.setVisibility(View.VISIBLE);
			txtEmail.setVisibility(View.VISIBLE);
			inputPassword.setVisibility(View.VISIBLE);
			txtPin.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
			tv3.setVisibility(View.VISIBLE);
			tv4.setVisibility(View.VISIBLE);
			tv5.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.VISIBLE);
			flat.setKeyListener(null);
		}
		
		
		/*
		 * Click event on Register button
		 * */
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// Read EditText dat
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();
				String flatre = flat.getText().toString();
				String admin = adminre.getText().toString();
				
				// Check if user filled the form
				if(name.trim().length() > 0 && email.trim().length() > 0){
					
					String pincheck = txtPin.getText().toString();
					if (pin.equals(pincheck)){

					new AddDaAccount().execute();
					// Launch Main Activity
					}
					else{
						Toast.makeText(getApplicationContext(), R.string.wrongpin, Toast.LENGTH_LONG).show();
						
					}
					
					
				}else{
					// user doen't filled that data
					// ask him to fill the form
					alert.showAlertDialog(RegisterActivity.this, "Registration Error!", "Please enter your details", false);
				}
			}
		});
        }else if(userFunctions.isUserLoggedIn(getApplicationContext())){
	        	// user is logged in show login screen
	        	Intent login = new Intent(getApplicationContext(), MainActivity.class);
	        	login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(login);
	        	// Closing dashboard screen
	        	finish();
    
			}else{
	        	// user is not logged in show login screen
				
	        	Intent login = new Intent(getApplicationContext(), LoginActivity.class);
	        	login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(login);
	        	// Closing dashboard screen
	        	finish();
	        	
	    }

		
	}
	class AddDaAccount extends AsyncTask<String, String, String>{
		
	
		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            pDialog = new ProgressDialog(RegisterActivity.this);
		            pDialog.setMessage("Loading. Please wait...");
		            pDialog.setIndeterminate(false);
		            pDialog.setCancelable(false);
		            pDialog.show();
		   }
			 

			@Override
			protected String doInBackground(String... args) {
				name = txtName.getText().toString();
				 email = txtEmail.getText().toString();
				flatre = flat.getText().toString();
				admin = adminre.getText().toString();
				dummy = "0";
				password = inputPassword.getText().toString().toLowerCase();

				JSONObject json = userFunctions.registerUser(name, email, flatre, admin, dummy, password);
				
				try {
					if (json.getString(KEY_SUCCESS) != null) {
				
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							
							// user successfully registred
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunctions.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_FLAT), json_user.getString(KEY_ADMIN),json_user.getString(KEY_DUMMY), pid, json_user.getString(KEY_CREATED_AT));						
							// Launch Dashboard Screen
							
						}else{
							// Error in registration
							v=2;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
				//
			}
		
	          protected void onPostExecute(String zoom){
	        	  pDialog.dismiss();
	        	  RegisterActivity.this.runOnUiThread(new Runnable() {
	                  public void run() {
	                	  if(v==2){
	                	  registerErrorMsg.setText(getResources().getString(R.string.usernameexists));
	                	  txtEmail.setText("");
	          
	                	  Builder alert = new AlertDialog.Builder(RegisterActivity.this);
	  	        		alert.setTitle("Info");
	  	        		alert.setMessage(getResources().getString(R.string.usernameexists));
	  	        		alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	  	                  public void onClick(DialogInterface dialog, int id) {
	  	                    //do things
	  	                	Intent intent = getIntent();
		                	  finish();
		                	  startActivity(intent);
	  	               }
	  	           });
	  	        		alert.show();
	                	  
	                	  
	                	  }else{
	        	// Registering user on our server					
					// Sending registraiton details to MainActivity
	                	  //Toast.makeText(getApplicationContext(), email +" " + password, Toast.LENGTH_LONG).show();
	                Intent i = new Intent(getApplicationContext(), MainActivity.class);
	                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("name", name);
					i.putExtra("email", email);
					i.putExtra("flat", flatre);
					i.putExtra("admin", admin);
					i.putExtra("dummy", dummy);
					i.putExtra("pid", pid);
					startActivity(i);
					finish();
	                	  }
	                  }
					});
	        	  
			}
	        	  
	}

}
