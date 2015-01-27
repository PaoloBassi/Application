package it.unozerouno.givemetime.view.intro.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;
import it.unozerouno.givemetime.R;

/**
 * This fragment displays the welcome page for the user that starts for the first time the application. 
 * @author Paolo Bassi
 *
 */


public class WelcomeAndDisclaimer extends Fragment{

	private ViewPager pager;
	
    public WelcomeAndDisclaimer(ViewPager pager) {
		this.pager = pager;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.welcome_and_disclaimer, container, false);

        Button btnNext = (Button) rootView.findViewById(R.id.btn_next_first_page);
        
        btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				System.out.println("Inside Welcome " + pager.getCurrentItem());
				pager.setCurrentItem(pager.getCurrentItem() + 1);
				
			}
		});

        return rootView;
    }
}
