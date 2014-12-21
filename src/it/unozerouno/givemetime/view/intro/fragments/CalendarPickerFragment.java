package it.unozerouno.givemetime.view.intro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.ApiController;

public class CalendarPickerFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_calendar_picker, container, false);
        //Hiding ProgressBar
        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        return rootView;
    }
    
   
}
