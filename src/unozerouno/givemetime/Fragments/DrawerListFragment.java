package unozerouno.givemetime.Fragments;

// Fragment class of the drawer list

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import unozerouno.givemetime.R;

public class DrawerListFragment extends Fragment{

    // reference of the item clicked
    public static final String ITEM_NUMBER = "item_number";

    public DrawerListFragment(){
        // required for fragment subclasses
    }

    // what happens when the fragment is attached to the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the content of the fragment
        // the fragment content is just an example
        View rootView = inflater.inflate(R.layout.fragment_screen_page_layout_one, container, false);
        // retrieve the item position number when it is clicked
        int i = getArguments().getInt(ITEM_NUMBER);
        // retrieve the name of the item clicked
        String item = getResources().getStringArray(R.array.menu_list)[i];
        // (example) obtain the text view of the fragment and set the name of the item clicked
        ((TextView)rootView.findViewById(R.id.view)).setText(item);
        // set the activity name equal to the item selected
        getActivity().setTitle(item);
        return rootView;
    }
}
