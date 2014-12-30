package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom adapter for choosing the user profile in the nav bar
 * @author Paolo
 *
 */

public class UserSpinnerAdapter extends ArrayAdapter<DrawerUserProfile>{

	private Context context;
	private int layoutResID;
	private List<DrawerUserProfile> spinnerData;
	
	public UserSpinnerAdapter(Context context, int layoutResID, int textViewResID, List<DrawerUserProfile> spinnerDataList) {
		super(context, layoutResID, textViewResID, spinnerDataList);
		
		this.context = context;
		this.layoutResID = layoutResID;
		this.spinnerData = spinnerDataList;
	}
	
	public UserSpinnerAdapter(Context context, int layoutResID, List<DrawerUserProfile> spinnerDataList) {
		super(context, layoutResID, spinnerDataList);
		
		this.context = context;
		this.layoutResID = layoutResID;
		this.spinnerData = spinnerDataList;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		SpinnerHolder holder;
		
		if(row == null){
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			
			row = inflater.inflate(layoutResID, parent, false);
			holder = new SpinnerHolder();
			
			holder.userImage = (ImageView) row.findViewById(R.id.left_pic);
			holder.name = (TextView) row.findViewById(R.id.text_main_name);
			holder.email = (TextView) row.findViewById(R.id.sub_text_email);
			
			row.setTag(holder);
		} else {
			holder = (SpinnerHolder) row.getTag();
		}
		
		DrawerUserProfile spinner = spinnerData.get(position);
		
		holder.userImage.setImageDrawable(row.getResources().getDrawable(spinner.getDrawableResID()));
		holder.name.setText(spinner.getName());
		holder.email.setText(spinner.getEmail());
		
		return row;
	}
	
	private static class SpinnerHolder{
		ImageView userImage;
		TextView name, email;
	}
}
