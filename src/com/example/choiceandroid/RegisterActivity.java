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
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{
	Button registerButton = null;
	Button resetButton = null;
	EditText username = null;
	EditText password1 = null;
	EditText password2 = null;
	EditText email = null;
	ProgressDialog myDialog = null;

	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("username", username.getText().toString().trim());
				intent.putExtras(bundle);
				startActivity(intent);
				RegisterActivity.this.finish();
			}
			else if (msg.what == 0){
				username.setText("");
				password1.setText("");
				password2.setText("");
				email.setText("");
				Toast.makeText(RegisterActivity.this, "Username In Use", Toast.LENGTH_LONG).show();
			}
		}
	};
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_register);

		GradientDrawable grad = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[]{Color.YELLOW, Color.WHITE}
				);
		getWindow().setBackgroundDrawable(grad);

		registerButton = (Button)findViewById(R.id.button3);
		resetButton = (Button)findViewById(R.id.button4);
		resetButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);

		username = (EditText)findViewById(R.id.editText3);
		password1 = (EditText)findViewById(R.id.editText4);
		password2 = (EditText)findViewById(R.id.editText5);
		email = (EditText)findViewById(R.id.editText6);
	}

	@Override
	public void onClick(View v) {
		if (v == registerButton){
			String u_name = username.getText().toString().trim();
			String u_pwd1 = password1.getText().toString().trim();
			String u_pwd2 = password2.getText().toString().trim();
			String u_email = email.getText().toString().trim();

			if(u_name.trim().equals("")){
				Toast.makeText(this, "Username Empty", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(u_pwd1.trim().equals("")){
				Toast.makeText(this, "Password Empty", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(!u_pwd2.trim().equals(u_pwd1.trim())){
				Toast.makeText(this, "Password Not Match", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(u_email.trim().equals("")){
				Toast.makeText(this, "Email Empty", Toast.LENGTH_SHORT).show();
				return;
			}
			myDialog = ProgressDialog.show(this, "Notification", "Processing...", true);
			new Thread(){
				public void run(){
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(ChoiceAndroid.API + "register/");

					try {
						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("password", password1.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString().trim()));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						// Execute HTTP Post Request
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();  
						if (resEntity != null) {  
							String result = new String(EntityUtils.toString(resEntity, HTTP.UTF_8));
							Log.d("TAG", result);
							JSONObject jo = new JSONObject(result);
							int code = jo.getInt("code");
							String info = (String) jo.get("info");
							Log.d("TAG", String.valueOf(code));
							Log.d("TAG", info);
							Message message = new Message();
							message.what = code;
							myHandler.sendMessage(message);
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
		else if (v == resetButton){
			username.setText("");
			password1.setText("");
			password2.setText("");
			email.setText("");
		}
	}

}
