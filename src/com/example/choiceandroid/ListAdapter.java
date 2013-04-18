package com.example.choiceandroid;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class ListAdapter extends BaseAdapter{
	public final class ViewHolder{
	    public TextView desc;
	    public TextView username;
	    public TextView datetime;
	}
	
	ArrayList<Vote> vote_items = null;
	private LayoutInflater mInflater;
	
	public ListAdapter(Context context, ArrayList<Vote> vote_items) {//¹¹ÔìÆ÷
        super();   
        this.vote_items = vote_items;      
        mInflater = LayoutInflater.from(context);
    } 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (vote_items == null)
			return 10;
		return vote_items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null){
			holder=new ViewHolder(); 
			convertView = mInflater.inflate(R.layout.vote, null);
			holder.desc = (TextView)convertView.findViewById(R.id.textView_desc);
			holder.username = (TextView)convertView.findViewById(R.id.textView_user);
			holder.datetime = (TextView)convertView.findViewById(R.id.textView_date);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.desc.setText(vote_items.get(position).desc);
		holder.username.setText(vote_items.get(position).username);
		holder.datetime.setText(vote_items.get(position).datetime);
		
		return convertView;
	}

}
