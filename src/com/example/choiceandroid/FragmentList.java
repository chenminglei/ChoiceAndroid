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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentList extends Fragment implements OnItemClickListener {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	private ListView vote_list = null;
	ProgressDialog myDialog = null;
	public ArrayList<Vote> vote_items = null;
	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				vote_items.add(new Vote(123, "desc1", "user1", "date1"));
				vote_items.add(new Vote(123, "desc2", "user2", "date2"));
				vote_items.add(new Vote(123, "desc3", "user3", "date3"));
				vote_items.add(new Vote(123, "desc4", "user4", "date4"));
				ListAdapter la = new ListAdapter(getActivity(), vote_items);
			    vote_list.setAdapter(la);	
			    la.notifyDataSetChanged();
			} else{
				Toast.makeText(getActivity(), "Get Failed", Toast.LENGTH_LONG).show();
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
        View view = inflater.inflate(R.layout.tab_fragment2, container, false);
        vote_list = (ListView)view.findViewById(R.id.listView_list);
		vote_list.setOnItemClickListener(this);
		initializeList();
        return view;
    }
	private void initializeList() {
		myDialog = ProgressDialog.show(getActivity(), "Notification", "Processing...", true);
		new Thread(){
			public void run(){
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(ChoiceAndroid.API + "index/");
				ArrayList<Vote> items = new ArrayList<Vote>();
				int code = 0;
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("username", MainActivity.username));
					nameValuePairs.add(new BasicNameValuePair("type", "public"));
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
						Log.d("TAG", String.valueOf(code));
						Log.d("TAG", info);
						JSONArray jarray = jo.getJSONArray("list");
						int size = jarray.length();
						for (int i = 0; i < size; i++){
							JSONObject o = jarray.getJSONObject(i);
							items.add(new Vote(o.getInt("poll_id"), o.getString("question"),
									o.getString("username"), o.getString("pub_date").substring(0,19)));
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
				vote_items = items;
				Message message = new Message();
				message.what = code;
				myHandler.sendMessage(message);
				httpClient.getConnectionManager().shutdown();
				myDialog.dismiss();
			}
			}.start();
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "list item clicked" + pos, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();//创建Intent
		intent.setClass(getActivity(),  InfoActivity.class);
		Bundle bundle = new Bundle();//创建数据Bundle
		String action = "list";
		bundle.putString("action", action);
		bundle.putInt("poll_id", vote_items.get(pos).id);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
