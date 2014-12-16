package unozerouno.givemetime.Activities;

import unozerouno.givemetime.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import unozerouno.givemetime.R;
import unozerouno.givemetime.Fragments.DrawerListFragment;
public class MainActivity extends Activity {

    // titles in the drawer
    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private CharSequence drawerTitle;
    private CharSequence title;

    // manage the interaction between the action bar and the drawer
    // Moreover, it implements the drawer listener, useful for managing the drawer actions
    private ActionBarDrawerToggle drawerToggle;

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

        // obtain the list of titles
        titles = getResources().getStringArray(R.array.menu_list);
        // retrieve the drawer layout
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        // retrieve the reference of the drawer in the view
        drawerList = (ListView)findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set the adapters for the list
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, titles));
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

        // if there is no savedInstanceState, start from the first Item
        if (savedInstanceState == null){
            selectItem(0);
        }

    }

    // listener of the drawer list menu
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // swaps fragment in the main content view
    private void selectItem(int position){
        // create a Fragment
        Fragment fragment = new DrawerListFragment();
        Bundle args = new Bundle();
        // save the position clicked in the string item_number
        args.putInt(DrawerListFragment.ITEM_NUMBER, position);
        // assign the value to the fragment
        fragment.setArguments(args);

        // insert the fragment into the view, replacing the existing one
        // obtain the fragment manager
        FragmentManager fragmentManager = getFragmentManager();
        // start transaction, replace the fragment, commit
        fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();

        // highlight the selected item
        drawerList.setItemChecked(position, true);
        // update the title
        setTitle(titles[position]);
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
        // set the visibility according to drawerOpen
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
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

        // handle here the other action bar items
        switch (item.getItemId()){
            case R.id.action_websearch:
                // create the intent to handle this option
                Intent websearch = new Intent(Intent.ACTION_WEB_SEARCH);
                // save the title of the action bar
                websearch.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // check if the action could be handled, otherwise show an error message
                if (websearch.resolveActivity(getPackageManager()) != null){
                    startActivity(websearch);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.addEvent:
                // Create the intent to handle this option
                Intent addEvent = new Intent(this, AddNewEventActivity.class);
                startActivity(addEvent); // maybe with result?
                return true;
            case R.id.action_settings:
                // create the intent
                Intent settings = new Intent(this, SettingsActivity.class);
                // launch it
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
