package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.UserKeyRing;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Specific adapter for the elements in the navigation drawer
 * @author Paolo
 *
 */

public class DrawerListAdapter extends ArrayAdapter<DrawerItem>{
	
	private Context context;
	private List<DrawerItem> drawerItemList;
	private int layoutResID;
	
	public DrawerListAdapter(Context context, int layoutResID, List<DrawerItem> listItems) {
		super(context, layoutResID, listItems);
		this.context = context;
		this.drawerItemList = listItems;
		this.layoutResID = layoutResID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		DrawerItemHolder drawerHolder;
		View view = convertView;
		
		if (view == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			drawerHolder = new DrawerItemHolder();
			
			view = inflater.inflate(layoutResID, parent, false);
			drawerHolder.itemName = (TextView) view.findViewById(R.id.drawer_ItemName);
			drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
			
			drawerHolder.spinner = (Spinner) view.findViewById(R.id.userDrawerSpinner);
			drawerHolder.title = (TextView) view.findViewById(R.id.drawerTitle);
			
			drawerHolder.headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
			drawerHolder.itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
			drawerHolder.spinnerLayout = (LinearLayout) view.findViewById(R.id.spinnerLayout);
			
			view.setTag(drawerHolder);
		} else {
			drawerHolder = (DrawerItemHolder) view.getTag();
		}
		
		DrawerItem dItem = this.drawerItemList.get(position);

		// if the item is a spinner
		if(dItem.isSpinner()){
			drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
			drawerHolder.spinnerLayout.setVisibility(LinearLayout.VISIBLE);
			
			List<DrawerUserProfile> userList = new ArrayList<DrawerUserProfile>();
			// temporary the add is for the only one user logged in
			userList.add(new DrawerUserProfile(R.drawable.user, UserKeyRing.getUserName(getContext()) + " " + UserKeyRing.getUserSurname(getContext()), UserKeyRing.getUserEmail(getContext())));
			// create the adapter for the spinner
			UserSpinnerAdapter adapter = new UserSpinnerAdapter(context, R.layout.user_profile, userList);
			// attach it to the holder
			drawerHolder.spinner.setAdapter(adapter);
			// show the change of the user
			drawerHolder.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					//Toast.makeText(context, "User Changed", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// do nothing
				}
			});
		// if the item is a header
		} else if(dItem.getTitle() != null) {
				drawerHolder.headerLayout.setVisibility(LinearLayout.VISIBLE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
                drawerHolder.spinnerLayout.setVisibility(LinearLayout.INVISIBLE);
                drawerHolder.title.setText(dItem.getTitle());
		// if the item is an item
		} else {
				drawerHolder.headerLayout.setVisibility(LinearLayout.INVISIBLE);
                drawerHolder.spinnerLayout.setVisibility(LinearLayout.INVISIBLE);
                drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);
                drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
                drawerHolder.itemName.setText(dItem.getItemName());
			}
		return view;
	}
	
	private static class DrawerItemHolder{
		TextView itemName, title;
		ImageView icon;
		LinearLayout headerLayout, itemLayout, spinnerLayout;
		Spinner spinner;
	}
}
