package it.unozerouno.givemetime.view.main.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import com.google.android.gms.drive.internal.SetFileUploadPreferencesRequest;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.questions.FreeTimeQuestion;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
import it.unozerouno.givemetime.model.questions.OptimizingQuestion;
import it.unozerouno.givemetime.model.questions.QuestionModel;
import it.unozerouno.givemetime.model.questions.QuestionModel.OnQuestionGenerated;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.main.MainActivity;
import it.unozerouno.givemetime.view.questions.QuestionActivity;
import it.unozerouno.givemetime.view.utilities.SwipeDismissListViewTouchListener;
import it.unozerouno.givemetime.view.utilities.SwipeDismissListViewTouchListener.DismissCallbacks;
import it.unozerouno.givemetime.view.utilities.TimeConversion;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends Fragment implements OnQuestionGenerated{
	ListView questionList;
	ArrayList<Intent> questionIntents;
	QuestionIntentAdapter adapter;
	public static final String ITEM_NAME = "item_name";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.toolbar.setTitle("Questions");
		View view = inflater.inflate(R.layout.fragment_main_layout, container, false);
		questionList = (ListView) view.findViewById(R.id.main_question_list);	
		questionIntents = new ArrayList<Intent>();
		setUpQuestionList();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		GiveMeLogger.log("Generating missing data questions");
		DatabaseManager.getInstance(getActivity());
		DatabaseManager.generateMissingDataQuestions(getActivity(), this);
	}
	
	private synchronized void setUpQuestionList(){
		adapter = new QuestionIntentAdapter(getActivity(), R.layout.element_list_questions, questionIntents);
		 questionList.setAdapter(adapter);
		  SwipeDismissListViewTouchListener touchListener =
				         new SwipeDismissListViewTouchListener(questionList, new DismissCallbacks() {
		                      public synchronized void onDismiss(ListView listView, int[] reverseSortedPositions) {
		                          for (int position : reverseSortedPositions) {
		                        	  GiveMeLogger.log("Removing item " + position + " adapter has " + adapter.getCount() + " and array has " + questionIntents.size() );
		                        	//Deleting swiped question
		                              Intent questionIntent = adapter.getItem(position);
		                              int questionId = questionIntent.getIntExtra(QuestionModel.QUESTION_ID, -1);
		                              DatabaseManager.removeQuestion(questionId);
		                        	  adapter.remove(adapter.getItem(position));
		                              GiveMeLogger.log("Removed item " + position + " adapter has " + adapter.getCount() + " and array has " + questionIntents.size() );
		                              
		                          }
		                         adapter.notifyDataSetChanged();
		                     }

							@Override
							public boolean canDismiss(int position) {
								return true;
							}
		                  });
				                  
		  questionList.setOnTouchListener(touchListener);
		  questionList.setOnScrollListener(touchListener.makeScrollListener());
		  questionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				Intent questionIntent = (Intent) adapter.getItemAtPosition(position);
				startActivity(questionIntent);
			}
		});
	}
	
	private synchronized void reloadQuestionList(){
		 GiveMeLogger.log("Reloading list, array has " + questionIntents.size() + " and adapter has " + adapter.getCount() );
		questionIntents.clear();
		DatabaseManager.getInstance(getActivity());
		questionIntents.addAll(DatabaseManager.getQuestions(getActivity(), QuestionActivity.class));
		adapter.notifyDataSetChanged();
		GiveMeLogger.log("Reloaded list, now array has " + questionIntents.size() + " and adapter has " + adapter.getCount() );
	}
	
	
	/**
	 * Adapter for the question list
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	 private class QuestionIntentAdapter extends ArrayAdapter<Intent>{

			public QuestionIntentAdapter(Context context, int resource, List<Intent> objects) {
				super(context, resource, objects);
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return getViewOptimize(position, convertView, parent);
			}
			
			
			public View getViewOptimize(int position, View convertView, ViewGroup parent) {
				   ViewHolder viewHolder = null;
			        if (convertView == null) {
				  LayoutInflater inflater = (LayoutInflater) getContext()
				             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				        convertView = inflater.inflate(R.layout.element_list_questions, null);
				        viewHolder = new ViewHolder();
				        viewHolder.description = (TextView)convertView.findViewById(R.id.question_list_description);
				        viewHolder.icon = (ImageView)convertView.findViewById(R.id.question_list_icon);
				        viewHolder.time = (TextView) convertView.findViewById(R.id.question_list_time);
				        convertView.setTag(viewHolder);
			        }
			        else
			        {
			        	viewHolder = (ViewHolder) convertView.getTag();
			        }
				        
				        Intent questionIntent = getItem(position);
				        String questionType = questionIntent.getStringExtra(QuestionModel.QUESTION_TYPE);
				        String questionTime = questionIntent.getStringExtra(QuestionModel.QUESTION_TIME);
				        String questionText = questionIntent.getStringExtra(QuestionModel.QUESTION_TEXT);
				        viewHolder.setDescription(questionText);
				        viewHolder.setTime(questionTime);
				        viewHolder.setIcon(questionType);
				        
				        return convertView;
			}
			/**
			 * Avoids the inflatation of every single element on list scroll
			 * @author Edoardo Giacomello
			 *
			 */
			 private class ViewHolder {
			        public TextView description;
			        public TextView time;
			        public ImageView icon;
			        public void setDescription(String text){
			        		description.setText(text);
			        }
			        public void setTime(String questionTimeString){
			        	Time questionTime = new Time();
			        	questionTime.set(Long.parseLong(questionTimeString));
			        	time.setText(TimeConversion.timeToString(questionTime, true, false, true, false));
			        }
			        public void setIcon(String questionType){
			        	if (questionType.equals(OptimizingQuestion.TYPE)){
			        		icon.setImageResource(R.drawable.ic_stat_question_missing_data);
			        	}
			        	if (questionType.equals(LocationMismatchQuestion.TYPE)){
			        		icon.setImageResource(R.drawable.ic_stat_question_location);
			        	}
			        	if (questionType.equals(FreeTimeQuestion.TYPE)){
			        		icon.setImageResource(R.drawable.ic_stat_question_freetime);
			        	}
			        }
			    }
	    }


	@Override
	public void onQuestionGenerated() {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				reloadQuestionList();
			}
		});
		
	}
}
