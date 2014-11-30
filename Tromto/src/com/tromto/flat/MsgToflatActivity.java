package com.tromto.flat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.tromto.flat.FlatListActivity.AddDaComment;
import com.tromto.flat.library.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MsgToflatActivity extends Activity {
	String flat, ap, username, yourusername, yourap;
	TextView textview1;
	AlertDialogManager alert = new AlertDialogManager();
	Button button1;
	private ProgressDialog pDialog;
	JSONParser parser3 = new JSONParser();
	private static final String smileowlurl = "http://smileowl.com/labloc/sendmsgtoapp.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msg_toflat);
		
		Intent i = getIntent();	
		yourusername = i.getStringExtra("yourusername");
		yourap = i.getStringExtra("yourap");	
		//this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		username = i.getStringExtra("username");
		flat = i.getStringExtra("flatre");		
		ap= i.getStringExtra("apre");
		
		textview1 = (TextView)findViewById(R.id.textView1);
		textview1.setText(getResources().getString(R.string.writemessageto) +" "+ username + " "+getResources().getString(R.string.ap) +" "+ ap);
		button1 = (Button)findViewById(R.id.sendmsg);
		Button button1 =(Button)findViewById(R.id.sendmsg);
		
		button1.setOnClickListener(new OnClickListener() {
			private boolean handledClick = false;
			@Override
			public void onClick(View v) {
				if (!handledClick){
			        handledClick = true;
			        EditText edittext1 = (EditText)findViewById(R.id.editText1);
				    String ms = edittext1.getText().toString();
				    if(ms.trim().length() > 0){
				new AddDaComment().execute(flat, ap);	
				    }
				    else{
				    	Toast.makeText(getApplicationContext(), R.string.nomessage, Toast.LENGTH_LONG).show();
				    	
				    	finish();
				    }
				}
				
			}
		});
	
	}
	class AddDaComment extends AsyncTask<String, String, String>{
		
		EditText edittext1 = (EditText)findViewById(R.id.editText1);
	    String ms = edittext1.getText().toString();
	    String msg = ms +"\n"+ getResources().getString(R.string.sentby)+ " " +yourusername + " " + getResources().getString(R.string.ap) + " "+yourap;

	   @Override
       protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(MsgToflatActivity.this);
	            pDialog.setMessage("Loading. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	   }
		 

		@Override
		protected String doInBackground(String... args) {
			
						
			String flat = args[0];
			String app = args[1];
			List<NameValuePair> params = new ArrayList<NameValuePair> ();
		
			params.add(new BasicNameValuePair("flat", flat));
			params.add(new BasicNameValuePair("app", app));
			params.add(new BasicNameValuePair("message", msg));
			
			@SuppressWarnings("unused")
			JSONObject json = parser3.getJSONFromUrl(smileowlurl, params);
			
			return null;			
		}
	
          protected void onPostExecute(String zoom){
        	  pDialog.dismiss();
			
        	  MsgToflatActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                	Toast.makeText(getApplicationContext(), R.string.messagesent, Toast.LENGTH_LONG).show();
					finish();
					
			
			
                }
			});
		}/**/
}
}
