package android.app.safariskenia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.safariskenia.R;
import android.app.safariskenia.RestClient.RequestMethod;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Tours extends Activity {
	
	private RestClient restClient;
	private static final String LOG_TAB = "DebugApp";
    private static LayoutInflater inflater = null;
	ListView list;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours);
        
        list=(ListView)findViewById(R.id.tr_tours_list);
        //TODO:: whether to store data in database or let up sync on startup
        CallWebServiceTask task = new CallWebServiceTask();
        task.applicationContext = this;
        task.execute();

    }
    
    /**Setup options menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 0, 0, "Refresh");
    	//menu.add(0, 1, 0, "Exit");
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
	    	//case 1:
	    		//code to stop program
	    		//break;
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
    
    /*Extend BaseAdapter to display a custom formatted List Item View*/
    public static class ViewHolder{
        public TextView tr_name;
        public TextView tr_description;
    }

    public class ToursAdapter extends BaseAdapter {
	    private Activity activity;
	    private String[] name;
	    private String[] description;
	    
	    public ToursAdapter(Activity a, String[] trName, String[] trDescription) {
	        activity = a;
	        name = trName;
	        description = trDescription;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public int getCount() {
	        return name.length;
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        View vi = convertView;
	        ViewHolder holder;
	        if(convertView == null){
	            vi = inflater.inflate(R.layout.tours_item, null);
	            holder = new ViewHolder();
	            holder.tr_name = (TextView)vi.findViewById(R.id.tr_value_name);
	            holder.tr_description = (TextView)vi.findViewById(R.id.tr_value_description);
	            vi.setTag(holder);
	        }
	        else{
	            holder = (ViewHolder)vi.getTag();
	        }
	        
	        holder.tr_name.setText(name[position]);
	        holder.tr_description.setText(description[position]);
	        	        
	        return vi;
	    }
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
    	    restClient.AddParam("view", "tours");
    	    restClient.AddParam("format", "raw");
    	    //restClient.AddParam("action", "marketStats");
    		
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
    				JSONArray trItems = jObject.getJSONObject("items").getJSONArray("item");
    				
    				if(trItems != null && trItems.length() > 0){
    			        String[] trNames = new String[trItems.length()];
    			        String[] trDescription = new String[trItems.length()];
    			            		        
    			        for (int i = 0; i < trItems.length(); i++){
    						try {
    							trNames[i] = trItems.getJSONObject(i).getString("name").toString();
    							trDescription[i] = trItems.getJSONObject(i).getString("short_desc").toString();
    						} catch (JSONException e) {
    							e.printStackTrace();
    							Log.v(LOG_TAB, "Loop Exception - "+e);
    						}
    					}
    			        if(trNames[0] != null){
	    			        ToursAdapter adapter = new ToursAdapter(Tours.this, trNames, trDescription);
	    			        list.setAdapter(adapter);
    			        }else{
        	        		emptyList(list, R.layout.empty_item, getString(R.string.no_tours));
        	        	}
    	        	}else{
    	        		emptyList(list, R.layout.empty_item, getString(R.string.no_tours));
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
