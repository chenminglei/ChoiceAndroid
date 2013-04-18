package com.example.choiceandroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity implements OnClickListener{
	Button button_vote = null;
	TextView vote_title = null;
	TextView vote_date = null;
	ProgressDialog myDialog = null;
	ArrayList<Choice> choice_items;
	private int id;
	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				Bundle bundle= msg.getData();
				String date = bundle.getString("pub_date");
				String question = bundle.getString("question");
				vote_title.setText(question);
				vote_date.setText(date);
			} else{
				Toast.makeText(InfoActivity.this, "Get Failed", Toast.LENGTH_LONG).show();
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_info);

		GradientDrawable grad = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[]{Color.YELLOW, Color.WHITE}
				);
		getWindow().setBackgroundDrawable(grad);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getInt("poll_id");
		if (bundle.getString("action").equals("list")){
			button_vote = (Button)findViewById(R.id.button_vote);
			button_vote.setOnClickListener(this);
		}
		vote_title = (TextView)findViewById(R.id.textView_vote_info);
		vote_date = (TextView)findViewById(R.id.textView_date_info);
		initialize();
	}
	private void initialize() {
		myDialog = ProgressDialog.show(this, "Notification", "Processing...", true);
		new Thread(){
			public void run(){
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(ChoiceAndroid.API + "detail/");
				ArrayList<Choice> items = new ArrayList<Choice>();
				String question = null;
				String pub_date = null;
				int code = 0;
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("p_id", String.valueOf(id)));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpClient.execute(httpPost);
					HttpEntity resEntity = response.getEntity();  
					if (resEntity != null) {  
						String result = new String(EntityUtils.toString(resEntity, HTTP.UTF_8));
						Log.d("TAG", result);
						JSONObject   jo   =   new  JSONObject(result);
						code = jo.getInt("code");
						String info = (String) jo.get("info");
						question = jo.getString("question");
						pub_date = jo.getString("pub_date");
						Log.d("TAG", String.valueOf(code));
						Log.d("TAG", info);
						JSONArray jarray = jo.getJSONArray("choices");
						int size = jarray.length();
						for (int i = 0; i < size; i++){
							JSONObject o = jarray.getJSONObject(i);
							items.add(new Choice(o.getString("choice_text"), o.getString("image_url"),
									o.getInt("choice_id"), o.getInt("votes")));
						}
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					Log.i("EXCEPTION", e.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				choice_items = items;
				Message message = new Message();
				message.what = code;
				Bundle bundle = new Bundle();
				bundle.putString("question", question);
				bundle.putString("pub_date", pub_date);
				message.setData(bundle);
				myHandler.sendMessage(message);
				
				httpClient.getConnectionManager().shutdown();
				myDialog.dismiss();
			}
			}.start();
	}
	@Override
	public void onClick(View v) {
		if (v == button_vote){
			Toast.makeText(this, "vote clicked", Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}
}
