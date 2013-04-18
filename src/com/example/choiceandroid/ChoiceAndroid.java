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
import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChoiceAndroid extends Activity implements OnClickListener {
	EditText username = null;
	EditText password = null; 
    Button loginButton = null;
	Button registerButton = null;
	ProgressDialog myDialog = null;
	public static String API = "http://10.0.2.2:8000/choice/m/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_choice);

		GradientDrawable grad = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[]{Color.YELLOW, Color.WHITE}
				);
		getWindow().setBackgroundDrawable(grad);

		loginButton = (Button)findViewById(R.id.button1);
		registerButton = (Button)findViewById(R.id.button2);
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		username = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v == loginButton){
			String editUid = username.getText().toString().trim();
			String editPwd = password.getText().toString().trim();
			if(editUid.trim().equals("")){
				Toast.makeText(this, "Username Empty",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if(editPwd.trim().equals("")){
				Toast.makeText(this, "Password Empty",
						Toast.LENGTH_SHORT).show();
				return;
			}
			myDialog = ProgressDialog.show(this, "Notification", "Processing...", true);

			new Thread(){
				public void run(){
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(API + "login/");

					try {
						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						// Execute HTTP Post Request
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();  
						if (resEntity != null) {  
							String result = new String(EntityUtils.toString(resEntity, HTTP.UTF_8));
							Log.d("TAG", result);
							JSONObject   jo   =   new  JSONObject(result);
							int code = jo.getInt("code");
							String info = (String) jo.get("info");
							Log.d("TAG", String.valueOf(code));
							Log.d("TAG", info);
							
							Intent intent = new Intent();
							intent.setClass(ChoiceAndroid.this, MainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("username", username.getText().toString());
							bundle.putString("password",password.getText().toString());
							intent.putExtras(bundle);
							startActivity(intent);
							ChoiceAndroid.this.finish();
						}

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
						Log.i("EXCEPTION", e.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}

					httpClient.getConnectionManager().shutdown();
					myDialog.dismiss();
				}
				}.start();

			}
			else if(v == registerButton){
				Intent intent = new Intent();
				intent.setClass(this, RegisterActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("action", "register");
				intent.putExtras(bundle);
				startActivity(intent);
				this.finish();
			}
		}
	}
