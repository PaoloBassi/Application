package it.unozerouno.givemetime.view.intro;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment.OnSelectedPlaceModelListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class HomeLocationDialogActivity extends FragmentActivity implements OnSelectedPlaceModelListener{
	
	LocationEditorFragment lef;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_home_location);
		
		lef = (LocationEditorFragment) getSupportFragmentManager().findFragmentById(R.id.home_location_fragment_locations_container);
		getSupportFragmentManager().beginTransaction().show(lef).commit();
		
	}

	@Override
	public void onSelectedPlaceModel(PlaceModel place) {
		// TODO needs sleepTime set, precedence problem
		//DatabaseManager.addUserHomePlace(UserKeyRing.getUserEmail(this), place);
		finish();
	}

}
