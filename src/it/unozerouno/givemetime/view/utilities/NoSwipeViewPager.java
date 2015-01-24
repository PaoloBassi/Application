package it.unozerouno.givemetime.view.utilities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSwipeViewPager extends ViewPager{

	public NoSwipeViewPager(Context context) {
		super(context);
	}
	
	public NoSwipeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptHoverEvent(MotionEvent event) {
		// do not allow swiping between pages
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// do not allow swiping between pages
		return false;
	}

}
