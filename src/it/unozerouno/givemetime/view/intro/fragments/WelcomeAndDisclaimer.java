package it.unozerouno.givemetime.view.intro.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.welcome_and_disclaimer, container, false);
        
        // prepare a blinking animation for the swipe suggestion
        TextView swipeText = (TextView) rootView.findViewById(R.id.centerPoint);
        
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        swipeText.startAnimation(anim);

        return rootView;
    }
}
