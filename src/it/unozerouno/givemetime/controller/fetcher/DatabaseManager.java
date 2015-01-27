package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher.Actions;
import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher.PlaceResult;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.constraints.DateConstraint;
import it.unozerouno.givemetime.model.constraints.DayConstraint;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import it.unozerouno.givemetime.model.events.EventCategory;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.model.questions.OptimizingQuestion;
import it.unozerouno.givemetime.model.questions.QuestionModel;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.utils.Results;
import it.unozerouno.givemetime.utils.TaskListener;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.SparseArray;

/**
 * This is the entry Point for the model. It fetches all stored app data from DB
 * and generates Model. It also keeps the internal GiveMeTime db and the
 * CalendarProvider synchronized by fetching updates from Google calendar and
 * updating the internal db.
 * 
 * @author Edoardo Giacomello
 * @author Paolo Bassi
 * 
 */
public final class DatabaseManager {

	private static SQLiteDatabase database = null;
	private static DatabaseCreator dbCreator;
	private static DatabaseManager dbManagerInstance;

	private DatabaseManager(Context context) {
		if (database == null || dbCreator == null) {
			dbCreator = DatabaseCreator.createHelper(context);
			database = dbCreator.getWritableDatabase();
			GiveMeLogger.log("Database Path: "+ database.getPath() );
		}
	}

	public static synchronized DatabaseManager getInstance(Context context) {
		if (dbManagerInstance == null) {
			dbManagerInstance = new DatabaseManager(context);
		}
		return dbManagerInstance;
	}

	/**
	 * close all instances of DB and DBHelper
	 */

	static void closeDB() {
		if (database != null) {
			database.close();
		}
		if (dbCreator != null) {
			dbCreator.close();
		}
	}

