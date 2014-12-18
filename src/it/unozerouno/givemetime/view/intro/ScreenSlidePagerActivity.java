package it.unozerouno.givemetime.view.intro;

import it.unozerouno.givemetime.controller.fetcher.ApiController;
import it.unozerouno.givemetime.view.intro.fragments.ScreenSlidePageFragmentOne;
import it.unozerouno.givemetime.view.intro.fragments.CalendarPickerFragment;
import it.unozerouno.givemetime.view.intro.fragments.ScreenSlidePageFragmentTwo;
import it.unozerouno.givemetime.view.main.MainActivity;
import it.unozerouno.givemetime.view.utilities.LoginPreferences;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import it.unozerouno.givemetime.R;

public class ScreenSlidePagerActivity extends FragmentActivity{

    // number of pages to show
    private static final int NUM_PAGES = 3;

    // pager widget
    private ViewPager mPager;

    //pager adapter
    private PagerAdapter mPagerAdapter;

    public PagerAdapter getmPagerAdapter() {
        return mPagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if it's the first time that the user launches the app, continue. Else, launch main activity
        if (LoginPreferences.isFirst(this)){
            // set the layout
            setContentView(R.layout.activity_screen_slide);

            // instantiate the ViewPager and the adapter
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            // remove comment if you want to use the zoomOutPageTransformer animation
            // mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        } else {
            // lauch main activity
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            // close this activity in order to avoid to return to it pressing the back button in MainActivity
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(mPager.getCurrentItem() == 0){
            super.onBackPressed();
        } else {
           mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
   
    /**
     * Launch the API Controller
     */
    public void initializeApi(View v){
        	startActivity(new Intent(this, ApiController.class));
    }
    
    // adapter as an internal class
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            // choose the right fragment to display
            switch (i){
                case 0: return new ScreenSlidePageFragmentOne();
                case 1: return new CalendarPickerFragment();
                case 2: return new ScreenSlidePageFragmentTwo();
                default: return null;
            }


        }
        
        

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}