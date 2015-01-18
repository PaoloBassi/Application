package it.unozerouno.givemetime.view.main;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.StartUpFlow;
import it.unozerouno.givemetime.view.editor.AddNewEventActivity;
import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import it.unozerouno.givemetime.view.main.fragments.DebugFragment;
import it.unozerouno.givemetime.view.main.fragments.EventListFragment;
import it.unozerouno.givemetime.view.main.fragments.FragmentThree;
import it.unozerouno.givemetime.view.utilities.DrawerItem;
import it.unozerouno.givemetime.view.utilities.DrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
public class MainActivity extends Activity {

    // titles in the drawer
    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private CharSequence drawerTitle;
    private CharSequence title;
    private DrawerListAdapter adapter;
    
    private List<DrawerItem> dataList; 
    
    private static final String STARTUP_COMPLETE = "STARTUP_COMPLETE";
    // manage the interaction between the action bar and the drawer
    // Moreover, it implements the drawer listener, useful for managing the drawer actions
    @SuppressWarnings("deprecation")
	private ActionBarDrawerToggle drawerToggle;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the main content
        setContentView(R.layout.main_drawer);

       
        
        
        // set the default values of the settings as Shared Preferences
        // the last argument is for overriding values with the one written in preferences.xml
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        
        
        // acquire the current title of the activity
        title = drawerTitle = getTitle();

        // initialize the list in the nav drawer
        dataList = new ArrayList<DrawerItem>();
        // obtain the list of titles
        titles = getResources().getStringArray(R.array.menu_list);
        // retrieve the drawer layout
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        // retrieve the reference of the drawer in the view
        drawerList = (ListView)findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        // add elements to the list navigation drawer
        dataList.add(new DrawerItem(true));	// add a spinner
        
        dataList.add(new DrawerItem("Menu Options")); // add a header
        dataList.add(new DrawerItem("First Page", R.drawable.action_search));
        dataList.add(new DrawerItem("Second Page", R.drawable.action_search));
        dataList.add(new DrawerItem("Third Page", R.drawable.action_search));
        
        dataList.add(new DrawerItem("Settings")); // add a header
        dataList.add(new DrawerItem("Settings", R.drawable.ic_action_settings));
        dataList.add(new DrawerItem("About", R.drawable.ic_action_about));
        
        // set the adapters for the list
        adapter = new DrawerListAdapter(this, R.layout.drawer_list_item, dataList);
        drawerList.setAdapter(adapter);
        
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        // set the action bar icons
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // describe and acquire the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this,                   // the host activity
                drawerLayout,           // the drawerLayout
                R.drawable.ic_drawer,   // drawer icon to replace the 'Up'
                R.string.drawer_open,   // "open drawer" for accessibility
                R.string.drawer_close   // "close drawer" for accessibility
        ){

            // called when the drawer is closed
            @Override
            public void onDrawerClosed(View drawerView) {
                // when the drawer is closed, set the activity title in the action bar
                getActionBar().setTitle(title);
                // create the call to onPrepareOptionMenu()
                invalidateOptionsMenu();
            }

            // called when the drawer is open
            @Override
            public void onDrawerOpened(View drawerView) {
                // when the drawer is opened, set the activity drawer title in the action bar
                getActionBar().setTitle(drawerTitle);
                // create the call to onPrepareOptionMenu()
                invalidateOptionsMenu();
            }
        };

        // set the prepared drawer toggle as the drawer listener
        drawerLayout.setDrawerListener(drawerToggle);

        // if there is no savedInstanceState, start from the first useful Item
        if (savedInstanceState == null){
            
        	// both spinner and header are present
        	if(dataList.get(0).isSpinner() && dataList.get(1).getTitle() != null){
        		selectItem(3);
        	// spinner is missing
        	} else if (dataList.get(0).getTitle() != null) {
        		selectItem(1);
        	// both are missing
        	} else {
        		selectItem(0);
        	}
        }
      //StartUpFlow start
        if (savedInstanceState == null) {
        	  getFragmentManager().beginTransaction().add(new StartUpFlow(),"StartUpFlow").commit();
		} 
     
    }
    
  
    // listener of the drawer list menu
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (dataList.get(position).getTitle() == null){
            	selectItem(position);
            }
        }
    }

    // swaps fragment in the main content view
    private void selectItem(int position){
        // create a Fragment
        Fragment fragment = null;
        Bundle args = new Bundle();
        
        // cases must be evaluated depending on how the items are inserted into the drawerList
        switch(position){
        	case 2:	fragment = new EventListFragment();
        			args.putString(EventListFragment.ITEM_NAME, dataList.get(position).getItemName());
        			break;
        	case 3: fragment = new DebugFragment();
        			args.putString(EventListFragment.ITEM_NAME, dataList.get(position).getItemName());
        			break;
        	case 4: fragment = new FragmentThree();
        			args.putString(EventListFragment.ITEM_NAME, dataList.get(position).getItemName());
        			break;
        	case 6: Intent settings = new Intent(this, SettingsActivity.class);
    				startActivity(settings);
    				break;
        	case 7: Toast.makeText(getBaseContext(), "Lauch about popup", Toast.LENGTH_SHORT).show();
        			break;
        }
        
        if (fragment != null){
	        // assign the value to the fragment
	        fragment.setArguments(args);
	
	        // insert the fragment into the view, replacing the existing one
	        // obtain the fragment manager
	        FragmentManager fragmentManager = getFragmentManager();
	        // start transaction, replace the fragment, commit
	        fragmentManager.beginTransaction()
	                        .replace(R.id.content_frame, fragment)
	                        .commit();
	        
        }
        // highlight the selected item
        drawerList.setItemChecked(position, true);
        // update the title
        setTitle(dataList.get(position).getItemName());
        // close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    // rewrite the setTitle method to update the activity with the title of the fragment
    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(this.title);
    }

    // whenever we call the invalidateOptionMenu(), used to disable the action related to the content view
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if the navigation drawer is open, hide the action item related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        // set the visibility according to drawerOpen, if there are any
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    // restore the toggle state after onRestoreInstanceState has occurred
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // if the configuration changed, set it
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    // the menu to inflate when the action bar is pressed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    
   


    // called every time an item in the action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass the event to the ActionBarDrawerToggle, if it returns true,
        // than it has handled the app icon touch event
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        int itemId = item.getItemId();
		if (itemId == R.id.addEvent) {
			// Create the intent to handle this option
			Intent addEvent = new Intent(this, EventEditorActivity.class);
			addEvent.putExtra("EditOrNew", "New");
			startActivity(addEvent); // maybe with result?
			// TODO: refresh the view
			return true;
		} else if (itemId == R.id.action_settings) {
			// create the intent
			Intent settings = new Intent(this, SettingsActivity.class);
			// launch it
			startActivity(settings);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}

    }
}
