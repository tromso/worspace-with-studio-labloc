package com.tromto.flat;

import static com.tromto.flat.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.tromto.flat.CommonUtilities.EXTRA_MESSAGE;
import static com.tromto.flat.CommonUtilities.SENDER_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tromto.flat.AlertDialogManager;
import com.tromto.flat.ConnectionDetector;
import com.tromto.flat.MainActivity;
import com.tromto.flat.R;
import com.tromto.flat.ServerUtilities;
import com.tromto.flat.WakeLocker;
import com.tromto.flat.FlatListActivity.GetDaApps;
import com.tromto.flat.library.DatabaseHandler;
import com.tromto.flat.library.UserFunctions;
import com.tromto.flat.LoginActivity;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ListActivity {
	
	//register from gcm
	// label to display gcm messages
		TextView lblMessage;
		
		// Asyntask
		AsyncTask<Void, Void, Void> mRegisterTask;
		
		// Alert dialog manager
		AlertDialogManager alert = new AlertDialogManager();
		
		// Connection detector
		ConnectionDetector cd;
		private ProgressDialog pDialog;
		private ProgressDialog zpDialog;
		jParser parser = new jParser();
		jParser parser0 = new jParser();
		private static final String getappurl = "http://smileowl.com/labloc/getcommentsbyflatid.php";
		private static final String getdapidurl = "http://smileowl.com/labloc/getpidfromflats.php";
		private static final String deleteurl = "http://smileowl.com/labloc/deletecomment.php";
		JSONArray jArray = null;
		JSONArray zjArray = null;
		JSONArray hjArray = null;
		ArrayList<HashMap<String, String>> movies;
		public static String name;
		public static String email, flat, admin, dummy;
		String dummyre, pid, dapid;
		Button btnLogout, gotoflats;
		UserFunctions userFunctions;
		String a,b,c,d;
		String e, f;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		
		
		// Getting name, email from intent
		Intent i = getIntent();
		
		name = i.getStringExtra("name");
		email = i.getStringExtra("email");		
		flat= i.getStringExtra("flat");
		admin= i.getStringExtra("admin");
		dummy= i.getStringExtra("dummy");
		pid= i.getStringExtra("pid");
		/*
		//logout button - login
				userFunctions = new UserFunctions();
				btnLogout = (Button) findViewById(R.id.btnLogout);
		    	
		    	btnLogout.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						userFunctions.logoutUser(getApplicationContext());
						Intent login = new Intent(getApplicationContext(), LoginActivity.class);
						login.putExtra("pid", pid);
			        	login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        	startActivity(login);
			        	// Closing dashboard screen
			        	finish();
					}
				});
				*/
		    	//invite button -
				
				Button invite = (Button) findViewById(R.id.invite);
		    	
		    	invite.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View arg0) {
						Toast.makeText(getApplicationContext(), R.string.accesscode, Toast.LENGTH_LONG).show();
						Intent intent = new Intent(Intent.ACTION_SEND);
		                intent.setType("text/plain");
		                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.getapplication)+ "... https://play.google.com/store/apps/details?id=com.tromto.flat");  
		                startActivity(Intent.createChooser(intent, "Share with: " ));
					}
				});
		
		//Toast.makeText(getApplicationContext(), "pid is: " + pid, Toast.LENGTH_LONG).show();
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		lblMessage = (TextView) findViewById(R.id.lblMessage);
		if(i.getStringExtra("message")!=null){
		lblMessage.append(""+ i.getStringExtra("message") + "\n"); 
		}
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);

		} else {
			
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
			
				//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, name, email, regId, flat, admin, dummy);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
			
		}
		//get info from sqlite database
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		//TextView textview1 = (TextView)findViewById(R.id.textView1);
		HashMap<String, String> map = new HashMap<String, String>();
		map = db.getUserDetails();
		String username = map.get("email");
		String flatre = map.get("flat");
		String appartment  = map.get("name");
		final String dummyre = map.get("dummy");
		String repid  = map.get("repid");
		
		
		//move to the flatlist activity
		if (username == null){
			//textview1.setText("Logged in as: "+ " " + email+" Appartment " + name);
			a=name;
			b=email;
			c=flat;
			d=admin;
			e=dummy;
			//f=pid;
			movies = new ArrayList<HashMap<String, String>>();
			new GetDaPid().execute();
			
        	// Closing dashboard screen
        	//finish();
		}else{
		//textview1.setText("Logged in as: " + username+ " Appartment " +  appartment);
		
		a=appartment;
		b=username;
		c=flatre;
		e=dummyre;
		}
		movies = new ArrayList<HashMap<String, String>>();
		if (repid==null){
			//Toast.makeText(getApplicationContext(), "repid is: " + repid, Toast.LENGTH_LONG).show();
			new GetDaPid().execute();
		}else{
			//Toast.makeText(getApplicationContext(), "repid is: " + repid, Toast.LENGTH_LONG).show();
			f= repid;
			new GetDaComments().execute();
		}
		
		
		//Toast.makeText(getApplicationContext(), "repid is: " + f, Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), "flat is: " + c, Toast.LENGTH_LONG).show();
		
		
		
		
		gotoflats = (Button) findViewById(R.id.gotoflats); 	
    	gotoflats.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(),  a +" " + b + " "+c, Toast.LENGTH_LONG).show();

				Intent getapps = new Intent(getApplicationContext(), FlatListActivity.class);
				getapps.putExtra("appartment", a);
				getapps.putExtra("flat", c);
				getapps.putExtra("username", b);
				getapps.putExtra("pid", f);
	        	//getapps.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(getapps);
			}
		});
		
		
	}	
	class GetDaComments extends AsyncTask<String, String, String>{

		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            zpDialog = new ProgressDialog(MainActivity.this);
		            zpDialog.setMessage("Loading. Please wait...");
		            zpDialog.setIndeterminate(false);
		            zpDialog.setCancelable(false);
		            zpDialog.show();
		   }
			 

			@Override
			protected String doInBackground(String... args) {
		
				try {
				
				List<NameValuePair> params = new ArrayList<NameValuePair> ();
				params.add(new BasicNameValuePair("flatid", f));
				
				@SuppressWarnings("unused")
				JSONObject json = parser.makeHttpRequest(getappurl, params);
				jArray = json.getJSONArray("smileowlTable");
				
				 for (int i =0; i<jArray.length();i++){
						
						JSONObject c = jArray.getJSONObject(i);	

						String comments = c.getString("comments");
						String id = c.getString("id");
	
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("comments", comments);
						map.put("id", id);
		
						movies.add(map);
	
				 }
					
			} catch(JSONException e) {
				
				e.printStackTrace();
			}
				return null;
				//
			}
		
	          protected void onPostExecute(String zoom){
	        	  zpDialog.dismiss();
				
	        	  MainActivity.this.runOnUiThread(new Runnable() {
	                public void run() {
	                	
	                	
	                	ListAdapter adapter = new SimpleAdapter(MainActivity.this, movies,
	     		               R.layout.stickymessages, new String[] {"comments"}, 
	     		               new int[]{R.id.textView1});
	                	
	     		           setListAdapter(adapter);
	     		          ListView lv = getListView();
	     		         //Toast.makeText(getApplicationContext(), "dummy is: " + e, Toast.LENGTH_LONG).show();

  			   			
	     		          if(e.equals("1")){
	     		        	
	     	        		
	     		         lv.setOnItemClickListener(new OnItemClickListener(){

	     			   			@Override
	     			   			public void onItemClick(AdapterView<?> parent, View view, int position,
	     			   					long id) {
	     			   				final String reid =movies.get(position).get("id");
	     			   			Builder alert = new AlertDialog.Builder(MainActivity.this);
	     			   			alert.setTitle("Info");
		     	        		alert.setMessage(R.string.deletemessage);
		     	        		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		     	        	        public void onClick(DialogInterface dialog, int which) {
		     	        	            //do stuff here
		     	        	        	//Toast.makeText(getApplicationContext(), "delete message #: " + reid, Toast.LENGTH_LONG).show();
		     	        	        	new DeleteComment().execute(reid);
		     	        	        }
		     	        	    });
		     	        	    //cancel button with dismiss.
		     	        	   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		     	        	        public void onClick(DialogInterface dialog, int which) {
		     	        	             //dismiss();
		     	        	        	//Toast.makeText(getApplicationContext(), "why you not delete me?: " + reid, Toast.LENGTH_LONG).show();

		     	        	        }
		     	        	    });
		     	        		
		     	        		alert.show();
	     			   			
	     			   				
	     			
	     			   				
	     			   			}
	     			   			
	     			   		});
	     		          }
	     					lv.setOnTouchListener(new ListView.OnTouchListener() {
	     				        @Override
	     				        public boolean onTouch(View v, MotionEvent event) {
	     				            int action = event.getAction();
	     				            switch (action) {
	     				            case MotionEvent.ACTION_DOWN:
	     				                // Disallow ScrollView to intercept touch events.
	     				                v.getParent().requestDisallowInterceptTouchEvent(true);
	     				                break;

	     				            case MotionEvent.ACTION_UP:
	     				                // Allow ScrollView to intercept touch events.
	     				                v.getParent().requestDisallowInterceptTouchEvent(false);
	     				                break;
	     				            }

	     				            // Handle ListView touch events.
	     				            v.onTouchEvent(event);
	     				            return true;
	     				        }
	     				    });
	     
	     			   		
				
				
	                }
				});
			}
			
		}
	class GetDaPid extends AsyncTask<String, String, String>{

		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            pDialog = new ProgressDialog(MainActivity.this);
		            pDialog.setMessage("Loading. Please wait...");
		            pDialog.setIndeterminate(false);
		            pDialog.setCancelable(false);
		            pDialog.show();
		           // Toast.makeText(getApplicationContext(), "flat is still: " + c, Toast.LENGTH_LONG).show();
		   }
			 

			@Override
			protected String doInBackground(String... args) {
		
				try {
				
				List<NameValuePair> params = new ArrayList<NameValuePair> ();
				params.add(new BasicNameValuePair("flat", c));
				
				@SuppressWarnings("unused")
				JSONObject json = parser0.makeHttpRequest(getdapidurl, params);
				zjArray = json.getJSONArray("smileowlTable");
				

						
						JSONObject c = zjArray.getJSONObject(0);	
						String ci =c.getString("pid");

						dapid = ci;
				
		
					
			} catch(JSONException e) {
				
				e.printStackTrace();
			}
				return null;
				//
			}
		
	          protected void onPostExecute(String zoom){
	        	  pDialog.dismiss();
	        	  MainActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		                	//Toast.makeText(getApplicationContext(), "dapid is " + dapid, Toast.LENGTH_LONG).show();
		                	f=dapid;
		            		//Toast.makeText(getApplicationContext(), "f is: " + f, Toast.LENGTH_LONG).show();
		            		new GetDaComments().execute();
		                }
					});
			}
			
		}
	class DeleteComment extends AsyncTask<String, String, String>{

		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            pDialog = new ProgressDialog(MainActivity.this);
		            pDialog.setMessage("Loading. Please wait...");
		            pDialog.setIndeterminate(false);
		            pDialog.setCancelable(false);
		            pDialog.show();
		           // Toast.makeText(getApplicationContext(), "flat is still: " + c, Toast.LENGTH_LONG).show();
		   }
			 

			@Override
			protected String doInBackground(String... args) {
		String reid = args[0];
				try {
				
				List<NameValuePair> params = new ArrayList<NameValuePair> ();
				params.add(new BasicNameValuePair("id", reid));
				
				@SuppressWarnings("unused")
				JSONObject json = parser0.makeHttpRequest(deleteurl, params);
				hjArray = json.getJSONArray("smileowlTable");
				

						
						JSONObject c = hjArray.getJSONObject(0);	
	
		
					
			} catch(JSONException e) {
				
				e.printStackTrace();
			}
				return null;
				//
			}
		
	          protected void onPostExecute(String zoom){
	        	  pDialog.dismiss();
	        	  MainActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		                	Toast.makeText(getApplicationContext(), R.string.messagedeleted, Toast.LENGTH_LONG).show();
		           		 
		                	Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);
							finish();
		                	
		            		
		                }
					});
			}
			
		}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		//	Log.e("broadcast", MainActivity.name);
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			// Showing received message
			lblMessage.append(""+ newMessage + "\n");			
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.newmessage)+" "+ newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			//Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
