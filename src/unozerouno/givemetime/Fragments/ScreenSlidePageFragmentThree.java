package unozerouno.givemetime.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import unozerouno.givemetime.R;

public class ScreenSlidePageFragmentThree extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_screen_page_layout_three, container, false);

        return rootView;
    }
}