	/**
	 * return a list of EventInstanceModel to be used by the calendar view
	 * inside two time constraints
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static void getEventsInstances(Time start, Time end, Context caller,
			final EventListener<EventInstanceModel> eventListener) {
			getEventsInstances(-1, start, end, caller, eventListener);
	}
	/**
	 * return a list of EventInstanceModel in the specified range and with the specified Id.
	 * If eventId == -1, all instances are given 
	 * @param start
	 * @param end
	 * @return
	 */
	public static void getEventsInstances(int eventId, Time start, Time end, Context caller,
			final EventListener<EventInstanceModel> eventListener) {

		// fetch the event from the calendar provider
		final CalendarFetcher calendarFetcher = new CalendarFetcher(caller);
		calendarFetcher.setAction(CalendarFetcher.Actions.LIST_OF_EVENTS);
		calendarFetcher.setEventInstanceTimeQuery(start.toMillis(false),
				end.toMillis(false));
		if(eventId!=-1)	calendarFetcher.setEventId(eventId);
		calendarFetcher.setListener(new TaskListener<String[]>(caller) {
			SparseArray<EventDescriptionModel> eventDescriptionMap = new SparseArray<EventDescriptionModel>();

			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == Results.RESULT_TYPE_EVENT) {
					EventDescriptionModel newEvent = eventDescriptionToModel(results[1]);
					eventDescriptionMap.put(Integer.parseInt(newEvent.getID()),
							newEvent);
				} else if (results[0] == Results.RESULT_TYPE_INSTANCE) {
					EventInstanceModel newInstance = eventInstanceToModel(
							results[1], eventDescriptionMap);
					newInstance.addListener(eventListener);
					newInstance.setCreated();
				}
				else if (results[0] == Results.RESULT_OK) {
					eventListener.loadingComplete();
				}else {
					// Unexpected result
					System.out
							.println("Got unexpected result from calendarFetcher");
				}

			}

		});
		calendarFetcher.execute();
	}
	
	/**
	 * Converts a CalendarFetcher string into an EventDescriptionModel
	 * Also loads GiveMeTime additional data, if present in database
	 * @param eventResult
	 * @return
	 */
	private static EventDescriptionModel eventDescriptionToModel(String[] eventResult) {
		// Returned Values: 0:Events._ID, 1:Events.TITLE, 2:Events.DTSTART,
		// 3:Events.DTEND, 4:Events.EVENT_COLOR, 5:Events.RRULE, 6:Events.RDATE,
		// 7: Events.ALL_DAY
		// put each event inside a EventDescriptionModel
		// prepare the model
		String id = eventResult[0];
		String title = eventResult[1];
		String start = eventResult[2];
		String end = eventResult[3];
		String color = eventResult[4];
		String RRULE = eventResult[5];
		String RDATE = eventResult[6];
		String ALL_DAY = eventResult[7];

		EventDescriptionModel eventDescriptionModel = new EventDescriptionModel(
				id, title);
		Long startLong = null;
		Long endLong = null;

		if (start != null) {
			startLong = Long.parseLong(start);
			eventDescriptionModel.setSeriesStartingDateTime(CalendarUtils
					.longToTime(startLong));
		}
		if (end != null) {
			endLong = Long.parseLong(end);
			eventDescriptionModel.setSeriesEndingDateTime(CalendarUtils
					.longToTime(endLong));
		}

		if (color != null) {
			eventDescriptionModel.setColor(Integer.parseInt(color));
		}
		// check for recursive events
		if (RRULE != null) {
			eventDescriptionModel.setRRULE(RRULE);
		}
		if (RDATE != null) {
			eventDescriptionModel.setRDATE(RDATE);
		}
		if (ALL_DAY != null) {
			eventDescriptionModel.setAllDay(Integer.parseInt(ALL_DAY));
		}
		
		//Loads GiveMeTime event Information
		return loadEventFromDatabase(eventDescriptionModel);
	}

	/**
	 * Converts a CalendarFetcher string into an EventInstanceModel
	 * 
	 * @param instanceResult
	 *            the CalendarFetcher result
	 * @param eventLookupTable
	 *            an HashMap containing known events from which select the one
	 *            to associate to the instance
	 * @return
	 */
	private static EventInstanceModel eventInstanceToModel(String[] instanceResult,
			SparseArray<EventDescriptionModel> eventLookupTable) {
		int eventId = Integer.parseInt(instanceResult[0]);
		Time startTime = new Time();
		Time endTime = new Time();
		Long startLong = Long.parseLong(instanceResult[1]);
		Long endLong = Long.parseLong(instanceResult[2]);
		startTime.set(startLong);
		endTime.set(endLong);
		EventInstanceModel eventInstance = new EventInstanceModel(
				eventLookupTable.get(eventId), startTime, endTime);
		if (eventInstance.getEvent() == null) {
			System.err
					.println("Found an orphan instance without no event, looking for event id "
							+ eventId);
		}

		return eventInstance;

	}

	/**
	 * Pulls all new events from Google Calendar and creates relative entries in
	 * GiveMeTime database
	 */
	public static synchronized boolean synchronize(final Context caller) {

		// Fetching Events ID from CalendarProvider
		final CalendarFetcher calendarFetcher = new CalendarFetcher(caller);
		calendarFetcher.setAction(CalendarFetcher.Actions.LIST_EVENTS_ID_RRULE);
		calendarFetcher.setListener(new TaskListener<String[]>(caller) {
			@Override
			public void onTaskResult(String[]... results) {
				for (String[] event : results) {
					String eventId = event[0];
					String eventRRULE = event[1];
					String eventRDATE = event[2];
					DatabaseManager.getInstance(caller).createNewEventRow(caller,eventId);
					
					GiveMeLogger.log("Created in DB event with id: "+ eventId + " RRULE: " + eventRRULE + " RDATE: "
							+ eventRDATE);
				}
			}

		});
		calendarFetcher.execute();

		return true;
		// TODO: synchronization: update of modified events while app was not
		// running
	}
	/**
	 * Updates event in both database and calendar provider
	 * @param caller
	 * @param eventToUpdate
	 */
	public static void updateEvent(Context caller,
			EventInstanceModel eventToUpdate) {
		// Updating CalendarFetcher
		CalendarFetcher updater = new CalendarFetcher(caller);
		updater.setEventToUpdate(eventToUpdate);
		updater.setAction(Actions.UPDATE_EVENT);
		updater.setListener(new TaskListener<String[]>(caller) {
			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == Results.RESULT_OK) {
					GiveMeLogger.log("Event Update complete");
				} else {
					GiveMeLogger.log("Error during event update");
				}

			}
		});

		addEventInDatabase(eventToUpdate.getEvent().getID(), eventToUpdate);
	}

	/**
	 * Adds a new event into the db
	 */
	public static void addEvent(final Context caller,
			final EventInstanceModel newEvent) {

		CalendarFetcher updater = new CalendarFetcher(caller);
		updater.setEventToUpdate(newEvent);
		updater.setAction(Actions.ADD_NEW_EVENT);
		updater.setListener(new TaskListener<String[]>(caller) {

			@Override
			public void onTaskResult(String[]... results) {
				String addedEventId = results[0][0];
				GiveMeLogger.log("Event added with id " + addedEventId);
				addEventInDatabase(addedEventId, newEvent);
				
			}

		});
		
		updater.execute();
	}

	/**
	 * Adds a new Row in GiveMeTime database corresponding to a new event. Note
	 * that the event must be already present on the Calendar Provider AND its
	 * relative ID must be supplied.
	 * Note that if a row with the same EventID already exists, IT WILL BE REPLACED
	 * 
	 * @param addedEventId
	 *            the Id of the event in Calendar Provider
	 * @param newEvent
	 *            the event Model to add to GiveMeTime database
	 */
	private static void addEventInDatabase(String addedEventId,
			EventInstanceModel newEventInstance) {

		EventDescriptionModel newEvent = newEventInstance.getEvent();
		String eventId = addedEventId;
		int calendarId = Integer.parseInt(newEvent.getCalendarId());
		String eventCategory = newEvent.getCategory().getName();
		String placeId = null;
		if(newEvent.getPlace() != null){
			placeId = newEvent.getPlace().getPlaceId();
		}
		
		boolean doNotDisturb = newEvent.getDoNotDisturb();
		boolean hasDeadline = newEvent.getHasDeadline();
		boolean isMovable = newEvent.getIsMovable();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseCreator.ID_CALENDAR, calendarId);
		values.put(DatabaseCreator.ID_EVENT_PROVIDER, eventId);
		values.put(DatabaseCreator.ID_EVENT_CATEGORY, eventCategory);
		values.put(DatabaseCreator.ID_PLACE, placeId);
		values.put(DatabaseCreator.FLAG_DO_NOT_DISTURB, doNotDisturb);
		values.put(DatabaseCreator.FLAG_DEADLINE, hasDeadline);
		values.put(DatabaseCreator.FLAG_MOVABLE, isMovable);
		
		Long result = database.insertWithOnConflict(DatabaseCreator.TABLE_EVENT_MODEL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		GiveMeLogger.log("Updated GiveMeTime DB row: " + result);
		
	}

	/**
	 * Fetches all active events at this moment and returns it through the provided listener
	 * @param listener listener in which onDatabaseUpdated(List<EventInstanceModel>) is called
	 */
	public static void getActiveEvents(final OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>> listener, Context context){
		Time start = new Time();
		Time end = new Time();
		final Time now = new Time();
		now.setToNow();
		start.setToNow();
		end.setToNow();
		end.set(59, 59, 23, end.monthDay, end.month, end.year);
		final ArrayList<EventInstanceModel> eventList = new ArrayList<EventInstanceModel>();
		EventListener<EventInstanceModel> fetcherListener = new EventListener<EventInstanceModel>() {

			@Override
			public void onEventChange(EventInstanceModel newEvent) {
				
			}

			@Override
			public void onEventCreation(EventInstanceModel newEvent) {
				//If the event has start and end
				if (newEvent.getStartingTime() != null && newEvent.getEndingTime() != null){
					//And if now it is between start and end
					if(newEvent.getStartingTime().toMillis(false)<= now.toMillis(false) && newEvent.getEndingTime().toMillis(false)>= now.toMillis(false)){
						//Then add to List
						eventList.add(newEvent);
					}
				}
			}

			@Override
			public void onLoadCompleted() {
				listener.updateFinished(eventList);
			}
		};
		getEventsInstances(start, end, context, fetcherListener);
		
	}
	
	/**
	 * This function fills the provided EventDescriptionModel with information present into GiveMeTime database.
	 * @param eventToLoad
	 * @return
	 */
	public static EventDescriptionModel loadEventFromDatabase (EventDescriptionModel eventToLoad){
		int eventIdToLoad = Integer.parseInt(eventToLoad.getID());
		List<ComplexConstraint> constraints;
		String calendarId ="";
		String placeId = "";
		boolean doNotDisturb=false;
		boolean hasDeadline=false;
		boolean isMovable=false;
		String categoryString = "";
		
		String table= DatabaseCreator.TABLE_EVENT_MODEL;
		String[] projection = DatabaseCreator.Projections.EVENT_MODEL_ALL;
		String where = DatabaseCreator.ID_EVENT_PROVIDER +" = " + eventIdToLoad;
		Cursor eventCursor = database.query(table, projection, where, null, null, null, null);
		while (eventCursor.moveToNext()){
			//Fetching strings from db
			String calendarIdDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ID_CALENDAR));
			String dndDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.FLAG_DO_NOT_DISTURB));
			String hasDeadLineDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.FLAG_DEADLINE));
			String isMovableDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.FLAG_MOVABLE));
			String categoryDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ID_EVENT_CATEGORY));
			String placeIdDB = eventCursor.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ID_PLACE));
			//Converting into actual types
			calendarId = calendarIdDB;
			if(dndDB != null) doNotDisturb = dndDB.equals("1");
			if(hasDeadLineDB != null) hasDeadline = hasDeadLineDB.equals("1");
			if(isMovableDB != null) isMovable = isMovableDB.equals("1");
			categoryString = categoryDB;
			placeId = placeIdDB;
		}
		eventCursor.close();
		
		//Creating model dependencies
		PlaceModel place = getPlaceById(placeId);
		constraints = getConstraints(eventToLoad);
		EventCategory category = getCategoryByName(categoryString);
		
		
		//Filling the model
		eventToLoad.setCalendarId(calendarId);
		eventToLoad.setDoNotDisturb(doNotDisturb);
		eventToLoad.setHasDeadline(hasDeadline);
		eventToLoad.setIsMovable(isMovable);
		eventToLoad.setCategory(category);
		eventToLoad.setConstraints(constraints);
		eventToLoad.setPlace(place);
		
		
		
		return eventToLoad;
	}

	/**
	 * Called at application startup. It inserts a new row into GMT database, with the eventId as primary key
	 * it also fetches, if present, the location stored on GoogleCalendar event and populates the corresponding table
	 * 
	 * @param context
	 * @param eventId
	 *            the id fetcher in the provider
	 */

	private void createNewEventRow(Context context, String eventId) {
		String calId = UserKeyRing.getCalendarId(context);
		final String table = DatabaseCreator.TABLE_EVENT_MODEL;
		final ContentValues values = new ContentValues();
		values.put(DatabaseCreator.ID_CALENDAR, Integer.parseInt(calId));
		values.put(DatabaseCreator.ID_EVENT_PROVIDER, Integer.parseInt(eventId));
		database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
	
	}

	
	
	// ///////////////////
	//
	// Location management
	//
	// ///////////////////
	
	

	/**
	 * Fetch more informations about Place result and store data into the
	 * database. When the operation is complete, it notify to a provided
	 * listener
	 * 
	 * @param placeResult
	 *            input Result
	 * @param OnDatabaseUpdatedListener
	 *            Listener to notify
	 */
	public static void addPlaceAndFetchInfo(PlaceResult placeResult,
			final OnDatabaseUpdatedListener<PlaceModel> listener) {
		PlaceModel newPlace = new PlaceModel(placeResult);

		AsyncTask<PlaceModel, Void, PlaceModel> placeFetcher = new AsyncTask<PlaceModel, Void, PlaceModel>() {

			@Override
			protected PlaceModel doInBackground(PlaceModel... place) {
				place[0] = PlaceFetcher.getAdditionalInfo(place[0]);
				return place[0];
			}

			@Override
			protected void onPostExecute(PlaceModel result) {
				super.onPostExecute(result);
				addPlaceInDatabase(result);
				listener.updateFinished(result);
			}
		};
		placeFetcher.execute(newPlace);
	}

	/**
	 * Store a placeModel into the database
	 * 
	 * @param newPlace
	 */
	private synchronized static void addPlaceInDatabase(PlaceModel newPlace) {
		// Now the place has all known infos

		// Getting whole place data
		String placeId = newPlace.getPlaceId();
		String name = newPlace.getName();
		String address = newPlace.getAddress();
		String formattedAddress = newPlace.getFormattedAddress();
		String country = newPlace.getCountry();
		String phoneNumber = newPlace.getPhoneNumber();
		String icon = newPlace.getIcon();
		Double latitude = newPlace.getLocation().getLatitude();
		Double longitude = newPlace.getLocation().getLongitude();
		int visitCounter = newPlace.getVisitCounter();

		// Inserting Values in PlaceModel table
		ContentValues values = new ContentValues();
		values.put(DatabaseCreator.PLACE_ID, placeId);
		values.put(DatabaseCreator.PLACE_NAME, name);
		values.put(DatabaseCreator.PLACE_ADDRESS, address);
		values.put(DatabaseCreator.PLACE_FORMATTED_ADDRESS, formattedAddress);
		values.put(DatabaseCreator.PLACE_COUNTRY, country);
		values.put(DatabaseCreator.PLACE_PHONE_NUMBER, phoneNumber);
		values.put(DatabaseCreator.PLACE_ICON, icon);
		values.put(DatabaseCreator.PLACE_LOCATION_LATITUDE,
				Double.toString(latitude));
		values.put(DatabaseCreator.PLACE_LOCATION_LONGITUDE,
				Double.toString(longitude));
		values.put(DatabaseCreator.PLACE_VISIT_COUNTER,
				Integer.toString(visitCounter));
		Time now = new Time();
		now.setToNow();
		values.put(DatabaseCreator.PLACE_DATE_CREATED, now.toMillis(false));

		// Executing Query
		Long query = database.insertWithOnConflict(
				DatabaseCreator.TABLE_PLACE_MODEL, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);
		GiveMeLogger.log("Inserted Location, added row: " + query);

		addOpeningTime(newPlace);
	}

	/**
	 * Return a place stored into the GiveMeTime db with id equals to placeId, or null if the place is not found.
	 * @param placeId
	 * @return
	 */
	public static PlaceModel getPlaceById(String placeId){
		PlaceModel placeResult = null;
		String[] projection = DatabaseCreator.Projections.PLACES_ALL;
		String where = DatabaseCreator.PLACE_ID + " = "+ "'" + placeId+ "'";
		Cursor cursor = database.query(DatabaseCreator.TABLE_PLACE_MODEL,
				projection, where, null, null, null,
				DatabaseCreator.PLACE_DATE_CREATED + ", "
						+ DatabaseCreator.PLACE_VISIT_COUNTER + " DESC");
		while (cursor.moveToNext()) {
			String name = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_NAME));
			String address = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_ADDRESS));
			String formattedAddress = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_FORMATTED_ADDRESS));
			String country = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_COUNTRY));
			String phoneNumber = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_PHONE_NUMBER));
			String icon = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_ICON));

			String latitudeString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_LOCATION_LATITUDE));
			Double latitude = Double.parseDouble(latitudeString);
			String longitudeString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_LOCATION_LONGITUDE));
			Double longitude = Double.parseDouble(longitudeString);
			String visitCounterString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_VISIT_COUNTER));
			int visitCounter = Integer.parseInt(visitCounterString);

			// Creating PlaceModel
			PlaceModel newPlace = new PlaceModel(placeId, name, address,
					country);
			newPlace.setFormattedAddress(formattedAddress);
			newPlace.setPhoneNumber(phoneNumber);
			newPlace.setIcon(icon);
			Location newLocation = new Location("GiveMeTime");
			newLocation.setLatitude(latitude);
			newLocation.setLongitude(longitude);
			newPlace.setLocation(newLocation);
			newPlace.setVisitCounter(visitCounter);

			List<ComplexConstraint> openingTimes = getOpeningTime(newPlace);
			newPlace.setOpeningTime(openingTimes);
			placeResult=newPlace;
			
		}
		cursor.close();
		return placeResult;
	}
	
	/**
	 * Gets all places stored into the DB
	 * @return
	 */
	public static List<PlaceModel> getPlaces() {
		List<PlaceModel> places = new ArrayList<PlaceModel>();
		String[] projection = DatabaseCreator.Projections.PLACES_ALL;
		Cursor cursor = database.query(DatabaseCreator.TABLE_PLACE_MODEL,
				projection, null, null, null, null,
				DatabaseCreator.PLACE_DATE_CREATED + ", "
						+ DatabaseCreator.PLACE_VISIT_COUNTER + " DESC");
		while (cursor.moveToNext()) {
			String placeId = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_ID));
			String name = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_NAME));
			String address = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_ADDRESS));
			String formattedAddress = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_FORMATTED_ADDRESS));
			String country = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_COUNTRY));
			String phoneNumber = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_PHONE_NUMBER));
			String icon = cursor.getString(DatabaseCreator.Projections
					.getIndex(projection, DatabaseCreator.PLACE_ICON));

			String latitudeString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_LOCATION_LATITUDE));
			Double latitude = Double.parseDouble(latitudeString);
			String longitudeString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_LOCATION_LONGITUDE));
			Double longitude = Double.parseDouble(longitudeString);
			String visitCounterString = cursor
					.getString(DatabaseCreator.Projections.getIndex(projection,
							DatabaseCreator.PLACE_VISIT_COUNTER));
			int visitCounter = Integer.parseInt(visitCounterString);

			// Creating PlaceModel
			PlaceModel newPlace = new PlaceModel(placeId, name, address,
					country);
			newPlace.setFormattedAddress(formattedAddress);
			newPlace.setPhoneNumber(phoneNumber);
			newPlace.setIcon(icon);
			Location newLocation = new Location("GiveMeTime");
			newLocation.setLatitude(latitude);
			newLocation.setLongitude(longitude);
			newPlace.setLocation(newLocation);
			newPlace.setVisitCounter(visitCounter);

			List<ComplexConstraint> openingTimes = getOpeningTime(newPlace);
			newPlace.setOpeningTime(openingTimes);
			places.add(newPlace);
		}
		cursor.close();
		return places;
	}
	
	

	// ///////////////////////
	//
	//
	// Constraints functions
	//
	//
	// ///////////////////////

			//////////////////////////
			// Constraints Insert/Update
			/////////////////////////
	private static int addComplexConstraintInDatabase(ComplexConstraint complexConstraint){
		SparseArray<Constraint> addedConstraintIndexes = new SparseArray<Constraint>();
		for (Constraint currentConstraint : complexConstraint.getConstraints()) {
		   int currentId = addSimpleConstratint(currentConstraint);
		   addedConstraintIndexes.put(currentId,currentConstraint);
		}
		//Now simple constraints are generated and we have a map for id-constraint
		//Inserting rows into ComplexConstraint table
		
			int complexConstraintID = complexConstraint.getId();
		
			//we have to update an existing ComplexConstraint 
			//Removing all existing rows of the complexConstraint (so even removed simpleConstraints are deleted)
			database.delete(DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS, DatabaseCreator.C_COMPLEX_ID + " = " + complexConstraint.getId(), null);
			
			
			//Adding all the new rows
			for (int i = 0; i < addedConstraintIndexes.size(); i++) {
			int currentSimpleConstraintId = addedConstraintIndexes.keyAt(i);
			ContentValues values = new ContentValues();
			values.put(DatabaseCreator.C_COMPLEX_S_ID, currentSimpleConstraintId);
			
			if (complexConstraintID != -1){
				//If the complexConstraintID is -1 then we are adding a new complexConstraint in the db
				//so we don't specify the constraint for the first insertion
				values.put(DatabaseCreator.C_COMPLEX_ID, complexConstraint.getId());
			}
			//Here we add a new row into ComplexConstaint table
				Long rowIndex = database.insert(DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS, null, values);
				
			if (complexConstraintID==-1){
				//If the constraintID is still -1 we can get back the actual complexConstraintID value from the database
				String where = "rowid = " + rowIndex;
				String[] projection = {DatabaseCreator.C_COMPLEX_ID};
				Cursor result = database.query(DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS, projection , where,null, null, null, null);
				while (result.moveToNext()) {
					//Here we're updating the complexCID with the value generated by the database
					complexConstraintID = result.getInt(0);
				}
				result.close();
			}
				GiveMeLogger.log("Added complex constraint with id " + complexConstraint.getId() + ", parent of a single event with id" + currentSimpleConstraintId );
		}
		return complexConstraintID;
			
			
		
		
	}
	/**
	 * Add a simpleConstraint in the relative database table
	 * @param constraintToAdd
	 * @return the id of the added/updated constraint
	 */
	private static int addSimpleConstratint(Constraint constraintToAdd){
		int id = -1;
		String type;
		Long start;
		Long end;
		int dayStart;
		int dayEnd;
		
		if (constraintToAdd instanceof DateConstraint){
			DateConstraint constraint = (DateConstraint) constraintToAdd;
			type = "DateConstraint"; 
			//Checking if given constraint has id or not (update or insert)
			if(constraint.getId() == -1){
				id = getNewSimpleConstraintId();
			} else {
				id = constraint.getId();
			}
			start = constraint.getStartingDate().toMillis(false);
			end = constraint.getEndingDate().toMillis(false);
			//Adding constraint to Database
			addSimpleConstraintRow(id, type, start.toString(), end.toString());
			return id;
		}
		if (constraintToAdd instanceof TimeConstraint){
			TimeConstraint constraint = (TimeConstraint) constraintToAdd;
			type = "TimeConstraint"; 
			//Checking if given constraint has id or not (update or insert)
			if(constraint.getId() == -1){
				id = getNewSimpleConstraintId();
			} else {
				id = constraint.getId();
			}
			start = constraint.getStartingTime().toMillis(false);
			end = constraint.getEndingTime().toMillis(false);
			//Adding constraint to Database
			addSimpleConstraintRow(id, type, start.toString(), end.toString());
			return id;
		}
		if (constraintToAdd instanceof DayConstraint){
			DayConstraint constraint = (DayConstraint) constraintToAdd;
			type = "DayConstraint"; 
			//Checking if given constraint has id or not (update or insert)
			if(constraint.getId() == -1){
				id = getNewSimpleConstraintId();
			} else {
				id = constraint.getId();
			}
			dayStart = constraint.getStartingDay();
			dayEnd = constraint.getEndingDay();
			//Adding constraint to Database
			addSimpleConstraintRow(id, type, Integer.toString(dayStart), Integer.toString(dayEnd));
			return id;
		}
		GiveMeLogger.log("This function cannot parse this constraint");
		return -1;
	}
	/**
	 * This function adds/update a single row in SimpleConstraint table of GiveMeTime DB
	 * @param id
	 * @param constraintType
	 * @param start
	 * @param end
	 */
	private static synchronized void addSimpleConstraintRow(int id, String constraintType, String start, String end){
		ContentValues values = new ContentValues();
		values.put(DatabaseCreator.C_SIMPLE_ID_CONSTRAINT, id);
		values.put(DatabaseCreator.C_SIMPLE_CONSTRAINT_TYPE, constraintType);
		values.put(DatabaseCreator.C_START, start);
		values.put(DatabaseCreator.C_END, end);
		Long query = database.insertWithOnConflict(DatabaseCreator.TABLE_SIMPLE_CONSTRAINTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		GiveMeLogger.log("Added/Edited row " + query + "of SimpleConstraint table");
	}
	
	/**
	 * This function inserts a new empty row in SimpleConstraints table and returns the ID for the update
	 * @return
	 */
	private static synchronized int getNewSimpleConstraintId(){
		Long rowIndex = database.insert(DatabaseCreator.TABLE_SIMPLE_CONSTRAINTS, null, new ContentValues());
		String where = "rowid = " + rowIndex;
		String[] projection = {DatabaseCreator.C_SIMPLE_ID_CONSTRAINT};
		Cursor result = database.query(DatabaseCreator.TABLE_SIMPLE_CONSTRAINTS, projection , where,null, null, null, null);
		int newId=-1;
		while (result.moveToNext()) {
			newId = result.getInt(0);
		}
		result.close();
		return newId;
	}
	
	/**
	 * Return a constraint saved into the database. If id is -1, all constraints are returned.
	 * @param id
	 * @return
	 */
	private static synchronized Constraint getSimpleConstraint(int selectId){
		Constraint fetchedConstraint;
		int id = -1;
		String type="";
		String start ="";
		String end="";
		
		String[] projection = DatabaseCreator.Projections.CONSTRAINTS_SIMPLE_ALL;
		String selection=null;
		if (selectId != -1){
			selection = DatabaseCreator.C_SIMPLE_ID_CONSTRAINT + " = " + selectId;
		}
		String table = DatabaseCreator.TABLE_SIMPLE_CONSTRAINTS;
		Cursor databaseResult = database.query(table, projection, selection, null, null, null, null);
		while (databaseResult.moveToNext()) {
			id = databaseResult.getInt(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_SIMPLE_ID_CONSTRAINT));
			type =  databaseResult.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_SIMPLE_CONSTRAINT_TYPE));
			start = databaseResult.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_START));
			end = databaseResult.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_END));
		}
		databaseResult.close();
		
		//Discerning what type of constraint has been fetched
		if (type == "DateConstraint")
		{
			Time startTime = new Time();
			startTime.set(Long.parseLong(start));
			Time endTime = new Time();
			endTime.set(Long.parseLong(end));
			fetchedConstraint = new DateConstraint(startTime, endTime);
			fetchedConstraint.setId(id);
			return fetchedConstraint;
		}
		if (type == "TimeConstraint")
		{
			Time startTime = new Time();
			startTime.set(Long.parseLong(start));
			Time endTime = new Time();
			endTime.set(Long.parseLong(end));
			fetchedConstraint = new TimeConstraint(startTime, endTime);
			fetchedConstraint.setId(id);
			return fetchedConstraint;
		}
		if (type == "DayConstraint")
		{
			int startDay = Integer.parseInt(start);
			int endDay = Integer.parseInt(end);
			fetchedConstraint = new DayConstraint(startDay, endDay);
			fetchedConstraint.setId(id);
			return fetchedConstraint;
		}
		GiveMeLogger.log("This function cannot parse the constraint row in the db");
		return null;
	}

	private static synchronized ComplexConstraint getComplexConstraint(int selectId){
		ComplexConstraint complexConstraint = new ComplexConstraint();
		String[] projection = DatabaseCreator.Projections.CONSTRAINT_COMPLEX_ALL;
		String selection=null;
		if (selectId != -1){
			selection = DatabaseCreator.C_COMPLEX_ID + " = " + selectId;
		}
		String table = DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS;
		Cursor databaseResult = database.query(table, projection, selection, null, null, null, null);
		ArrayList<Integer> simpleConstraintsId = new ArrayList<Integer>();
		while (databaseResult.moveToNext()) {
			int complexConstraintId = databaseResult.getInt(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_COMPLEX_ID));
			complexConstraint.setId(complexConstraintId);
			int simpleConstraintId = databaseResult.getInt(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.C_COMPLEX_S_ID));
			simpleConstraintsId.add(simpleConstraintId);
		}
		databaseResult.close();
		
		//Now we have the list with all the simpleConstraints related to this complexConstraint
		for (Integer simpleConstraintIndex : simpleConstraintsId) {
			//We are fetching every single simple constraint
			complexConstraint.addConstraint(getSimpleConstraint(simpleConstraintIndex));
		}
		return complexConstraint;
	}
	
	private static synchronized void deleteComplexConstraint (int complexIdToRemove){
		//First let's delete the simpleConstraints
		String[] projection = {DatabaseCreator.C_COMPLEX_S_ID};
		Cursor queryResult = database.query(DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS,projection , DatabaseCreator.C_COMPLEX_ID + " = " + complexIdToRemove, null, null, null, null);
		ArrayList<Integer> simpleConstraintsToRemove = new ArrayList<Integer>();
		while (queryResult.moveToNext()) {
			simpleConstraintsToRemove.add(queryResult.getInt(0));
		}
		queryResult.close();
			//Here simple constraints are removed
			for (Integer simpleIdToRemove : simpleConstraintsToRemove) {
				deleteSimpleConstraint(simpleIdToRemove);
			}
		//Then delete the constraint
		int rowsDeleted = database.delete(DatabaseCreator.TABLE_COMPLEX_CONSTRAINTS, DatabaseCreator.C_COMPLEX_ID + " = " + complexIdToRemove, null);
		GiveMeLogger.log("Deleted " + rowsDeleted + "comples constraints from db");
	}
	private static synchronized void deleteSimpleConstraint(int idToRemove) {
		int rowsDeleted = database.delete(DatabaseCreator.TABLE_SIMPLE_CONSTRAINTS, DatabaseCreator.C_SIMPLE_ID_CONSTRAINT + " = " + idToRemove, null);
		GiveMeLogger.log("Deleted " + rowsDeleted + "simple constraints from db");
	}
	
	/**
	 * This get the opening time of a particular place in the db. Note that they
	 * are not put directly in the PlaceModel object but returned instead.
	 * 
	 * @param place
	 * @return
	 */
    public static List<ComplexConstraint> getOpeningTime(PlaceModel place) {
		List<ComplexConstraint> constraints = new ArrayList<ComplexConstraint>();
		String placeId =place.getPlaceId();
		String table = DatabaseCreator.TABLE_OPENING_TIMES;
		String[] projection = DatabaseCreator.Projections.OT_ALL;
		String where = DatabaseCreator.OT_PLACE_ID + " = "+ "'"+ placeId+ "'";
		ArrayList<Integer> complexConstraintId = new ArrayList<Integer>();

		Cursor queryResult = database.query(table, projection, where, null, null, null, null);
		while(queryResult.moveToNext()){
			complexConstraintId.add(queryResult.getInt(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.OT_COMPLEX_CONSTRAINT)));
			}
		queryResult.close();
		
		//Now fetching all found constraints
		for (Integer compConstraintId : complexConstraintId) {
			constraints.add(getComplexConstraint(compConstraintId));
		}
		return constraints;
	}

	/**
	 * This get the constraints of a particular event in the db. Note that they
	 * are not put directly in the Event Model object but returned instead.
	 * 
	 * @param event
	 * @return
	 */
	public static List<ComplexConstraint> getConstraints(EventDescriptionModel event) {
		List<ComplexConstraint> constraints = new ArrayList<ComplexConstraint>();
		int eventId = Integer.parseInt(event.getID());
		String table = DatabaseCreator.TABLE_EVENT_CONSTRAINTS;
		String[] projection = DatabaseCreator.Projections.ECO_ALL;
		String where = DatabaseCreator.ECO_ID_EVENT + " = " + eventId;
		ArrayList<Integer> complexConstraintId = new ArrayList<Integer>();

		Cursor queryResult = database.query(table, projection, where, null, null, null, null);
		while(queryResult.moveToNext()){
			complexConstraintId.add(queryResult.getInt(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECO_ID_COMPLEX_CONSTRAINT)));
			}
		queryResult.close();
		
		//Now fetching all found constraints
		for (Integer compConstraintId : complexConstraintId) {
			constraints.add(getComplexConstraint(compConstraintId));
		}
		return constraints;
	}

	/**
	 * This function adds opening time of a particular places in db
	 * 
	 * @param place
	 */
	public static void addOpeningTime(PlaceModel place) {
		List<ComplexConstraint> constraints = place.getOpeningTime();
			SparseArray<ComplexConstraint> constraintList = new SparseArray<ComplexConstraint>();
				for (ComplexConstraint complexConstraint : constraints) {
					int constraintId = addComplexConstraintInDatabase(complexConstraint);
					constraintList.put(constraintId, complexConstraint);
				}
				//Now we have a map with id-constraints
				
				String placeId = place.getPlaceId();
				
			for (int i = 0; i < constraintList.size(); i++) {
				int constraintId = constraintList.keyAt(i);
				//Inserting into DB
				ContentValues values = new ContentValues();
				values.put(DatabaseCreator.OT_COMPLEX_CONSTRAINT, constraintId);
				values.put(DatabaseCreator.OT_PLACE_ID, placeId);
				database.insertWithOnConflict(DatabaseCreator.TABLE_OPENING_TIMES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				GiveMeLogger.log("Added row in Opening Times table - Place: " + placeId + " Constraint: " + constraintId);
			}
		
	}

	/**
	 * Removes every old constraints from the EventConstraints table and reinsert current ones
	 * @param event
	 */
	public static void setConstraints(EventDescriptionModel event) {
		//Removing old constraints
		 String[] projection = {DatabaseCreator.ECO_ID_COMPLEX_CONSTRAINT};
		 String where = DatabaseCreator.ECO_ID_EVENT + " = "+ "'" + event.getID()+ "'";
		 Cursor queryResult = database.query(DatabaseCreator.TABLE_EVENT_CONSTRAINTS, projection, where, null, null, null, null);
		 ArrayList<Integer> complexConstraintToRemove = new ArrayList<Integer>();
		 while (queryResult.moveToNext()){
			 complexConstraintToRemove.add(queryResult.getInt(0));
		 }
		 queryResult.close();
		 for (Integer complexConstraintIdToRemove : complexConstraintToRemove) {
			deleteComplexConstraint(complexConstraintIdToRemove);
		}
		 
		//Adding new constraints to database
		List<ComplexConstraint> constraints = event.getConstraints();
		SparseArray<ComplexConstraint> constraintList = new SparseArray<ComplexConstraint>();
		for (ComplexConstraint complexConstraint : constraints) {
			int constraintId = addComplexConstraintInDatabase(complexConstraint);
			constraintList.put(constraintId, complexConstraint);
		}
		//Now we have a map with id-constraints
		
		int eventId = Integer.parseInt(event.getID());
		
	for (int i = 0; i < constraintList.size(); i++) {
		int constraintId = constraintList.keyAt(i);
		//Inserting into DB
		ContentValues values = new ContentValues();
		values.put(DatabaseCreator.ECO_ID_COMPLEX_CONSTRAINT, constraintId);
		values.put(DatabaseCreator.ECO_ID_EVENT, eventId);
		database.insertWithOnConflict(DatabaseCreator.TABLE_EVENT_CONSTRAINTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		GiveMeLogger.log("Added row in Event-Constraint table - Event: " + eventId + " Constraint: " + constraintId);
	}
	}

	
	// ///////////////////////
	//
	//
	// EventCategory functions
	//
	//
	// ///////////////////////
	
	/**
	 * This functions adds the default categories into the GiveMeTime database
	 */
	public static void addDefaultCategories(){
		//Default categories
		EventCategory workCategory = new EventCategory("Work", false, true);
		//They are default category, thus they cannot be removed by the user
		workCategory.setDefaultCategory(true);
		EventCategory errandsCategory = new EventCategory("Errands (Movable)", true, false);
		errandsCategory.setDefaultCategory(true);
		EventCategory amusementCategory = new EventCategory("Amusement", false, false);
		amusementCategory.setDefaultCategory(true);
		
		List<EventCategory> defaultCategories = new ArrayList<EventCategory>();
		
		defaultCategories.add(workCategory);
		defaultCategories.add(errandsCategory);
		defaultCategories.add(amusementCategory);
		
		//Adding categories to database
		for (EventCategory eventCategory : defaultCategories) {
			ContentValues values = new ContentValues();
			values.put(DatabaseCreator.ECA_NAME, eventCategory.getName());
			values.put(DatabaseCreator.ECA_DEFAULT_DONOTDISTURB, eventCategory.isDefault_donotdisturb());
			values.put(DatabaseCreator.ECA_DEFAULT_MOVABLE, eventCategory.isDefault_movable());
			values.put(DatabaseCreator.ECA_DEFAULT_CATEGORY, eventCategory.isDefaultCategory());
			
			//TODO: Check if this update policy erases categories from the EVENT_MODEL table when they update
			Long query = database.insertWithOnConflict(DatabaseCreator.TABLE_EVENT_CATEGORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			GiveMeLogger.log("Added default category row: " + query);
		}
		
	}
	public static void addCategory(EventCategory category){
				//Adding category to database
					ContentValues values = new ContentValues();
					values.put(DatabaseCreator.ECA_NAME, category.getName());
					values.put(DatabaseCreator.ECA_DEFAULT_DONOTDISTURB, category.isDefault_donotdisturb());
					values.put(DatabaseCreator.ECA_DEFAULT_MOVABLE, category.isDefault_movable());
					values.put(DatabaseCreator.ECA_DEFAULT_CATEGORY, category.isDefaultCategory());
					
					//TODO: Check if this update policy erases categories from the EVENT_MODEL table when they update
					Long query = database.insertWithOnConflict(DatabaseCreator.TABLE_EVENT_CATEGORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
					GiveMeLogger.log("Added default constraint row: " + query);
	}
	
	/**
	 * Return all existing Event Categories
	 */
	public static List<EventCategory> getCategories(){
		List<EventCategory> categories = new ArrayList<EventCategory>();
		
		String[] projection = DatabaseCreator.Projections.ECA_ALL;
		String table = DatabaseCreator.TABLE_EVENT_CATEGORY;
		String orderBy = DatabaseCreator.ECA_DEFAULT_CATEGORY+", "+ DatabaseCreator.ECA_NAME;
		
		Cursor fetchedCategories = database.query(table, projection, null, null, null, null, orderBy);
			while (fetchedCategories.moveToNext()) {
				String name = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_NAME));				
				String defaultDoNotDisturb = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_DONOTDISTURB));
				String defaultMovable = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_MOVABLE));
				String defaultCategory = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_CATEGORY));
				
				EventCategory newCategory = new EventCategory(name, (defaultMovable.equals("1")), (defaultDoNotDisturb).equals("1"));
				newCategory.setDefaultCategory((defaultCategory.equals("1")));
				categories.add(newCategory);
			}
		
		
		fetchedCategories.close();
		return categories;
	} 
	/**
	 * Return a DB stored category that has "categoryName" as name
	 * @param categoryName
	 * @return
	 */
	public static EventCategory getCategoryByName(String categoryName){
		EventCategory category = null;
		String[] projection = DatabaseCreator.Projections.ECA_ALL;
		String table = DatabaseCreator.TABLE_EVENT_CATEGORY;
		String where = DatabaseCreator.ECA_NAME + " = " +"'" +categoryName + "'";
		String orderBy = DatabaseCreator.ECA_DEFAULT_CATEGORY+", "+ DatabaseCreator.ECA_NAME;
		
		Cursor fetchedCategories = database.query(table, projection, where, null, null, null, orderBy);
			while (fetchedCategories.moveToNext()) {
				String name = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_NAME));				
				String defaultDoNotDisturb = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_DONOTDISTURB));
				String defaultMovable = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_MOVABLE));
				String defaultCategory = fetchedCategories.getString(DatabaseCreator.Projections.getIndex(projection, DatabaseCreator.ECA_DEFAULT_CATEGORY));
				
				EventCategory newCategory = new EventCategory(name, (defaultMovable.equals("1")), (defaultDoNotDisturb.equals("1")));
				newCategory.setDefaultCategory((defaultCategory.equals("1")));
				category=newCategory;
			}
		fetchedCategories.close();
		return category;
	}
	public static void deleteCategory(EventCategory categoryToDelete){
		String table = DatabaseCreator.TABLE_EVENT_CATEGORY;
		String where = DatabaseCreator.ECA_NAME + " = " + "'" +categoryToDelete.getName() + "'";
		int deleteQuery = database.delete(table, where, null);
		GiveMeLogger.log("Deleted " + deleteQuery + " category rows.");
	}
	
	    ///////////////////////////////
		//
		// User Preferences
		//
		// /////////////////////////////
	
	/**
	 * Adds or updates home place (home has to be already added to Place table)
	 * @param home
	 */
		public static void addUserHomePlace(String account, PlaceModel home){
			//The home place should already be in Places table
			String table = DatabaseCreator.TABLE_USER_PREFERENCE;
			//Building the whole row for update, old sleepTime is needed to preserve it
			int oldSleeptime = getUserSleepTime(account).getId();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseCreator.ACCOUNT, account);
			values.put(DatabaseCreator.HOME_LOCATION, home.getPlaceId());
			values.put(DatabaseCreator.ID_SLEEP_TIME, oldSleeptime);
			
			database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		}
		
		/**
		 * Return the home get location
		 * @return
		 */
		public static PlaceModel getUserHomePlace(String account){
			String resultPlace = "";
			String table = DatabaseCreator.TABLE_USER_PREFERENCE;
			String[] projection = {DatabaseCreator.HOME_LOCATION};
			String where = DatabaseCreator.ACCOUNT + " = " + "'"+ account+ "'";
			
			Cursor result = database.query(table, projection, where, null, null, null, null);
			while (result.moveToNext()){
				resultPlace = result.getString(0);
				}
			result.close();
			PlaceModel homePlace = getPlaceById(resultPlace);
			return homePlace;
		}
		
		/**
		 * Set or updates the user sleep time
		 * @param sleepTime
		 */
		public static void setUserSleepTime(String account, TimeConstraint sleepTime){
			String table = DatabaseCreator.TABLE_USER_PREFERENCE;
			//Building the whole row for update, old HomePlace is needed to preserve it
			String oldHomePlace = getUserHomePlace(account).getPlaceId();
			//Building a complexConstraint to store sleeptime, and adding to db
			ComplexConstraint sleepTimeComplex = new ComplexConstraint();
			sleepTimeComplex.addConstraint(sleepTime);
			int sleepTimeComplexID = addComplexConstraintInDatabase(sleepTimeComplex);
			
			ContentValues values = new ContentValues();
			values.put(DatabaseCreator.ACCOUNT, account);
			values.put(DatabaseCreator.ID_SLEEP_TIME, sleepTimeComplexID);
			values.put(DatabaseCreator.HOME_LOCATION, oldHomePlace);
	
			database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		}
	
		/**
		 * Return the user sleep time
		 * @return
		 */
		public static TimeConstraint getUserSleepTime(String account){
			int resultComplexSleepTime = -1;
			String table = DatabaseCreator.TABLE_USER_PREFERENCE;
			String[] projection = {DatabaseCreator.ID_SLEEP_TIME};
			String where = DatabaseCreator.ACCOUNT + " = " + "'"+ account+ "'";
			
			Cursor result = database.query(table, projection, where, null, null, null, null);
			while (result.moveToNext()){
				resultComplexSleepTime = result.getInt(0);
				}
			result.close();
			ComplexConstraint complexSleepTime = getComplexConstraint(resultComplexSleepTime);
			for (Constraint constraint : complexSleepTime.getConstraints()) {
				if(constraint instanceof TimeConstraint){
					return (TimeConstraint)constraint;
				}
			}
			GiveMeLogger.log("No TimeConstraints found in sleeptime, maybe none has ben set?");
			return null;
			
		}
		
		
		
		/**
		 * Set or update vacation days
		 */
		public static void setUserVacationDays(String account, List<ComplexConstraint> vacationDays){
			//Removing all old entries
			removeUserVacationDays(account);
			//adding complexConstraints to DB
			ArrayList<Integer> constraintIndexList = new ArrayList<Integer>();
			for (ComplexConstraint complexConstraint : vacationDays) {
				int index = addComplexConstraintInDatabase(complexConstraint);
				constraintIndexList.add(index);
			}
			String table = DatabaseCreator.TABLE_VACATION_DAYS;
			
			for (Integer constrintID : constraintIndexList) {
				ContentValues values = new ContentValues();
				values.put(DatabaseCreator.VD_ACCOUNT, account);
				values.put(DatabaseCreator.VD_ID_CONSTRAINT, constrintID);
				database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			}
		}
		
		/**
		 * Delete all user vacations
		 */
		public static void removeUserVacationDays(String account){
			String table = DatabaseCreator.TABLE_VACATION_DAYS;
			String where = DatabaseCreator.VD_ACCOUNT + " = "+ "'" + account+ "'"; 
			database.delete(table, where, null);
		}
		/**
		 * Return the vacation periods
		 * @return
		 */
		public static List<ComplexConstraint> getVacationDays(String account){
			String table = DatabaseCreator.TABLE_VACATION_DAYS;
			String where = DatabaseCreator.VD_ACCOUNT + " = " +  "'"+account+ "'"; 
			String[] projection = {DatabaseCreator.VD_ID_CONSTRAINT};
			ArrayList<Integer> resultConstraint = new ArrayList<Integer>();
			ArrayList<ComplexConstraint> results = new ArrayList<ComplexConstraint>();
			//Getting constraint ids
			Cursor result = database.query(table, projection, where, null, null, null, null);
			while (result.moveToNext()){
				int constraintId = result.getInt(0);
				resultConstraint.add(constraintId);
				}
			result.close();
			
			//Fetching constraints
			for (Integer constraintId : resultConstraint) {
				ComplexConstraint  constraint = getComplexConstraint(constraintId);
				results.add(constraint);
			}
			return results;
		}
		/**
		 * Set the user work timetable
		 */
		public static void setUserWorkTimetable(String account, List<ComplexConstraint> workTimetable){
			//Removing all old entries
			removeUserWorkTimetable(account);
			//adding complexConstraints to DB
			ArrayList<Integer> constraintIndexList = new ArrayList<Integer>();
			for (ComplexConstraint complexConstraint : workTimetable) {
				int index = addComplexConstraintInDatabase(complexConstraint);
				constraintIndexList.add(index);
			}
			String table = DatabaseCreator.TABLE_WORK_TIMETABLE;
			
			for (Integer constrintID : constraintIndexList) {
				ContentValues values = new ContentValues();
				values.put(DatabaseCreator.WT_ACCOUNT, account);
				values.put(DatabaseCreator.WT_ID_CONSTRAINT, constrintID);
				database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			}
		}
		/**
		 * Returns the user work timetable
		 * @return
		 */
		public static List<ComplexConstraint> getUserWorkTimetable(String account){
			String table = DatabaseCreator.TABLE_WORK_TIMETABLE;
			String where = DatabaseCreator.WT_ACCOUNT + " = "+ "'" + account+ "'"; 
			String[] projection = {DatabaseCreator.WT_ID_CONSTRAINT};
			ArrayList<Integer> resultConstraint = new ArrayList<Integer>();
			ArrayList<ComplexConstraint> results = new ArrayList<ComplexConstraint>();
			//Getting constraint ids
			Cursor result = database.query(table, projection, where, null, null, null, null);
			while (result.moveToNext()){
				int constraintId = result.getInt(0);
				resultConstraint.add(constraintId);
				}
			result.close();
			
			//Fetching constraints
			for (Integer constraintId : resultConstraint) {
				ComplexConstraint  constraint = getComplexConstraint(constraintId);
				results.add(constraint);
			}
			return results;
		}
		/**
		 * Remove the user work timetable
		 * @param account 
		 */
		public static void removeUserWorkTimetable(String account){
			String table = DatabaseCreator.TABLE_WORK_TIMETABLE;
			String where = DatabaseCreator.WT_ACCOUNT + " = " + "'"+ account+ "'"; 
			database.delete(table, where, null);
		}
		// /////////////////////////////
		//
		// Questions
		//
		// /////////////////////////////		
	
		public static void addQuestion(QuestionModel question){
			//TODO: IMPLEMENT THIS
		}
		public static void removeQuestion(QuestionModel question){
			//TODO: IMPLEMENT THIS
		}
		public static List<QuestionModel> getQuestions(){
			//TODO: IMPLEMENT THIS
			return null;
		}
		
		public static void generateMissingDataQuestions(final Context context, final OnDatabaseUpdatedListener<SparseArray<OptimizingQuestion>> listener){
			Time now = new Time();
			now.setToNow();
			Time end = new Time();
			long ninetyDaysMillis = (long)1000*(long)60*(long)60*(long)24*(long)90;
			end.set(now.toMillis(false)+ninetyDaysMillis);
			final SparseArray<OptimizingQuestion> questions = new SparseArray<OptimizingQuestion>();
			//Getting results from getEventInstances
			EventListener<EventInstanceModel> eventResults = new EventListener<EventInstanceModel>() {
				@Override
				public void onLoadCompleted() {
				listener.updateFinished(questions);
				}
				@Override
				public void onEventCreation(EventInstanceModel newEvent) {
					//Parsing the event and getting the question
					EventDescriptionModel event = newEvent.getEvent();
					boolean missingCategory = false;
					boolean missingPlace = false;
					boolean missingConstraints = false;
					if (event.getCategory() == null){
						missingCategory = true;
					}
					if(event.getPlace() == null || event.getPlace().getPlaceId()==null){
						missingPlace=true;
					}
					if(event.getIsMovable() && event.getConstraints().isEmpty()){
						missingConstraints =true;
					}
					//Generating new question for the event
					if(missingCategory||missingPlace||missingConstraints){
					OptimizingQuestion newQuestion = new OptimizingQuestion(context, event, missingPlace, missingCategory, missingConstraints);
					questions.put(Integer.parseInt(event.getID()), newQuestion);
					}
				}
				@Override
				public void onEventChange(EventInstanceModel newEvent) {
				}
			};
			getEventsInstances(now, end, context, eventResults);
		}
		
		
		
	// /////////////////////////////
	//
	// Database
	//
	// /////////////////////////////

	/**
	 * This helper class creates the GiveMeTime database
	 * 
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com> Paolo Bassi
	 * 
	 */
	private static class DatabaseCreator extends SQLiteOpenHelper {
		static class Projections {
			public static final String[] PLACES_ALL = { PLACE_ID, PLACE_NAME,
					PLACE_ADDRESS, PLACE_FORMATTED_ADDRESS, PLACE_COUNTRY,
					PLACE_PHONE_NUMBER, PLACE_ICON, PLACE_LOCATION_LONGITUDE,
					PLACE_LOCATION_LATITUDE, PLACE_VISIT_COUNTER,
					PLACE_DATE_CREATED };
			public static final String[] ECA_ALL = {ECA_NAME, ECA_DEFAULT_DONOTDISTURB, ECA_DEFAULT_MOVABLE, ECA_DEFAULT_CATEGORY};
			public static final String[] ECO_ALL = {ECO_ID_EVENT, ECO_ID_COMPLEX_CONSTRAINT};
			public static final String[] CONSTRAINTS_SIMPLE_ALL = {C_SIMPLE_ID_CONSTRAINT, C_SIMPLE_CONSTRAINT_TYPE, C_START, C_END};
			public static final String[] CONSTRAINT_COMPLEX_ALL = {C_COMPLEX_ID, C_COMPLEX_S_ID};
			public static final String[] OT_ALL = {OT_PLACE_ID, OT_COMPLEX_CONSTRAINT};
			public static final String[] EVENT_MODEL_ALL = { ID_CALENDAR, ID_EVENT_PROVIDER,ID_PLACE, ID_EVENT_CATEGORY, FLAG_DO_NOT_DISTURB,FLAG_DEADLINE,FLAG_MOVABLE };
			public static int getIndex(String[] projection, String coloumn) {
				int counter = 0;
				for (String currentColoumn : projection) {
					if (currentColoumn == coloumn)
						return counter;
					else {
						counter++;
					}
				}
				return -1;
			}
		}

		// Database Name
		private static final String DATABASE_NAME = "givemetime.db";
		// Database Version
		private static final int DATABASE_VERSION = 1;
		// Database Tables
		static final String TABLE_EVENT_MODEL = "event_model";
		static final String TABLE_PLACE_MODEL = "place_model";
		static final String TABLE_QUESTION_MODEL = "question_model";
		static final String TABLE_OPENING_TIMES = "opening_times";
		static final String TABLE_EVENT_CATEGORY = "event_category";
		static final String TABLE_EVENT_CONSTRAINTS = "event_constraints";
		static final String TABLE_SIMPLE_CONSTRAINTS = "simple_constraints";
		static final String TABLE_COMPLEX_CONSTRAINTS = "complex_constraints";
		static final String TABLE_USER_PREFERENCE = "user_preference";
		static final String TABLE_VACATION_DAYS = "vacation_days";
		static final String TABLE_WORK_TIMETABLE = "work_timetable";

		// Database Column Names
		// EVENT_MODEL
		private static final String ID_CALENDAR = "id_calendar";
		private static final String ID_EVENT_PROVIDER = "id_event_provider";
		private static final String ID_EVENT_CATEGORY = "id_event_category";
		private static final String ID_PLACE = "id_place";
		private static final String FLAG_DO_NOT_DISTURB = "do_not_disturb_flag";
		private static final String FLAG_DEADLINE = "flag_deadline";
		private static final String FLAG_MOVABLE = "flag_movable";
		// PLACE_MODEL
		private static final String PLACE_ID = "place_id";
		private static final String PLACE_NAME = "place_name";
		private static final String PLACE_ADDRESS = "place_address";
		private static final String PLACE_FORMATTED_ADDRESS = "place_formatted_address";
		private static final String PLACE_COUNTRY = "place_country";
		private static final String PLACE_PHONE_NUMBER = "place_phone_number";
		private static final String PLACE_ICON = "place_icon";
		private static final String PLACE_LOCATION_LATITUDE = "place_location_latitude";
		private static final String PLACE_LOCATION_LONGITUDE = "place_location_longitude";
		private static final String PLACE_VISIT_COUNTER = "place_visit_counter";
		private static final String PLACE_DATE_CREATED = "place_date_created";
		//OPENING_TIMES
		private static final String OT_PLACE_ID = "ot_place_id";
		private static final String OT_COMPLEX_CONSTRAINT = "ot_constraint_id";
		// QUESTION_MODEL
		private static final String ID_QUESTION = "id_question";
		private static final String DATE_TIME = "date_time";
		private static final String TYPE_QUESTION = "type_question";
		private static final String EVENT_ID = "event_id";
		private static final String USER_LOCATION = "user_location";
		// EVENT CATEGORY
		private static final String ECA_NAME = "eca_name";
		private static final String ECA_DEFAULT_DONOTDISTURB = "default_donotdisturb";
		private static final String ECA_DEFAULT_MOVABLE = "default_movable";
		private static final String ECA_DEFAULT_CATEGORY = "default_category";
		// EVENT CONSTRAINT
		private static final String ECO_ID_COMPLEX_CONSTRAINT = "eco_id_complex_constraint";
		private static final String ECO_ID_EVENT = "eco_id_event";
		// SIMPLE CONSTRAINTS
		private static final String C_SIMPLE_ID_CONSTRAINT = "c_simple_id_constraint";
		private static final String C_SIMPLE_CONSTRAINT_TYPE = "c_simple_constraint_type";
		private static final String C_START = "c_start";
		private static final String C_END = "c_end";
		// COMPLEX CONSTRAINTS
		private static final String C_COMPLEX_ID = "c_complex_id_constraint";
		private static final String C_COMPLEX_S_ID = "c_complex_simple_id";
		// USER_PREFERENCE
		private static final String ACCOUNT = "account";
		private static final String HOME_LOCATION = "home_location";
		private static final String ID_SLEEP_TIME = "id_sleep_time";
		// VACATION DAYS
		private static final String VD_ACCOUNT = "vd_account";
		private static final String VD_ID_CONSTRAINT = "vd_id_constraint";
		// WORK TIMETABLE
		private static final String WT_ACCOUNT = "wt_account";
		private static final String WT_ID_CONSTRAINT = "wt_id_constraint";

		// Table Create Statements
		// EVENT_MODEL
		private static final String CREATE_TABLE_EVENT_MODEL = "CREATE TABLE "
				+ TABLE_EVENT_MODEL + "(" + ID_CALENDAR + " INT NOT NULL, "
				+ ID_EVENT_PROVIDER + " INT NOT NULL, " + ID_EVENT_CATEGORY
				+ " VARCHAR(30), " + ID_PLACE + " VARCHAR(255), "
				+ FLAG_DO_NOT_DISTURB + " BOOLEAN, " + FLAG_DEADLINE
				+ " BOOLEAN, " + FLAG_MOVABLE + " BOOLEAN, " 
				+ " PRIMARY KEY (" + ID_CALENDAR + ", " + ID_EVENT_PROVIDER + "),"
				+ " FOREIGN KEY (" + ID_EVENT_CATEGORY + ") REFERENCES " + TABLE_EVENT_CATEGORY + " (" + ECA_NAME + ")"
				+ " FOREIGN KEY (" + ID_PLACE + ") REFERENCES " + TABLE_PLACE_MODEL + " (" + PLACE_ID + ")" 
				+ ");";
		// PLACE_MODEL
		private static final String CREATE_TABLE_PLACE_MODEL = "CREATE TABLE "
				+ TABLE_PLACE_MODEL + "(" + PLACE_ID
				+ " VARCHAR(255) PRIMARY KEY NOT NULL, " + PLACE_NAME
				+ " VARCHAR(50), " + PLACE_ADDRESS + " VARCHAR(50), "
				+ PLACE_FORMATTED_ADDRESS + " VARCHAR(255), " + PLACE_COUNTRY
				+ " VARCHAR(50), " + PLACE_PHONE_NUMBER + " VARCHAR(50), "
				+ PLACE_ICON + " VARCHAR(255), " + PLACE_LOCATION_LATITUDE
				+ " VARCHAR(50), " + PLACE_LOCATION_LONGITUDE
				+ " VARCHAR(50), " + PLACE_VISIT_COUNTER + " VARCHAR(50), "
				+ PLACE_DATE_CREATED + " VARCHAR(50)" + 
				" );";
		// OPENING_TIMES
		private static final String CREATE_TABLE_OPENING_TIMES = "CREATE TABLE "
						+ TABLE_OPENING_TIMES + "(" 
						+ OT_PLACE_ID + " VARCHAR(255), " 
						+ OT_COMPLEX_CONSTRAINT + " INTEGER NOT NULL,"
						+ " PRIMARY KEY ("+ OT_PLACE_ID + ", " + OT_COMPLEX_CONSTRAINT +"),"
						+ " FOREIGN KEY (" + OT_PLACE_ID + ") REFERENCES " + TABLE_PLACE_MODEL + "(" + PLACE_ID + ")"
						+ " FOREIGN KEY (" + OT_COMPLEX_CONSTRAINT + ") REFERENCES " + TABLE_COMPLEX_CONSTRAINTS + "(" + C_COMPLEX_ID + ")"
						+ " );";
		
		// QUESTION_MODEL
		private static final String CREATE_TABLE_QUESTION_MODEL = "CREATE TABLE "
				+ TABLE_QUESTION_MODEL
				+ "("
				+ ID_QUESTION
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ DATE_TIME
				+ " DATE, "
				+ TYPE_QUESTION
				+ " VARCHAR(30), "
				+ EVENT_ID
				+ " INT, "
				+ USER_LOCATION
				+ " VARCHAR(255), "
				+ " FOREIGN KEY ("
				+ EVENT_ID
				+ ") REFERENCES "
				+ TABLE_EVENT_MODEL
				+ " ("
				+ ID_EVENT_PROVIDER
				+ ")"
				+ " FOREIGN KEY ("
				+ USER_LOCATION
				+ ") REFERENCES "
				+ TABLE_PLACE_MODEL + " (" + PLACE_ID + ")" + ");";
		// EVENT_CATEGORY
		private static final String CREATE_TABLE_EVENT_CATEGORY = "CREATE TABLE "
				+ TABLE_EVENT_CATEGORY
				+ "("
				+ ECA_NAME 	+ " VARCHAR(30) PRIMARY KEY, "	
				+ ECA_DEFAULT_DONOTDISTURB	+ " BOOLEAN," 
				+ ECA_DEFAULT_MOVABLE	+ " BOOLEAN," 
				+ ECA_DEFAULT_CATEGORY	+ " BOOLEAN" 
				+ ");";
		// EVENT_CONSTRAINTS
		private static final String CREATE_TABLE_EVENT_CONSTRAINTS = "CREATE TABLE "
				+ TABLE_EVENT_CONSTRAINTS
				+ "(" + ECO_ID_COMPLEX_CONSTRAINT + " INT, "
				+ ECO_ID_EVENT 	+ " INT, " + " PRIMARY KEY (" 
				+ ECO_ID_COMPLEX_CONSTRAINT + ", " + ECO_ID_EVENT + "),"
				+ " FOREIGN KEY (" + ECO_ID_COMPLEX_CONSTRAINT + ") REFERENCES " + TABLE_COMPLEX_CONSTRAINTS + " (" + C_COMPLEX_ID + ")"
				+ " FOREIGN KEY (" + ECO_ID_EVENT + ") REFERENCES "+ TABLE_EVENT_MODEL + " (" + ID_EVENT_PROVIDER + ")" 
				+ ");";
		// SIMPLE_CONSTRAINTS
		private static final String CREATE_TABLE_SIMPLE_CONSTRAINTS = "CREATE TABLE "
				+ TABLE_SIMPLE_CONSTRAINTS + "(" 
				+ C_SIMPLE_ID_CONSTRAINT + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ C_SIMPLE_CONSTRAINT_TYPE + " VARCHAR(30), " 
				+ C_START + " VARCHAR(30), " 
				+ C_END + " VARCHAR(30)" 
				+ ");";
		//COMPLEX_CONSTRAINTS
		private static final String CREATE_TABLE_COMPLEX_CONSTRAINTS = "CREATE TABLE "
						+ TABLE_COMPLEX_CONSTRAINTS + "(" 
						+ C_COMPLEX_ID + " INTEGER NOT NULL, "
						+ C_COMPLEX_S_ID + " INTEGER NOT NULL, " 
						+ " PRIMARY KEY ("+ C_COMPLEX_ID + ", " + C_COMPLEX_S_ID +"),"
						+ " FOREIGN KEY (" + C_COMPLEX_ID + ") REFERENCES " + TABLE_SIMPLE_CONSTRAINTS + "(" + C_SIMPLE_ID_CONSTRAINT + ")"
						+ ");";
		
		// USER_PREFERENCE
		private static final String CREATE_TABLE_USER_PREFERENCE = "CREATE TABLE "
				+ TABLE_USER_PREFERENCE + "("
				+ ACCOUNT + " VARCHAR(30) PRIMARY KEY, "
				+ HOME_LOCATION	+ " VARCHAR(255), "
				+ ID_SLEEP_TIME	+ " INT, "+ " FOREIGN KEY ("+ ACCOUNT+ ") REFERENCES "+ TABLE_VACATION_DAYS	+ " ("	+ VD_ACCOUNT+ ")"	
				+ " FOREIGN KEY ("+ HOME_LOCATION+ ") REFERENCES "+ TABLE_PLACE_MODEL+ " ("+ PLACE_ID+ ")"
				+ " FOREIGN KEY ("+ ID_SLEEP_TIME+ ") REFERENCES "+ TABLE_COMPLEX_CONSTRAINTS+ " ("+ C_COMPLEX_ID + ")" 
				+ ");";
		// VACATION_DAYS
		private static final String CREATE_TABLE_VACATION_DAYS = "CREATE TABLE "
				+ TABLE_VACATION_DAYS
				+ "("
				+ VD_ACCOUNT + " VARCHAR(30), "
				+ VD_ID_CONSTRAINT+ " INT, "+ 
				" PRIMARY KEY ("+ VD_ACCOUNT+ ", "+ VD_ID_CONSTRAINT+ "),"
				+ " FOREIGN KEY ("+ VD_ACCOUNT+ ") REFERENCES "+ TABLE_USER_PREFERENCE+ " ("+ ACCOUNT+ ")"
				+ " FOREIGN KEY ("+ VD_ID_CONSTRAINT+ ") REFERENCES "+ TABLE_COMPLEX_CONSTRAINTS+ " ("+ C_COMPLEX_ID+ ")" 
				+ ");";
		// WORK_TIMETABLE
		private static final String CREATE_TABLE_WORK_TIMETABLE = "CREATE TABLE "
				+ TABLE_WORK_TIMETABLE 	+ "("
				+ WT_ACCOUNT+ " VARCHAR(30), "
				+ WT_ID_CONSTRAINT+ " INT, "
				+ " PRIMARY KEY ("+ WT_ACCOUNT+ ", "+ WT_ID_CONSTRAINT+ "),"
				+ " FOREIGN KEY ("+ WT_ACCOUNT+ ") REFERENCES "+ TABLE_USER_PREFERENCE+ " ("+ ACCOUNT+ ")"
				+ " FOREIGN KEY ("+ WT_ID_CONSTRAINT+ ") REFERENCES "+ TABLE_COMPLEX_CONSTRAINTS+ " ("+ C_COMPLEX_ID+ ")" 
				+ ");";

		public static DatabaseCreator createHelper(Context context) {
			return new DatabaseCreator(context);
		}

		public DatabaseCreator(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			// generate all the tables of the db
			db.execSQL(CREATE_TABLE_EVENT_MODEL);
			db.execSQL(CREATE_TABLE_PLACE_MODEL);
			db.execSQL(CREATE_TABLE_QUESTION_MODEL);
			db.execSQL(CREATE_TABLE_OPENING_TIMES);
			db.execSQL(CREATE_TABLE_EVENT_CATEGORY);
			db.execSQL(CREATE_TABLE_EVENT_CONSTRAINTS);
			db.execSQL(CREATE_TABLE_SIMPLE_CONSTRAINTS);
			db.execSQL(CREATE_TABLE_COMPLEX_CONSTRAINTS);
			db.execSQL(CREATE_TABLE_USER_PREFERENCE);
			db.execSQL(CREATE_TABLE_VACATION_DAYS);
			db.execSQL(CREATE_TABLE_WORK_TIMETABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			GiveMeLogger.log("Upgrading database from version " + oldVersion
					+ " to " + newVersion + " which will destroy all old data");
			// on upgrade drop older tables
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_MODEL);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPENING_TIMES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_CATEGORY);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_CONSTRAINTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIMPLE_CONSTRAINTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLEX_CONSTRAINTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PREFERENCE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_VACATION_DAYS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK_TIMETABLE);

			// create new tables
			onCreate(db);

		}

	}
}
