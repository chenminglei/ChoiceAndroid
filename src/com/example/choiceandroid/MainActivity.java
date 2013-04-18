package com.example.choiceandroid;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener, OnClickListener {

	private TabHost mTabHost;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private TabInfo mLastTab = null;
    public static String username = null;
	//EditText title = null;
	//Button button_post = null;
    
    
    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }
    }
    
    class TabFactory implements TabContentFactory { 
        private final Context mContext;
        public TabFactory(Context context) {
            mContext = context;
        }
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }   
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		GradientDrawable grad = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[]{Color.YELLOW, Color.WHITE}
				);
		getWindow().setBackgroundDrawable(grad);
		
		Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
		username = bundle.getString("username");
		
		initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        
       // title = (EditText)findViewById(R.id.editText_title);
       // button_post = (Button)findViewById(R.id.button_post);
       // button_post.setOnClickListener(this);
        
	}

	protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
	
	
	private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Post Vote"), (tabInfo = new TabInfo("Tab1", FragmentPost.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Vote List"), (tabInfo = new TabInfo("Tab2", FragmentList.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("My Votes"), (tabInfo = new TabInfo("Tab3", FragmentResult.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        this.onTabChanged("Tab1");
        mTabHost.setOnTabChangedListener(this);
    }

	private static void addTab(MainActivity activity, TabHost tabHost, TabSpec tabSpec, TabInfo tabInfo) {
		tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();
        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
        tabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
        mTabHost.clearFocus();
	}

	@Override
	public void onClick(View v) {
		//if (v == button_post){
		//	Toast.makeText(this, "button pressed", Toast.LENGTH_LONG).show();
		//}
	}
}
