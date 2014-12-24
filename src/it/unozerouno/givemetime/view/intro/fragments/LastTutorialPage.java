package it.unozerouno.givemetime.view.intro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.view.main.MainActivity;

public class LastTutorialPage extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.last_tutorial_page, container, false);
        
        Button btn = (Button) rootView.findViewById(R.id.continueButton);
        
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserKeyRing.setFirstLogin(getActivity(), false);
				Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
				// close the calling activity in order to avoid back button to work
				getActivity().finish();
			}
		});

        return rootView;
    }
}
