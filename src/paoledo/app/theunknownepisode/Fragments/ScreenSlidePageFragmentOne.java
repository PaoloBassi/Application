package paoledo.app.theunknownepisode.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import paoledo.app.theunknownepisode.R;

public class ScreenSlidePageFragmentOne extends Fragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_screen_page_layout_one, container, false);

        return rootView;
    }
}
