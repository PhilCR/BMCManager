package com.solucionamos.bmcmanager;

import com.example.bmcmanager.R;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.solucionamos.bmcmanager.model.Server;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ServerListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ServerDetailFragment}.
 */
public class ServerDetailActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && 
        		(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))
        	getActionBar().setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ServerDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ServerDetailFragment.ARG_ITEM_ID));
            ServerDetailFragment fragment = new ServerDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ServerListActivity.class));
            return true;
        }
        if(id == R.id.add_server){
        	Intent k = new Intent(ServerDetailActivity.this, AddServerActivity.class);
    		startActivity(k);
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_server_list, menu);
		return true;
    }
    
    public void deleteServer(Server el){
    	DBHelper mydb = new DBHelper(this);
    	mydb.deleteServer(el.getName());
    	NavUtils.navigateUpTo(this, new Intent(this, ServerListActivity.class));
    }
}