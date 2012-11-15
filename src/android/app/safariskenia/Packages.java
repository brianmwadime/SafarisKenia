package android.app.safariskenia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.safariskenia.R;
import android.app.safariskenia.RestClient.RequestMethod;

import android.app.Activity;
//import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.TextView;
import android.widget.Toast;



public class Packages extends Activity {
	private View packageView;
	private View packages;
	TextView tpackage;
	public String[] packageno;
	public String[] getPackageId;
	public String[] getPackageno;
	private Integer selectedPsn;
	boolean isViewDetail = false;
	private LayoutInflater factory;

	private RestClient restClient;
	private static final String LOG_TAB = "DebugApp";
	ListView list;
	ListView packagelist;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        factory = getLayoutInflater();
        //setContentView(R.layout.packages);
        packages = factory.inflate(R.layout.packages, null);
        //set main view
        setContentView(packages);
        packageView = factory.inflate(R.layout.package_det_view, null);
        packagelist = (ListView)packageView.findViewById(R.id.package_det);
        list=(ListView)findViewById(R.id.package_list);
        //Set Package View List to be clicked
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
            	//Log.v(LOG_TAB, "ListView Item Clicked ");
              // When clicked, show a toast with the TextView text
             //Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                // Toast.LENGTH_SHORT).show();
            	//Use list item position to check if package no exists then use it to load package details
            	selectedPsn = position;
				boolean isnot = true;
				int i;
				
