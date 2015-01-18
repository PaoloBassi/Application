package it.unozerouno.givemetime.view.editor;

import it.unozerouno.givemetime.model.places.PlaceModel;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

abstract class LocationClickListener implements OnItemClickListener{
	 @Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			// reset color of all elements
			for (int i = 0; i < adapter.getChildCount(); i++) {
				adapter.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			}
			// change the background color of the selected element
			view.setBackgroundColor(Color.LTGRAY);
			// save the element selected
			PlaceModel placeSelected = (PlaceModel) adapter.getItemAtPosition(position);
			//TODO: Cannot select here
			//placeView.setSelection(position);
			//Do something with selected place
			doSomething(placeSelected);
		}
	 public abstract void doSomething(PlaceModel placeSelected);
}
