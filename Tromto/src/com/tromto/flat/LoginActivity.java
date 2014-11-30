/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.tromto.flat;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tromto.flat.library.DatabaseHandler;
import com.tromto.flat.library.UserFunctions;
import com.tromto.flat.LoginActivity;
import com.tromto.flat.MainActivity;
import com.tromto.flat.R;
import com.tromto.flat.RegisterActivity;
import com.tromto.flat.LoginActivity.AddDaComment;

public class LoginActivity extends Activity {
	private ProgressDialog pDialog;
	Button btnLogin;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;
	int v;
String pid;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		 //Intent i = getIntent();
			pid = null;

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			private boolean handledClick = false;
			@Override
			public void onClick(View v) {
				if (!handledClick){
			        handledClick = true;
				new AddDaComment().execute();	
				}
				
			}
		});

	}
class AddDaComment extends AsyncTask<String, String, String>{
	String email = inputEmail.getText().toString();
	String password = inputPassword.getText().toString().toLowerCase();

		
	
	      
	   @Override
       protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(LoginActivity.this);
	            pDialog.setMessage("Loading. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	   }
		 

		@Override
		protected String doInBackground(String... args) {
			
			UserFunctions userFunction = new UserFunctions();
			Log.d("Button", "Login");
			JSONObject json = userFunction.loginUser(email, password);
			// check for login response
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					//loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1){
						// user successfully logged in
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");
						
						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_FLAT), json_user.getString(KEY_ADMIN),json_user.getString(KEY_DUMMY),pid, json_user.getString(KEY_CREATED_AT));						
						
						// Launch Dashboard Screen
						Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
						
						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);
						
						// Close Login Screen
						finish();
					}else{
						
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
			
        	  LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
			
                	if(v==2){
                	// Error in login
					loginErrorMsg.setText("Wrong username or password.");
                	  Builder alert = new AlertDialog.Builder(LoginActivity.this);
	  	        		alert.setTitle("Info");
	  	        		alert.setMessage("Wrong username or password.");
	  	        		alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	  	                  public void onClick(DialogInterface dialog, int id) {
	  	                    //do things
	  	                	Intent intent = getIntent();
		                	  finish();
		                	  startActivity(intent);
	  	               }
	  	           });
	  	        		alert.show();
                	}
                }
			});
		}/**/
		
	}
}