				if(getPackageId != null && packageno != null){
					Log.v(LOG_TAB, "getPackageId");
					Log.v(LOG_TAB, "packageno");
					for(i=0; i< packageno.length; i++){
						if(packageno[i].equals(getPackageId[selectedPsn])){
							isnot = false;
							break;
						}
					}
				}
				if(isnot){
            	//bring up new view displaying package details
            	if(getWindow().getDecorView().findViewById(android.R.id.content) !=packageView ){
            		isViewDetail=true;
					setContentView(packageView);
					CallWebServiceTaskPackage task = new CallWebServiceTaskPackage();
			        task.applicationContext = Packages.this;
			        task.execute();
            	}else{
					Toast.makeText(getApplicationContext(), "Package doesn't exist", Toast.LENGTH_SHORT).show();
				}
				}
            }
          });
      //TODO:: whether to store data in database or let up sync on startup
        CallWebServiceTask task = new CallWebServiceTask();
        task.applicationContext = this;
        task.execute();
        list.setTextFilterEnabled(true);
        
    }
    
    /**Setup options menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 0, 0, "Refresh");
    	menu.add(0, 1, 1, "Back");
    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
		if(isViewDetail == false){
    		menu.findItem(1).setEnabled(false);
    	}
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
	    	case 0:
		        CallWebServiceTask taskA = new CallWebServiceTask();
		        taskA.applicationContext = this;
		        taskA.execute();
	    		break;
	    	case 1:
	    		//return to the main packages view
	    		if(getWindow().getDecorView().findViewById(android.R.id.content) != packages ){
            		isViewDetail=true;
					setContentView(packages);
				}
	    		break;
	    	default:
    		
    	}
    	return true;
    }
    
    private void emptyList(ListView thisList, int list_item, String toDisplay){
    	String[] emptyList;
    	emptyList = new String[1];
    	emptyList[0] = toDisplay;
    	thisList.setAdapter(new ArrayAdapter<String>(this, list_item, emptyList));
	}
    
    public class CallWebServiceTask extends AsyncTask<String, String, String> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "", "loading...", true);
		}

		protected String doInBackground(String... args) {
			restClient = new RestClient(getString(R.string.remote_server_link));
    	    restClient.AddParam("option", "com_tourmanager");
    	    restClient.AddParam("view", "itineraries");
    	    restClient.AddParam("format", "raw");
    	        	        		
    	    try {
    	    	restClient.Execute(RequestMethod.GET);
    	
    	        String response = restClient.getResponse();
    	        Log.v(LOG_TAB, response);
    	        if(restClient.getResponseCode() == 200){
    	        	return response;
    	        }
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        Log.v(LOG_TAB, "HTTP Request Error - "+e);
    	    }
    	    
    	    return "";
		}

		protected void onPostExecute(String result) {
			this.dialog.cancel(); 
			try {
    			JSONObject jObject = new JSONObject(result);
    			try {
    				JSONArray aItems = jObject.getJSONObject("items").getJSONArray("item");
    				
    				if(aItems != null && aItems.length() > 0){
    			        String[] packagesI = new String[aItems.length()];
    			        packageno = new String[aItems.length()];
    		        
    			        for (int i = 0; i < aItems.length(); i++){
    						try {
    							packagesI[i] = aItems.getJSONObject(i).getString("name").toString();
    							packageno[i] = aItems.getJSONObject(i).getString("id").toString();
    						} catch (JSONException e) {
    							e.printStackTrace();
    							Log.v(LOG_TAB, "Loop Exception - "+e);
    						}
    					}
    			        if(packagesI[0] != null){
    			        	list.setAdapter(new ArrayAdapter<String>(Packages.this, R.layout.package_item, packagesI));
    			        }else{
        	        		emptyList(list, R.layout.empty_item, getString(R.string.no_packages));
        	        	}
    	        	}else{
    	        		emptyList(list, R.layout.empty_item, getString(R.string.no_packages));
    	        	}
    			} catch (JSONException e) {
    				e.printStackTrace();
        			Log.v(LOG_TAB, "JSON Exception - "+e);
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
    			Log.v(LOG_TAB, "JSON Exception - "+e);
    		}
		}
	}

    public class CallWebServiceTaskPackage extends AsyncTask<String, String, String> {
		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "", "loading Package...", true);
		}

		protected String doInBackground(String... args) {
			restClient = new RestClient(getString(R.string.remote_server_link));
    	    restClient.AddParam("option", "com_tourmanager");
    	    restClient.AddParam("view", "itineraries");
    	    restClient.AddParam("format", "raw");
    	    //TODO: Add functions to pull and save data to the main database(site)
    	    restClient.AddParam("action", "getPackage");
    	    restClient.AddParam("itinid", packageno[selectedPsn]);
    		
    	    try {
    	    	restClient.Execute(RequestMethod.GET);
    	
    	        String response = restClient.getResponse();
    	        Log.v(LOG_TAB, response);
    	        if(restClient.getResponseCode() == 200){
    	        	return response;
    	        }
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        Log.v(LOG_TAB, "HTTP Request Error - "+e);
    	    }
    	    
    	    return "";
		}

		protected void onPostExecute(String result) {
			this.dialog.cancel(); 
			try {
    			JSONObject jObject = new JSONObject(result);
    			try {
    				JSONArray aItems = jObject.getJSONObject("items").getJSONArray("item");
    				
    				if(aItems != null && aItems.length() > 0){
    			        String[] packagesI = new String[aItems.length()];
    			         getPackageno = new String[aItems.length()];
    			        for (int i = 0; i < aItems.length(); i++){
    						try {
    							packagesI[i] = aItems.getJSONObject(i).getString("description").toString();
    							getPackageno[i] = aItems.getJSONObject(i).getString("id").toString();
    							
    						} catch (JSONException e) {
    							e.printStackTrace();
    							Log.v(LOG_TAB, "Loop Exception - "+e);
    						}
    					}
    			        if(packagesI[0] != null){
    			        	packagelist.setAdapter(new ArrayAdapter<String>(Packages.this, R.layout.package_item, packagesI));
    			        }else{
        	        		emptyList(packagelist, R.layout.empty_item, getString(R.string.no_packages));
        	        	}
    	        	}else{
    	        		emptyList(packagelist, R.layout.empty_item, getString(R.string.no_packages));
    	        	}
    			} catch (JSONException e) {
    				e.printStackTrace();
        			Log.v(LOG_TAB, "JSON Exception - "+e);
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
    			Log.v(LOG_TAB, "JSON Exception - "+e);
    		}
		}
	}
}

