package it.unozerouno.givemetime.view.intro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unozerouno.givemetime.R;

public class ScreenSlidePageFragmentTwo extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_screen_page_layout_two, container, false);

        return rootView;
    }
}