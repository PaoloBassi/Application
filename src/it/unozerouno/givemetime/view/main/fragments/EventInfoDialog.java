package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.view.utilities.TimeConversion;
import android.app.Dialog;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventInfoDialog extends DialogFragment{
	EventInstanceModel eventToShow;
	TextView eventName;
	TextView eventStart;
	TextView eventEnd;
	TextView eventLocation;
	TextView eventCategory;
	TextView eventMovable;
	
	
	public EventInfoDialog(Context context, EventInstanceModel eventToShow) {
		this.eventToShow = eventToShow;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_detail_dialog, container, false);
		eventName = (TextView) view.findViewById(R.id.info_event_name);
		eventStart = (TextView) view.findViewById(R.id.info_event_start);
		eventEnd = (TextView) view.findViewById(R.id.info_event_end);
		eventLocation = (TextView) view.findViewById(R.id.info_event_location);
		eventCategory = (TextView) view.findViewById(R.id.info_event_category);
		eventMovable = (TextView) view.findViewById(R.id.info_event_movable);
		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		loadEvent();
	}
	private void loadEvent(){
		if(eventToShow == null || eventToShow.getEvent() == null) {
			this.dismiss();
		}
		String start = (eventToShow.getStartingTime() != null) ? TimeConversion.timeToString(eventToShow.getStartingTime(), true, false, true, false):getString(R.string.text_none);
		String end = (eventToShow.getEndingTime() != null) ? TimeConversion.timeToString(eventToShow.getEndingTime(), true, false, true, false):getString(R.string.text_none);
		String name = (eventToShow.getEvent().getName() != null) ? eventToShow.getEvent().getName():getString(R.string.text_none);
		String location = (eventToShow.getEvent().getPlace() != null) ? eventToShow.getEvent().getPlace().getName():getString(R.string.text_none);
		String category = (eventToShow.getEvent().getCategory() != null) ? eventToShow.getEvent().getCategory().getName():getString(R.string.text_none);
		String movable = (eventToShow.getEvent().getConstraints().isEmpty()) ? getString(R.string.text_no) : getString(R.string.text_yes);
		eventName.setText(name);
		eventStart.setText(start);
		eventEnd.setText(end);
		eventLocation.setText(location);
		eventCategory.setText(category);
		eventMovable.setText(movable);
	}
		
}
