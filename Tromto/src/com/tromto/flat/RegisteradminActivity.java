package com.tromto.flat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.tromto.flat.RegisterActivity.AddDaAccount;
import com.tromto.flat.library.DatabaseHandler;
import com.tromto.flat.library.JSONParser;
import com.tromto.flat.library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisteradminActivity extends Activity {
	UserFunctions userFunctions;
	Button btnLogout;
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;
	EditText txtName, flat, pinre;
	EditText txtEmail;
	private ProgressDialog pDialog;
	// Register button
	Button btnRegister, login, searchflat;
	TextView registerErrorMsg;
	//the other register side
	EditText inputPassword;
	JSONParser parser3 = new JSONParser();
	private static final String smileowlurl = "http://smileowl.com/labloc/insertflat.php";
	TextView adminre;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registeradmin);
		//userFunctions = new UserFunctions();
		flat = (EditText) findViewById(R.id.flat);	
		pinre = (EditText) findViewById(R.id.pin);	
		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
        //Intent i = getIntent();
		pid = null;
		
		
		btnRegister = (Button) findViewById(R.id.btnRegister);		
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		/*
		 * Click event on Register button
		 * */
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// Read EditText dat
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();
				registerErrorMsg = (TextView) findViewById(R.id.register_error);
				
				// Check if user filled the form
				if(name.trim().length() > 0 && email.trim().length() > 0){
					
					new AddDaAccount().execute();
					
					// Launch Main Activity
					
					
					
				}else{
					// user doen't filled that data
					// ask him to fill the form
					alert.showAlertDialog(RegisteradminActivity.this, "Registration Error!", "Please enter your details", false);
				}
			}
		});
	}
	class AddDaAccount extends AsyncTask<String, String, String>{
		
		
		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            pDialog = new ProgressDialog(RegisteradminActivity.this);
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
				admin = pinre.getText().toString();
				dummy = "1";
				password = inputPassword.getText().toString().toLowerCase();
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(name, email, flatre, admin, dummy, password);
				
				try {
					if (json.getString(KEY_SUCCESS) != null) {
				
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully registred
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
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
	        	  RegisteradminActivity.this.runOnUiThread(new Runnable() {
	                  public void run() {
	                	  
	                	  if(v==2){
		                	  registerErrorMsg.setText(getResources().getString(R.string.usernameexists));
		                	  Builder alert = new AlertDialog.Builder(RegisteradminActivity.this);
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
		                		  
		                		  new AddDaFlat().execute();
	        
	                
		                	  }
	                  }
					});
	        	 
			}
	        	  
	}
	class AddDaFlat extends AsyncTask<String, String, String>{
		

	   @Override
       protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(RegisteradminActivity.this);
	            pDialog.setMessage("Loading. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	   }
		 

		@Override
		protected String doInBackground(String... args) {
			email = txtEmail.getText().toString();
			pin = pinre.getText().toString();
			flatre = flat.getText().toString();
			List<NameValuePair> params = new ArrayList<NameValuePair> ();
		
			params.add(new BasicNameValuePair("flat", flatre));
			params.add(new BasicNameValuePair("name",email));
			params.add(new BasicNameValuePair("pin", pin));
			
			@SuppressWarnings("unused")
			JSONObject json = parser3.getJSONFromUrl(smileowlurl, params);
			
			return null;			
		}
	
          protected void onPostExecute(String zoom){
        	  pDialog.dismiss();
			
        	  RegisteradminActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                
                	Toast.makeText(getApplicationContext(), R.string.flatadded, Toast.LENGTH_LONG).show();
                	Intent i = new Intent(getApplicationContext(), MainActivity.class);
                	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("name", name);
					i.putExtra("email", email);
					i.putExtra("flat", flatre);
					i.putExtra("admin", admin);
					i.putExtra("dummy", dummy);
					startActivity(i);
					finish();
			
                }
			});
		}/**/
}
}
