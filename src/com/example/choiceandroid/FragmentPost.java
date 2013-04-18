package com.example.choiceandroid;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FragmentPost extends Fragment implements OnClickListener {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	EditText title = null;
	Button button_post = null;
	Button button_add = null;
	ProgressDialog myDialog = null;
	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				Toast.makeText(getActivity(), "Post Success", Toast.LENGTH_LONG).show();
			} else{
				Toast.makeText(getActivity(), "Post Failed", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        View view = inflater.inflate(R.layout.tab_fragment1, container, false);
        title = (EditText)view.findViewById(R.id.editText_title);
        title.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				title.requestFocusFromTouch();
				return false;
			}
        });        
        button_post = (Button)view.findViewById(R.id.button_post);
        button_add = (Button)view.findViewById(R.id.button_add);
        button_post.setOnClickListener(this);
        button_add.setOnClickListener(this);
        return view;
    }

	@Override
	public void onClick(View v) {
		if (v == button_add){
			Toast.makeText(getActivity(), "button add", Toast.LENGTH_LONG).show();
		}
		else if (v == button_post){
			Toast.makeText(getActivity(), "button post", Toast.LENGTH_LONG).show();
			myDialog = ProgressDialog.show(getActivity(), "Notification", "Processing...", true);
			new Thread(){
				public void run(){
					int code = 0;
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(ChoiceAndroid.API + "post/");

					try {
						// Add your data
						/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("username", username.trim()));
						nameValuePairs.add(new BasicNameValuePair("question", "question:test"));
						nameValuePairs.add(new BasicNameValuePair("choice_list", new ArrayList().toString()));
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/
						
						/*ArrayList<Choice> choice_list = new ArrayList<Choice>();
						choice_list.add(new Choice("first", "http://first"));
						choice_list.add(new Choice("second", "http://second"));*/
						//JSONArray jsonArray = new JSONArray(choice_list);
						JSONArray jsonArray = new JSONArray();
						JSONObject j1 = new JSONObject();
						JSONObject j2 = new JSONObject();
						j1.put("choice_text", "first");
						j2.put("choice_text", "second");
						j1.put("image_url", "http://first.jpg");
						j2.put("image_url", "http://second.jpg");
						jsonArray.put(j1);
						jsonArray.put(j2);
						
						
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("username", MainActivity.username);
						jsonObj.put("question", "question is test");
						jsonObj.put("choice_list", jsonArray);
						Log.d("Json", jsonObj.toString());
						StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
						entity.setContentType("application/json");
						httpPost.setEntity(entity);
						
						// Execute HTTP Post Request
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity resEntity = response.getEntity();  
						if (resEntity != null) {  
							String result = new String(EntityUtils.toString(resEntity, HTTP.UTF_8));
							Log.d("TAG", result);
							JSONObject   jo   =   new  JSONObject(result);
							code = jo.getInt("code");
							String info = (String) jo.get("info");
							Log.d("TAG", String.valueOf(code));
							Log.d("TAG", info);
							
							/*Intent intent = new Intent();
							intent.setClass(ChoiceAndroid.this, MainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("username", username.getText().toString());
							bundle.putString("password",password.getText().toString());
							intent.putExtras(bundle);
							startActivity(intent);
							ChoiceAndroid.this.finish();*/
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
					Message message = new Message();
					message.what = code;
					myHandler.sendMessage(message);
				}
				}.start();

		}
	}
}
