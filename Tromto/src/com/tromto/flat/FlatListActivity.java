package com.tromto.flat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tromto.flat.SearchableActivity.AddDaComment;
import com.tromto.flat.library.JSONParser;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FlatListActivity extends ListActivity {
	private ProgressDialog pDialog;
	private static final String smileowlurl = "http://smileowl.com/labloc/send_message.php";
	private static final String getappurl = "http://smileowl.com/labloc/getappartmentsbyflat.php";
	JSONParser parser3 = new JSONParser();
	jParser parser = new jParser();
	AlertDialogManager alert = new AlertDialogManager();
	JSONArray jArray = null;
	ArrayList<HashMap<String, String>> movies;
	String appartment, flat, dummy, username, pid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flat_list);
        
		Intent i = getIntent();	
		appartment = i.getStringExtra("appartment");
		flat = i.getStringExtra("flat");		
		username= i.getStringExtra("username");
		//pid = i.getStringExtra("pid");		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//Toast.makeText(getApplicationContext(), "flat is: " + flat, Toast.LENGTH_LONG).show();
		
		movies = new ArrayList<HashMap<String, String>>();
		new GetDaApps().execute(flat);

		
		Button sendmsg =(Button)findViewById(R.id.sendmsg);
		sendmsg.setOnClickListener(new OnClickListener() {
			private boolean handledClick = false;
			@Override
			public void onClick(View v) {
				if (!handledClick){
			        handledClick = true;
			        EditText edittext1 = (EditText)findViewById(R.id.editText1);
				    String ms = edittext1.getText().toString();
			        if(ms.trim().length() > 0){
				new AddDaComment().execute(flat, pid);	
			        }else{
			        	Toast.makeText(getApplicationContext(), R.string.nomessage, Toast.LENGTH_LONG).show();
				    	
				    	finish();
				    }
				}
				
			}
		});
	}
	class GetDaApps extends AsyncTask<String, String, String>{

		   @Override
	       protected void onPreExecute() {
		            super.onPreExecute();
		            pDialog = new ProgressDialog(FlatListActivity.this);
		            pDialog.setMessage("Loading. Please wait...");
		            pDialog.setIndeterminate(false);
		            pDialog.setCancelable(false);
		            pDialog.show();
		   }
			 

			@Override
			protected String doInBackground(String... args) {
				
				String flat = args[0];
				try {
				
				List<NameValuePair> params = new ArrayList<NameValuePair> ();
				params.add(new BasicNameValuePair("flat", flat));
				
				@SuppressWarnings("unused")
				JSONObject json = parser.makeHttpRequest(getappurl, params);
				jArray = json.getJSONArray("smileowlTable");
				
				 for (int i =0; i<jArray.length();i++){
						
						JSONObject c = jArray.getJSONObject(i);	
						String username = c.getString("username");
						String lat = c.getString("flat");
						String ap = c.getString("appartment");
						String pi = c.getString("pid");
						pid=pi;
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("bloc", lat);
						map.put("ap", ap);
						map.put("pid", pi);
						map.put("username", username);
						movies.add(map);
	
				 }
					
			} catch(JSONException e) {
				
				e.printStackTrace();
			}
				return null;
				//
			}
		
	          protected void onPostExecute(String zoom){
	        	  pDialog.dismiss();
				
	        	  FlatListActivity.this.runOnUiThread(new Runnable() {
	                public void run() {
	                	
	                	
	                	ListAdapter adapter = new SimpleAdapter(FlatListActivity.this, movies,
	     		               R.layout.your_flat, new String[] {"username", "ap"}, 
	     		               new int[]{R.id.textView1, R.id.textView2});
	                	
	     		           setListAdapter(adapter);
	     		          ListView lv = getListView();
	     			   		lv.setOnItemClickListener(new OnItemClickListener(){

	     			   			@Override
	     			   			public void onItemClick(AdapterView<?> parent, View view, int position,
	     			   					long id) {
	     			   				String fla =movies.get(position).get("bloc");
	     			   				String apt = movies.get(position).get("ap");
	     			   			    String usernam = movies.get(position).get("username");
	     			   				

	     			   				Intent i2 = new Intent(getApplicationContext(), MsgToflatActivity.class);

	     			   				i2.putExtra("flatre", fla);
	     	         				i2.putExtra("apre", apt);  	
	     	         				i2.putExtra("username", usernam);  	

	     	         				i2.putExtra("yourap", appartment);  	
	     	         				i2.putExtra("yourusername", username);  	
	     			   				startActivityForResult(i2,100);
	     			
	     			   				
	     			   			}
	     			   			
	     			   		});
				
				
	                }
				});
			}
			
		}
	
class AddDaComment extends AsyncTask<String, String, String>{
	
		EditText edittext1 = (EditText)findViewById(R.id.editText1);
	    String ms = edittext1.getText().toString();
	    
	    String msg = ms + "\n"+ getResources().getString(R.string.sentby) + " " + username +" " + getResources().getString(R.string.ap) + " " +appartment;

	   @Override
       protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(FlatListActivity.this);
	            pDialog.setMessage("Loading. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	   }
		 

		@Override
		protected String doInBackground(String... args) {
			
						
			String flat = args[0];
			String pid = args[1];
			List<NameValuePair> params = new ArrayList<NameValuePair> ();
		
			params.add(new BasicNameValuePair("flat", flat));
			params.add(new BasicNameValuePair("message", msg));
			params.add(new BasicNameValuePair("flatid", pid));
			
			@SuppressWarnings("unused")
			JSONObject json = parser3.getJSONFromUrl(smileowlurl, params);
			
			return null;			
		}
	
          protected void onPostExecute(String zoom){
        	  pDialog.dismiss();
			
        	  FlatListActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                
                	Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
					// Close all views before launching Dashboard
					dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(dashboard);
					finish();
					Toast.makeText(getApplicationContext(), R.string.messagesent, Toast.LENGTH_LONG).show();
			
			
                }
			});
		}/**/
}
}
