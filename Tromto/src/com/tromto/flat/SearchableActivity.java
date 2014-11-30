package com.tromto.flat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tromto.flat.jParser;
import com.tromto.flat.library.JSONParser;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SearchableActivity extends ListActivity {
	JSONParser parser3 = new JSONParser();
	jParser parser = new jParser();
	
	private ProgressDialog pDialog;
	private static final String urlGetFlat = "http://smileowl.com/labloc/getflatbyname.php";
	JSONArray jArray = null;
	String ame, adr;
	ArrayList<HashMap<String, String>> movies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchable);
		
		Intent intent = getIntent();
		final String flat = intent.getStringExtra("flat");
	
		
		movies = new ArrayList<HashMap<String, String>>();
		new AddDaComment().execute(flat);
		
	}

	class AddDaComment extends AsyncTask<String, String, String>{

	   @Override
       protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(SearchableActivity.this);
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
			JSONObject json = parser.makeHttpRequest(urlGetFlat, params);
			jArray = json.getJSONArray("smileowlTable");
			
			 for (int i =0; i<jArray.length();i++){
					
					JSONObject c = jArray.getJSONObject(i);	
					String pin = c.getString("pin");
					ame = c.getString("flat");
					String name = c.getString("name");
					String pid = c.getString("pid");
					//Log.e("flat", ame);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("pid", pid);
					map.put("pin", pin);
					map.put("flat", ame);
					map.put("name", name);
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
			
        	  SearchableActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                	
                	ListAdapter adapter = new SimpleAdapter(SearchableActivity.this, movies,
     		               R.layout.your_item, new String[] {"pin","flat", "name"}, 
     		               new int[]{R.id.textView1, R.id.textView2, R.id.textView3});
                	
                	
                	if(ame != null){
                		
                	}else{
                		Toast.makeText(getApplicationContext(), R.string.nobuilding, Toast.LENGTH_LONG).show();
				    	
                	}
     		           setListAdapter(adapter);
     		          ListView lv = getListView();
     			   		lv.setOnItemClickListener(new OnItemClickListener(){

     			   			@Override
     			   			public void onItemClick(AdapterView<?> parent, View view, int position,
     			   					long id) {
     			   				String flat =movies.get(position).get("flat");
     			   				String pin = movies.get(position).get("pin");
     			   			    String name = movies.get(position).get("name");
     			   			String pid = movies.get(position).get("pid");

     			   				Intent i2 = new Intent(getApplicationContext(), RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

     			   				i2.putExtra("flatre", flat);
     	         				i2.putExtra("pin", pin);  
     	         				i2.putExtra("pid", pid);  
     	         				i2.putExtra("name", name);  	
     			   				startActivityForResult(i2,100);
     			   				finish();
     			   				
     			   			}
     			   			
     			   		});
			
			
                }
			});
		}/**/
		
	}
}
