package android.app.safariskenia;

import android.app.safariskenia.R;
import android.app.TabActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class SafariMain extends TabActivity {
    /** Called when the activity is first created. */
	private TabHost mTabHost;
	
	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
		
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupTabHost();
        
        Intent intent;
	    intent = new Intent().setClass(this, Tours.class);
	    setupTab(intent, "tours", "Tours n Safaris");
		
		intent = new Intent().setClass(this, Packages.class);
		//intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(intent, "packages", "Tour Packages");
		
		mTabHost.setCurrentTab(0);
    }
    
    private void setupTab(Intent intent, String tag, String label) {
		View tabview = createTabView(mTabHost.getContext(), label);
		
	    TabHost.TabSpec spec;
	
	    spec = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
	    mTabHost.addTab(spec);
	
	}
    
    private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}