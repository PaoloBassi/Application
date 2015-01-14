package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.utils.Direction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * This class creates the view to show all the events related to a selected period in a tipical calendar view
 * @author Paolo
 * Taken from https://github.com/alamkanak/Android-Week-View and
 *
 */
public class WeekView extends View{
	
    public static final int LENGTH_SHORT = 1;
    public static final int LENGTH_LONG = 2;
    private final Context context;
    private Calendar today;
    private Calendar startDate;
    // Paint variables
    private Paint timeTextPaint;
    private Paint headerTextPaint;
    private Paint dayBackgroundPaint;
    private Paint hourSeparatorPaint;
    private Paint headerBackgroundPaint;
    private Paint todayBackgroundPaint;
    private Paint todayHeaderTextPaint;
    private Paint eventBackgroundPaint;
    private Paint headerColumnBackgroundPaint;
    private TextPaint eventTextPaint;
    // float variables
    private float timeTextWidth;
    private float timeTextHeight;
    private float headerTextHeight;
    private float widthPerDay;
    private float headerMarginBottom;
    private float headerColumnWidth;
    private float distanceY = 0;
    private float distanceX = 0;
    
    private GestureDetectorCompat gestureDetector;
    private Scroller stickyScroller;
    private OverScroller scroller;
    private PointF currentOrigin = new PointF(0f, 0f);
    
    private Direction currentScrollDirection = Direction.NONE;
    private Direction currentFlingDirection = Direction.NONE;
    
    private List<EventRect> eventRects;

    private int fetchedMonths[] = new int[3];
    private boolean refreshEvents = false;
	
	// Attributes with their default values
    private int hourHeight = 50;
    private int columnGap = 10;
    private int firstDayOfWeek = Calendar.MONDAY;
    private int textSize = 12;
    private int headerColumnPadding = 10;
    private int headerColumnTextColor = Color.BLACK;
    private int numberOfVisibleDays = 3;
    private int headerRowPadding = 10;
    private int headerRowBackgroundColor = Color.WHITE;
    private int dayBackgroundColor = Color.rgb(245, 245, 245);
    private int hourSeparatorColor = Color.rgb(230, 230, 230);
    private int todayBackgroundColor = Color.rgb(239, 247, 254);
    private int hourSeparatorHeight = 2;
    private int todayHeaderTextColor = Color.rgb(39, 137, 228);
    private int eventTextSize = 12;
    private int eventTextColor = Color.BLACK;
    private int eventPadding = 8;
    private int headerColumnBackgroundColor = Color.WHITE;
    private int defaultEventColor;
    private boolean isFirstDraw = true;
    private int dayNameLength = LENGTH_LONG;
    private int overlappingEventGap = 0;
    private int eventMarginVertical = 0;
    private Calendar firstVisibleDay;
    private Calendar lastVisibleDay;
    
    
    ////////////////////////////////////////////////////////////
    //
    //	Listeners
    //
    ////////////////////////////////////////////////////////////
    
    
    public EventClickListener eventClickListener;
    public EventLongPressListener eventLongPressListener;
    public MonthChangeListener monthChangeListener;
    // Listener for gestures
    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            scroller.forceFinished(true);
            stickyScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float _distanceX, float _distanceY) {
            if (currentScrollDirection == Direction.NONE) {
                if (Math.abs(distanceX) > Math.abs(distanceY)){
                    if(distanceX > 0)
                        currentScrollDirection = Direction.LEFT;
                    else
                        currentScrollDirection = Direction.RIGHT;

                    currentFlingDirection = currentScrollDirection;
                }
                else {
                    currentFlingDirection = Direction.VERTICAL;
                    currentScrollDirection = Direction.VERTICAL;
                }
            }
            distanceX = _distanceX;
            distanceY = _distanceY;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.forceFinished(true);
            stickyScroller.forceFinished(true);

            if (currentFlingDirection.isHorizontal()){
                scroller.fling((int) currentOrigin.x, 0, (int) velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
            else if (currentFlingDirection == Direction.VERTICAL){
                scroller.fling(0, (int) currentOrigin.y, 0, (int) velocityY, 0, 0, (int) -(hourHeight * 24 + headerTextHeight + headerRowPadding * 2 - getHeight()), 0);
            }

            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (eventRects != null && eventClickListener != null) {
                List<EventRect> reversedEventRects = eventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        eventClickListener.onEventClick(event.originalEvent, event.rectF);
                        playSoundEffect(SoundEffectConstants.CLICK);
                        break;
                    }
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            if (eventLongPressListener != null && eventRects != null) {
                List<EventRect> reversedEventRects = eventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        eventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        break;
                    }
                }
            }
        }
    };
    
    
    ////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ////////////////////////////////////////////////////////////
    
    public WeekView(Context context) {
		this(context, null);
	}
    
    public WeekView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
    
    public WeekView(Context context, AttributeSet attrs, int defStyleAttr){
    	super(context, attrs, defStyleAttr);
    	
    	// hold reference
    	this.context = context;
    	
    	// Get the attribute values (if any)
    	TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);
    	
    	// assign a format to every attribute
    	try {
            firstDayOfWeek = a.getInteger(R.styleable.WeekView_firstDayOfWeek, firstDayOfWeek);
            hourHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourHeight, hourHeight);
            textSize = a.getDimensionPixelSize(R.styleable.WeekView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
            headerColumnPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerColumnPadding, headerColumnPadding);
            columnGap = a.getDimensionPixelSize(R.styleable.WeekView_columnGap, columnGap);
            headerColumnTextColor = a.getColor(R.styleable.WeekView_headerColumnTextColor, headerColumnTextColor);
            numberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, numberOfVisibleDays);
            headerRowPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerRowPadding, headerRowPadding);
            headerRowBackgroundColor = a.getColor(R.styleable.WeekView_headerRowBackgroundColor, headerRowBackgroundColor);
            dayBackgroundColor = a.getColor(R.styleable.WeekView_dayBackgroundColor, dayBackgroundColor);
            hourSeparatorColor = a.getColor(R.styleable.WeekView_hourSeparatorColor, hourSeparatorColor);
            todayBackgroundColor = a.getColor(R.styleable.WeekView_todayBackgroundColor, todayBackgroundColor);
            hourSeparatorHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight, hourSeparatorHeight);
            todayHeaderTextColor = a.getColor(R.styleable.WeekView_todayHeaderTextColor, todayHeaderTextColor);
            eventTextSize = a.getDimensionPixelSize(R.styleable.WeekView_eventTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, eventTextSize, context.getResources().getDisplayMetrics()));
            eventTextColor = a.getColor(R.styleable.WeekView_eventTextColor, eventTextColor);
            eventPadding = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight, eventPadding);
            headerColumnBackgroundColor = a.getColor(R.styleable.WeekView_headerColumnBackground, headerColumnBackgroundColor);
            dayNameLength = a.getInteger(R.styleable.WeekView_dayNameLength, dayNameLength);
            overlappingEventGap = a.getDimensionPixelSize(R.styleable.WeekView_overlappingEventGap, overlappingEventGap);
            eventMarginVertical = a.getDimensionPixelSize(R.styleable.WeekView_eventMarginVertical, eventMarginVertical);
        } finally {
            a.recycle();
        }
    	
    	initialize();
    }
    
    /**
     * initialize all attributes values
     */
    
    private void initialize(){
    	// Get the date today.
        today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        // Scrolling initialization.
        gestureDetector = new GestureDetectorCompat(context, gestureListener);
        scroller = new OverScroller(context);
        stickyScroller = new Scroller(context);

        // Measure settings for time column.
        timeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timeTextPaint.setTextAlign(Paint.Align.RIGHT);
        timeTextPaint.setTextSize(textSize);
        timeTextPaint.setColor(headerColumnTextColor);
        Rect rect = new Rect();
        timeTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        timeTextWidth = timeTextPaint.measureText("00 PM");
        timeTextHeight = rect.height();
        headerMarginBottom = timeTextHeight / 2;

        // Measure settings for header row.
        headerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerTextPaint.setColor(headerColumnTextColor);
        headerTextPaint.setTextAlign(Paint.Align.CENTER);
        headerTextPaint.setTextSize(textSize);
        headerTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        headerTextHeight = rect.height();
        headerTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Prepare header background paint.
        headerBackgroundPaint = new Paint();
        headerBackgroundPaint.setColor(headerRowBackgroundColor);

        // Prepare day background color paint.
        dayBackgroundPaint = new Paint();
        dayBackgroundPaint.setColor(dayBackgroundColor);

        // Prepare hour separator color paint.
        hourSeparatorPaint = new Paint();
        hourSeparatorPaint.setStyle(Paint.Style.STROKE);
        hourSeparatorPaint.setStrokeWidth(hourSeparatorHeight);
        hourSeparatorPaint.setColor(hourSeparatorColor);

        // Prepare today background color paint.
        todayBackgroundPaint = new Paint();
        todayBackgroundPaint.setColor(todayBackgroundColor);

        // Prepare today header text color paint.
        todayHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        todayHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        todayHeaderTextPaint.setTextSize(textSize);
        todayHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        todayHeaderTextPaint.setColor(todayHeaderTextColor);

        // Prepare event background color.
        eventBackgroundPaint = new Paint();
        eventBackgroundPaint.setColor(Color.rgb(174, 208, 238));

        // Prepare header column background color.
        headerColumnBackgroundPaint = new Paint();
        headerColumnBackgroundPaint.setColor(headerColumnBackgroundColor);

        // Prepare event text size and color.
        eventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        eventTextPaint.setStyle(Paint.Style.FILL);
        eventTextPaint.setColor(eventTextColor);
        eventTextPaint.setTextSize(eventTextSize);
        startDate = (Calendar) today.clone();

        // Set default event color.
        defaultEventColor = Color.parseColor("#9fc6e7");
    }
    
    ////////////////////////////////////////////////////////////
    //
    //	Drawing & style functions
    //
    ////////////////////////////////////////////////////////////
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the header row.
        drawHeaderRowAndEvents(canvas);

        // Draw the time column and all the axes/separators.
        drawTimeColumnAndAxes(canvas);

        // Hide everything in the first cell (top left corner).
        canvas.drawRect(0, 0, timeTextWidth + headerColumnPadding * 2, headerTextHeight + headerRowPadding * 2, headerBackgroundPaint);

        // Hide anything that is in the bottom margin of the header row.
        canvas.drawRect(headerColumnWidth, headerTextHeight + headerRowPadding * 2, getWidth(), headerRowPadding * 2 + headerTextHeight + headerMarginBottom + timeTextHeight/2 - hourSeparatorHeight / 2, headerColumnBackgroundPaint);
    }
    
    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Do not let the view go above/below the limit due to scrolling. Set the max and min limit of the scroll.
        if (currentScrollDirection == Direction.VERTICAL) {
            if (currentOrigin.y - distanceY > 0) currentOrigin.y = 0;
            else if (currentOrigin.y - distanceY < -(hourHeight * 24 + headerTextHeight + headerRowPadding * 2 - getHeight())) currentOrigin.y = -(hourHeight * 24 + headerTextHeight + headerRowPadding * 2 - getHeight());
            else currentOrigin.y -= distanceY;
        }

        // Draw the background color for the header column.
        canvas.drawRect(0, headerTextHeight + headerRowPadding * 2, headerColumnWidth, getHeight(), headerColumnBackgroundPaint);

        for (int i = 0; i < 24; i++) {
            float top = headerTextHeight + headerRowPadding * 2 + currentOrigin.y + hourHeight * i + headerMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            if (top < getHeight()) canvas.drawText(getTimeString(i), timeTextWidth + headerColumnPadding, top + timeTextHeight, timeTextPaint);
        }
    }
    
    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        headerColumnWidth = timeTextWidth + headerColumnPadding *2;
        widthPerDay = getWidth() - headerColumnWidth - columnGap * (numberOfVisibleDays - 1);
        widthPerDay = widthPerDay/numberOfVisibleDays;

        // If the week view is being drawn for the first time, then consider the first day of week.
        if (isFirstDraw && numberOfVisibleDays >= 7) {
            if (today.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
                int difference = 7 + (today.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek);
                currentOrigin.x += (widthPerDay + columnGap) * difference;
            }
            isFirstDraw = false;
        }

        // Consider scroll offset.
        if (currentScrollDirection.isHorizontal()) currentOrigin.x -= distanceX;
        int leftDaysWithGaps = (int) -(Math.ceil(currentOrigin.x / (widthPerDay + columnGap)));
        float startFromPixel = currentOrigin.x + (widthPerDay + columnGap) * leftDaysWithGaps +
                headerColumnWidth;
        float startPixel = startFromPixel;

        // Prepare to iterate for each day.
        Calendar day = (Calendar) today.clone();
        day.add(Calendar.HOUR, 6);

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (int) ((getHeight() - headerTextHeight - headerRowPadding * 2 -
                headerMarginBottom) / hourHeight) + 1;
        lineCount = (lineCount) * (numberOfVisibleDays+1);
        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for events rectangles.
        if (eventRects != null) {
            for (EventRect eventRect: eventRects) {
                eventRect.rectF = null;
            }
        }

        // Iterate through each day.
        firstVisibleDay = (Calendar) today.clone();
        firstVisibleDay.add(Calendar.DATE, leftDaysWithGaps);
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + numberOfVisibleDays + 1;
             dayNumber++) {

            // Check if the day is today.
            day = (Calendar) today.clone();
            lastVisibleDay = (Calendar) day.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            lastVisibleDay.add(Calendar.DATE, dayNumber - 2);
            boolean sameDay = isSameDay(day, today);

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
            if (eventRects == null || refreshEvents || (dayNumber == leftDaysWithGaps + 1 && fetchedMonths[1] != day.get(Calendar.MONTH)+1 && day.get(Calendar.DAY_OF_MONTH) == 15)) {
                getMoreEvents(day);
                refreshEvents = false;
            }

            // Draw background color for each day.
            float start =  (startPixel < headerColumnWidth ? headerColumnWidth : startPixel);
            if (widthPerDay + startPixel - start> 0)
                canvas.drawRect(start, headerTextHeight + headerRowPadding * 2 + timeTextHeight/2 + headerMarginBottom, startPixel + widthPerDay, getHeight(), sameDay ? todayBackgroundPaint : dayBackgroundPaint);

            // Prepare the separator lines for hours.
            int i = 0;
            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
                float top = headerTextHeight + headerRowPadding * 2 + currentOrigin.y + hourHeight * hourNumber + timeTextHeight/2 + headerMarginBottom;
                if (top > headerTextHeight + headerRowPadding * 2 + timeTextHeight/2 + headerMarginBottom - hourSeparatorHeight && top < getHeight() && startPixel + widthPerDay - start > 0){
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = startPixel + widthPerDay;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, hourSeparatorPaint);

            // Draw the events.
            drawEvents(day, startPixel, canvas);

            // In the next iteration, start from the next day.
            startPixel += widthPerDay + columnGap;
        }

        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), headerTextHeight + headerRowPadding * 2, headerBackgroundPaint);

        // Draw the header row texts.
        startPixel = startFromPixel;
        for (int dayNumber=leftDaysWithGaps+1; dayNumber <= leftDaysWithGaps + numberOfVisibleDays + 1; dayNumber++) {
            // Check if the day is today.
            day = (Calendar) today.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            boolean sameDay = isSameDay(day, today);

            // Draw the day labels.
            String dayLabel = String.format("%s %d/%02d", getDayName(day), day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH));
            canvas.drawText(dayLabel, startPixel + widthPerDay / 2, headerTextHeight + headerRowPadding, sameDay ? todayHeaderTextPaint : headerTextPaint);
            startPixel += widthPerDay + columnGap;
        }

    }
    
    /**
     * Draw all the events of a particular day.
     * @param date The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas The canvas to draw upon.
     */
    private void drawEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (eventRects != null && eventRects.size() > 0) {
            for (int i = 0; i < eventRects.size(); i++) {
                if (isSameDay(CalendarUtils.timeToCalendar(eventRects.get(i).event.getStartingTime()), date)) {

                    // Calculate top.
                    float top = hourHeight * 24 * eventRects.get(i).top / 1440 + currentOrigin.y + headerTextHeight + headerRowPadding * 2 + headerMarginBottom + timeTextHeight/2 + eventMarginVertical;
                    float originalTop = top;
                    if (top < headerTextHeight + headerRowPadding * 2 + headerMarginBottom + timeTextHeight/2)
                        top = headerTextHeight + headerRowPadding * 2 + headerMarginBottom + timeTextHeight/2;

                    // Calculate bottom.
                    float bottom = eventRects.get(i).bottom;
                    bottom = hourHeight * 24 * bottom / 1440 + currentOrigin.y + headerTextHeight + headerRowPadding * 2 + headerMarginBottom + timeTextHeight/2 - eventMarginVertical;

                    // Calculate left and right.
                    float left = startFromPixel + eventRects.get(i).left * widthPerDay;
                    if (left < startFromPixel)
                        left += overlappingEventGap;
                    float originalLeft = left;
                    float right = left + eventRects.get(i).width * widthPerDay;
                    if (right < startFromPixel + widthPerDay)
                        right -= overlappingEventGap;
                    if (left < headerColumnWidth) left = headerColumnWidth;

                    // Draw the event and the event name on top of it.
                    RectF eventRectF = new RectF(left, top, right, bottom);
                    if (bottom > headerTextHeight + headerRowPadding * 2 + headerMarginBottom + timeTextHeight/2 && left < right &&
                            eventRectF.right > headerColumnWidth &&
                            eventRectF.left < getWidth() &&
                            eventRectF.bottom > headerTextHeight + headerRowPadding * 2 + timeTextHeight / 2 + headerMarginBottom &&
                            eventRectF.top < getHeight() &&
                            left < right
                            ) {
                        eventRects.get(i).rectF = eventRectF;
                        eventBackgroundPaint.setColor(eventRects.get(i).event.getEvent().getColor() == 0 ? defaultEventColor : eventRects.get(i).event.getEvent().getColor());
                        canvas.drawRect(eventRects.get(i).rectF, eventBackgroundPaint);
                        drawText(eventRects.get(i).event.getEvent().getName(), eventRects.get(i).rectF, canvas, originalTop, originalLeft);
                    }
                    else
                        eventRects.get(i).rectF = null;
                }
            }
        }
    }
    
    /**
     * Draw the name of the event on top of the event rectangle.
     * @param text The text to draw.
     * @param rect The rectangle on which the text is to be drawn.
     * @param canvas The canvas to draw upon.
     * @param originalTop The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    private void drawText(String text, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - eventPadding * 2 < 0) return;

        // Get text dimensions
        StaticLayout textLayout = new StaticLayout(text, eventTextPaint, (int) (rect.right - originalLeft - eventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Crop height
        int availableHeight = (int) (rect.bottom - originalTop - eventPadding * 2);
        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();
        if (lineHeight < availableHeight && textLayout.getHeight() > rect.height() - eventPadding * 2) {
            int lineCount = textLayout.getLineCount();
            int availableLineCount = (int) Math.floor(lineCount * availableHeight / textLayout.getHeight());
            float widthAvailable = (rect.right - originalLeft - eventPadding * 2) * availableLineCount;
            textLayout = new StaticLayout(TextUtils.ellipsize(text, eventTextPaint, widthAvailable, TextUtils.TruncateAt.END), eventTextPaint, (int) (rect.right - originalLeft - eventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        else if (lineHeight >= availableHeight) {
            int width = (int) (rect.right - originalLeft - eventPadding * 2);
            textLayout = new StaticLayout(TextUtils.ellipsize(text, eventTextPaint, width, TextUtils.TruncateAt.END), eventTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        }

        // Draw text
        canvas.save();
        canvas.translate(originalLeft + eventPadding, originalTop + eventPadding);
        textLayout.draw(canvas);
        canvas.restore();
    }
    
    
    ////////////////////////////////////////////////////////////
    //
    // Event Rect
    //
    ////////////////////////////////////////////////////////////
    
    
    
    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private class EventRect {
        public EventInstanceModel event;
        public EventInstanceModel originalEvent;
        public RectF rectF;
        public float left;
        public float width;
        public float top;
        public float bottom;

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         * @param event Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF The rectangle.
         */
        public EventRect(EventInstanceModel event, EventInstanceModel originalEvent, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
            this.originalEvent = originalEvent;
        }
    }
    
    /**
     * Gets more events of one/more month(s) if necessary. This method is called when the user is
     * scrolling the week view. The week view stores the events of three months: the visible month,
     * the previous month, the next month.
     * @param day The day where the user is currently is.
     */
    private void getMoreEvents(Calendar day) {

        // Delete all events if its not current month +- 1.
        deleteFarMonths(day);

        // Get more events if the month is changed.
        if (eventRects == null)
            eventRects = new ArrayList<EventRect>();
        if (monthChangeListener == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.
        if (refreshEvents) {
            eventRects.clear();
            fetchedMonths = new int[3];
        }

        // Get events of previous month.
        int previousMonth = (day.get(Calendar.MONTH) == 0?12:day.get(Calendar.MONTH));
        int nextMonth = (day.get(Calendar.MONTH)+2 == 13 ?1:day.get(Calendar.MONTH)+2);
        int[] lastFetchedMonth = fetchedMonths.clone();
        if (fetchedMonths[0] < 1 || fetchedMonths[0] != previousMonth || refreshEvents) {
            if (!containsValue(lastFetchedMonth, previousMonth) && !isInEditMode()){
                List<EventInstanceModel> events = monthChangeListener.onMonthChange((previousMonth==12)?day.get(Calendar.YEAR)-1:day.get(Calendar.YEAR), previousMonth);
                sortEvents(events);
                for (EventInstanceModel event: events) {
                    cacheEvent(event);
                }
            }
            fetchedMonths[0] = previousMonth;
        }

        // Get events of this month.
        if (fetchedMonths[1] < 1 || fetchedMonths[1] != day.get(Calendar.MONTH)+1 || refreshEvents) {
            if (!containsValue(lastFetchedMonth, day.get(Calendar.MONTH)+1) && !isInEditMode()) {
                List<EventInstanceModel> events = monthChangeListener.onMonthChange(day.get(Calendar.YEAR), day.get(Calendar.MONTH) + 1);
                sortEvents(events);
                for (EventInstanceModel event : events) {
                    cacheEvent(event);
                }
            }
            fetchedMonths[1] = day.get(Calendar.MONTH)+1;
        }

        // Get events of next month.
        if (fetchedMonths[2] < 1 || fetchedMonths[2] != nextMonth || refreshEvents) {
            if (!containsValue(lastFetchedMonth, nextMonth) && !isInEditMode()) {
                List<EventInstanceModel> events = monthChangeListener.onMonthChange(nextMonth == 1 ? day.get(Calendar.YEAR) + 1 : day.get(Calendar.YEAR), nextMonth);
                sortEvents(events);
                for (EventInstanceModel event : events) {
                    cacheEvent(event);
                }
            }
            fetchedMonths[2] = nextMonth;
        }

        // Prepare to calculate positions of each events.
        ArrayList<EventRect> tempEvents = new ArrayList<EventRect>(eventRects);
        eventRects = new ArrayList<EventRect>();
        Calendar dayCounter = (Calendar) day.clone();
        dayCounter.add(Calendar.MONTH, -1);
        dayCounter.set(Calendar.DAY_OF_MONTH, 1);
        Calendar maxDay = (Calendar) day.clone();
        maxDay.add(Calendar.MONTH, 1);
        maxDay.set(Calendar.DAY_OF_MONTH, maxDay.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Iterate through each day to calculate the position of the events.
        while (dayCounter.getTimeInMillis() <= maxDay.getTimeInMillis()) {
            ArrayList<EventRect> eventRects = new ArrayList<EventRect>();
            for (EventRect eventRect : tempEvents) {
                if (isSameDay(CalendarUtils.timeToCalendar(eventRect.event.getStartingTime()), dayCounter))
                    eventRects.add(eventRect);
            }

            computePositionOfEvents(eventRects);
            dayCounter.add(Calendar.DATE, 1);
        }
    }
    
    /**
     * store the event in the list of events to be shown
     * @param event
     */
    
    private void cacheEvent(EventInstanceModel event) {
        if (!isSameDay(CalendarUtils.timeToCalendar(event.getStartingTime()), CalendarUtils.timeToCalendar(event.getEndingTime()))) {
        	//TODO: If working, remove me.
//            Calendar endTime = (Calendar) CalendarUtils.timeToCalendar(event.getStartingTime()).clone();
//            endTime.set(Calendar.HOUR_OF_DAY, 23);
//            endTime.set(Calendar.MINUTE, 59);
        	Time endTime = new Time(event.getStartingTime());
        	endTime.hour = 23;
        	endTime.minute=59;
        	endTime.second=59;
//            Calendar startTime = (Calendar) CalendarUtils.timeToCalendar(event.getEndingTime()).clone();
//            startTime.set(Calendar.HOUR_OF_DAY, 00);
//            startTime.set(Calendar.MINUTE, 0);
        	Time startTime = new Time(event.getEndingTime());
        	startTime.hour = 0;
        	startTime.minute=0;
        	startTime.second=0;
            EventInstanceModel event1 = new EventInstanceModel(event.getEvent(), event.getStartingTime(), endTime);
            EventInstanceModel event2 = new EventInstanceModel(event.getEvent(), startTime, event.getEndingTime());
            eventRects.add(new EventRect(event1, event, null));
            eventRects.add(new EventRect(event2, event, null));
        }
        else
            eventRects.add(new EventRect(event, event, null));
    }
    
    /**
     * Sorts the events in ascending order.
     * @param events The events to be sorted.
     */
    private void sortEvents(List<EventInstanceModel> events) {
        Collections.sort(events, new Comparator<EventInstanceModel>() {
            @Override
            public int compare(EventInstanceModel event1, EventInstanceModel event2) {
                long start1 = CalendarUtils.timeToCalendar(event1.getStartingTime()).getTimeInMillis();
                long start2 = CalendarUtils.timeToCalendar(event2.getStartingTime()).getTimeInMillis();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
                if (comparator == 0) {
                    long end1 = CalendarUtils.timeToCalendar(event1.getEndingTime()).getTimeInMillis();
                    long end2 = CalendarUtils.timeToCalendar(event2.getEndingTime()).getTimeInMillis();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }
                return comparator;
            }
        });
    }
    
    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     * @param eventRects The events along with their wrapper class.
     */
    private void computePositionOfEvents(List<EventRect> eventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<EventRect>> collisionGroups = new ArrayList<List<EventRect>>();
        for (EventRect eventRect : eventRects) {
            boolean isPlaced = false;
            outerLoop:
            for (List<EventRect> collisionGroup : collisionGroups) {
                for (EventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, eventRect.event)) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }
            if (!isPlaced) {
                List<EventRect> newGroup = new ArrayList<EventRect>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<EventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }
    
    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {
        // Expand the events to maximum possible width.
        List<List<EventRect>> columns = new ArrayList<List<EventRect>>();
        columns.add(new ArrayList<EventRect>());
        for (EventRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<EventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                }
                else if (!isEventsCollide(eventRect.event, column.get(column.size()-1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<EventRect> newColumn = new ArrayList<EventRect>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        int maxRowCount = columns.get(0).size();
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<EventRect> column : columns) {
                if (column.size() >= i+1) {
                    EventRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    eventRect.top = CalendarUtils.timeToCalendar(eventRect.event.getStartingTime()).get(Calendar.HOUR_OF_DAY) * 60 + CalendarUtils.timeToCalendar(eventRect.event.getStartingTime()).get(Calendar.MINUTE);
                    eventRect.bottom = CalendarUtils.timeToCalendar(eventRect.event.getEndingTime()).get(Calendar.HOUR_OF_DAY) * 60 + CalendarUtils.timeToCalendar(eventRect.event.getEndingTime()).get(Calendar.MINUTE);
                    eventRects.add(eventRect);
                }
                j++;
            }
        }
    }

    /**
     * Checks if two events overlap.
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private boolean isEventsCollide(EventInstanceModel event1, EventInstanceModel event2) {
        long start1 = CalendarUtils.timeToCalendar(event1.getStartingTime()).getTimeInMillis();
        long end1 = CalendarUtils.timeToCalendar(event1.getEndingTime()).getTimeInMillis();
        long start2 = CalendarUtils.timeToCalendar(event2.getStartingTime()).getTimeInMillis();
        long end2 = CalendarUtils.timeToCalendar(event2.getEndingTime()).getTimeInMillis();
        return !((start1 >= end2) || (end1 <= start2));
    }
    
    /**
     * Checks if time1 occurs after (or at the same time) time2.
     * @param time1 The time to check.
     * @param time2 The time to check against.
     * @return true if time1 and time2 are equal or if time1 is after time2. Otherwise false.
     */
    private boolean isTimeAfterOrEquals(Calendar time1, Calendar time2) {
        return !(time1 == null || time2 == null) && time1.getTimeInMillis() >= time2.getTimeInMillis();
    }
    
    /**
     * Deletes the events of the months that are too far away from the current month.
     * @param currentDay The current day.
     */
    private void deleteFarMonths(Calendar currentDay) {

        if (eventRects == null) return;

        Calendar nextMonth = (Calendar) currentDay.clone();
        nextMonth.add(Calendar.MONTH, 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, nextMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        nextMonth.set(Calendar.HOUR_OF_DAY, 12);
        nextMonth.set(Calendar.MINUTE, 59);
        nextMonth.set(Calendar.SECOND, 59);

        Calendar prevMonth = (Calendar) currentDay.clone();
        prevMonth.add(Calendar.MONTH, -1);
        prevMonth.set(Calendar.DAY_OF_MONTH, 1);
        prevMonth.set(Calendar.HOUR_OF_DAY, 0);
        prevMonth.set(Calendar.MINUTE, 0);
        prevMonth.set(Calendar.SECOND, 0);

        List<EventRect> newEvents = new ArrayList<EventRect>();
        for (EventRect eventRect : eventRects) {
            boolean isFarMonth = CalendarUtils.timeToCalendar(eventRect.event.getStartingTime()).getTimeInMillis() > nextMonth.getTimeInMillis() || CalendarUtils.timeToCalendar(eventRect.event.getEndingTime()).getTimeInMillis() < prevMonth.getTimeInMillis();
            if (!isFarMonth) newEvents.add(eventRect);
        }
        eventRects.clear();
        eventRects.addAll(newEvents);
    }
    
    ////////////////////////////////////////////////////////////
    //
    // Setters and getters
    //
    ////////////////////////////////////////////////////////////
    
    public void setOnEventClickListener(EventClickListener listener){
    	this.eventClickListener = listener;
    }
    
    public EventClickListener getEventClickListener() {
        return eventClickListener;
    }

    public MonthChangeListener getMonthChangeListener() {
        return monthChangeListener;
    }

    public void setMonthChangeListener(MonthChangeListener monthChangeListener) {
        this.monthChangeListener = monthChangeListener;
    }

    public EventLongPressListener getEventLongPressListener() {
        return eventLongPressListener;
    }

    public void setEventLongPressListener(EventLongPressListener eventLongPressListener) {
        this.eventLongPressListener = eventLongPressListener;
    }
    
    /**
     * Get the number of visible days in a week.
     * @return The number of visible days in a week.
     */
    public int getNumberOfVisibleDays() {
        return numberOfVisibleDays;
    }
    
    /**
     * Set the number of visible days in a week.
     * @param numberOfVisibleDays The number of visible days in a week.
     */
    public void setNumberOfVisibleDays(int numberOfVisibleDays) {
        this.numberOfVisibleDays = numberOfVisibleDays;
        currentOrigin.x = 0;
        currentOrigin.y = 0;
        invalidate();
    }
    
    public int getHourHeight() {
        return hourHeight;
    }

    public void setHourHeight(int hourHeight) {
        this.hourHeight = hourHeight;
        invalidate();
    }

    public int getColumnGap() {
        return columnGap;
    }

    public void setColumnGap(int columnGap) {
        this.columnGap = columnGap;
        invalidate();
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }
    
    /**
     * Set the first day of the week. First day of the week is used only when the week view is first
     * drawn. It does not of any effect after user starts scrolling horizontally.
     * <p>
     *     <b>Note:</b> This method will only work if the week view is set to display more than 6 days at
     *     once.
     * </p>
     * @param firstDayOfWeek The supported values are {@link java.util.Calendar#SUNDAY},
     * {@link java.util.Calendar#MONDAY}, {@link java.util.Calendar#TUESDAY},
     * {@link java.util.Calendar#WEDNESDAY}, {@link java.util.Calendar#THURSDAY},
     * {@link java.util.Calendar#FRIDAY}.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        invalidate();
    }
    
    /**
     * this getter is fundamental in order to obtain the list of event to be displayed in the view inside EventListFragment
     * @return
     */
    public List<EventRect> getEventRects() {
		return eventRects;
	}

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        todayHeaderTextPaint.setTextSize(textSize);
        headerTextPaint.setTextSize(textSize);
        timeTextPaint.setTextSize(textSize);
        invalidate();
    }

    public int getHeaderColumnPadding() {
        return headerColumnPadding;
    }

    public void setHeaderColumnPadding(int headerColumnPadding) {
    	this.headerColumnPadding = headerColumnPadding;
        invalidate();
    }

    public int getHeaderColumnTextColor() {
        return headerColumnTextColor;
    }

    public void setHeaderColumnTextColor(int headerColumnTextColor) {
    	this.headerColumnTextColor = headerColumnTextColor;
        invalidate();
    }

    public int getHeaderRowPadding() {
        return headerRowPadding;
    }

    public void setHeaderRowPadding(int headerRowPadding) {
    	this.headerRowPadding = headerRowPadding;
        invalidate();
    }

    public int getHeaderRowBackgroundColor() {
        return headerRowBackgroundColor;
    }

    public void setHeaderRowBackgroundColor(int headerRowBackgroundColor) {
    	this.headerRowBackgroundColor = headerRowBackgroundColor;
        invalidate();
    }

    public int getDayBackgroundColor() {
        return dayBackgroundColor;
    }

    public void setDayBackgroundColor(int dayBackgroundColor) {
    	this.dayBackgroundColor = dayBackgroundColor;
        invalidate();
    }

    public int getHourSeparatorColor() {
        return hourSeparatorColor;
    }

    public void setHourSeparatorColor(int hourSeparatorColor) {
    	this.hourSeparatorColor = hourSeparatorColor;
        invalidate();
    }

    public int getTodayBackgroundColor() {
        return todayBackgroundColor;
    }

    public void setTodayBackgroundColor(int todayBackgroundColor) {
    	this.todayBackgroundColor = todayBackgroundColor;
        invalidate();
    }

    public int getHourSeparatorHeight() {
        return hourSeparatorHeight;
    }

    public void setHourSeparatorHeight(int hourSeparatorHeight) {
    	this.hourSeparatorHeight = hourSeparatorHeight;
        invalidate();
    }

    public int getTodayHeaderTextColor() {
        return todayHeaderTextColor;
    }

    public void setTodayHeaderTextColor(int todayHeaderTextColor) {
    	this.todayHeaderTextColor = todayHeaderTextColor;
        invalidate();
    }

    public int getEventTextSize() {
        return eventTextSize;
    }

    public void setEventTextSize(int eventTextSize) {
    	this.eventTextSize = eventTextSize;
        eventTextPaint.setTextSize(eventTextSize);
        invalidate();
    }

    public int getEventTextColor() {
        return eventTextColor;
    }

    public void setEventTextColor(int eventTextColor) {
    	this.eventTextColor = eventTextColor;
        invalidate();
    }

    public int getEventPadding() {
        return eventPadding;
    }

    public void setEventPadding(int eventPadding) {
        this.eventPadding = eventPadding;
        invalidate();
    }

    public int getHeaderColumnBackgroundColor() {
        return headerColumnBackgroundColor;
    }

    public void setHeaderColumnBackgroundColor(int headerColumnBackgroundColor) {
    	this.headerColumnBackgroundColor = headerColumnBackgroundColor;
        invalidate();
    }

    public int getDefaultEventColor() {
        return defaultEventColor;
    }

    public void setDefaultEventColor(int defaultEventColor) {
    	this.defaultEventColor = defaultEventColor;
        invalidate();
    }

    public int getDayNameLength() {
        return dayNameLength;
    }
    
    /**
     * Set the length of the day name displayed in the header row. Example of short day names is
     * 'M' for 'Monday' and example of long day names is 'Mon' for 'Monday'.
     * @param length Supported values are {@link com.alamkanak.weekview.WeekView#LENGTH_SHORT} and
     * {@link com.alamkanak.weekview.WeekView#LENGTH_LONG}.
     */
    public void setDayNameLength(int length) {
        if (length != LENGTH_LONG && length != LENGTH_SHORT) {
            throw new IllegalArgumentException("length parameter must be either LENGTH_LONG or LENGTH_SHORT");
        }
        this.dayNameLength = length;
    }

    public int getOverlappingEventGap() {
        return overlappingEventGap;
    }

    /**
     * Set the gap between overlapping events.
     * @param overlappingEventGap The gap between overlapping events.
     */
    public void setOverlappingEventGap(int overlappingEventGap) {
        this.overlappingEventGap = overlappingEventGap;
        invalidate();
    }

    public int getEventMarginVertical() {
        return eventMarginVertical;
    }

    /**
     * Set the top and bottom margin of the event. The event will release this margin from the top
     * and bottom edge. This margin is useful for differentiation consecutive events.
     * @param eventMarginVertical The top and bottom margin.
     */
    public void setEventMarginVertical(int eventMarginVertical) {
        this.eventMarginVertical = eventMarginVertical;
        invalidate();
    }

    /**
     * Returns the first visible day in the week view.
     * @return The first visible day in the week view.
     */
    public Calendar getFirstVisibleDay() {
        return firstVisibleDay;
    }

    /**
     * Returns the last visible day in the week view.
     * @return The last visible day in the week view.
     */
    public Calendar getLastVisibleDay() {
        return lastVisibleDay;
    }
    
    ////////////////////////////////////////////////////////////
    //
    //      Functions related to scrolling.
    //
    ////////////////////////////////////////////////////////////
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentScrollDirection.isHorizontal()) {
                float leftDays = Math.round(currentOrigin.x / (widthPerDay + columnGap));
                int nearestOrigin = (int) (currentOrigin.x - leftDays * (widthPerDay+columnGap));
                stickyScroller.startScroll((int) currentOrigin.x, 0, - nearestOrigin, 0);
                ViewCompat.postInvalidateOnAnimation(WeekView.this);
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (Math.abs(scroller.getFinalX() - scroller.getCurrX()) < widthPerDay + columnGap && Math.abs(scroller.getFinalX() - scroller.getStartX()) != 0) {
                scroller.forceFinished(true);
                float leftDays = Math.round(currentOrigin.x / (widthPerDay + columnGap));
                if(currentScrollDirection == Direction.LEFT)
                    leftDays--;
                else
                    leftDays++;
                int nearestOrigin = (int) ((currentOrigin.x - leftDays * (widthPerDay+columnGap)));
                stickyScroller.startScroll((int) currentOrigin.x, 0, - nearestOrigin, 0);
                ViewCompat.postInvalidateOnAnimation(WeekView.this);
            }
            else {
                if (currentFlingDirection == Direction.VERTICAL) currentOrigin.y = scroller.getCurrY();
                else currentOrigin.x = scroller.getCurrX();
                ViewCompat.postInvalidateOnAnimation(this);
            }
            currentScrollDirection = Direction.NONE;
        }
        if (stickyScroller.computeScrollOffset()) {
            currentOrigin.x = stickyScroller.getCurrX();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    
    ////////////////////////////////////////////////////////////
    //
    // Public methods
    //
    ////////////////////////////////////////////////////////////
    
    /**
     * Show today on the week view.
     */
    public void goToToday() {
        Calendar today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        goToDate(today);
    }

    /**
     * Show a specific day on the week view.
     * @param date The date to show.
     */
    public void goToDate(Calendar date) {
        scroller.forceFinished(true);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);

        refreshEvents = true;

        Calendar today = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        int dateDifference = (int) (date.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        currentOrigin.x = - dateDifference * (widthPerDay + columnGap);

        invalidate();
    }

    /**
     * Refreshes the view and loads the events again.
     */
    public void notifyDatasetChanged(){
        refreshEvents = true;
        invalidate();
    }

    /**
     * Vertically scroll to a specific hour in the week view.
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    public void goToHour(double hour){
        if (hour < 0)
            throw new IllegalArgumentException("Cannot scroll to an hour of negative value.");
        else if (hour > 24)
            throw new IllegalArgumentException("Cannot scroll to an hour of value greater than 24.");
        else if (hour * hourHeight > hourHeight * 24 - getHeight() + headerTextHeight + headerRowPadding * 2 + headerMarginBottom)
            throw new IllegalArgumentException("Cannot scroll to an hour which will result the calendar to go off the screen.");

        int verticalOffset = (int) (hourHeight * hour);
        currentOrigin.y = -verticalOffset;
        invalidate();
    }
    
    ////////////////////////////////////////////////////////////
    //
    // Interfaces
    //
    ////////////////////////////////////////////////////////////
    
    public interface EventClickListener {
        public void onEventClick(EventInstanceModel event, RectF eventRect);
    }

    public interface MonthChangeListener {
        public List<EventInstanceModel> onMonthChange(int newYear, int newMonth);
    }

    public interface EventLongPressListener {
        public void onEventLongPress(EventInstanceModel event, RectF eventRect);
    }
    
    ////////////////////////////////////////////////////////////
    //
    // Helper methods.
    //
    ////////////////////////////////////////////////////////////
    
    private boolean containsValue(int[] list, int value) {
        for (int i = 0; i < list.length; i++){
            if (list[i] == value)
                return true;
        }
        return false;
    }


    /**
     * Possibility to onvert an int (0-23) to time string (e.g. 12 PM).
     * @param hour The time. Limit: 0-23.
     * @return The string representation of the time.
     */
    private String getTimeString(int hour) {
        String amPm = ".00";
        //if (hour >= 0 && hour < 12) amPm = "AM";
        //else amPm = "PM";
        //if (hour == 0) hour = 12;
        //if (hour > 12) hour -= 12;
        return String.format("%02d%s", hour, amPm);
    }


    /**
     * Checks if two times are on the same day.
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    private boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }


    /**
     * Get the day name of a given date.
     * @param date The date.
     * @return The first the characters of the day name.
     */
    private String getDayName(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        if (Calendar.MONDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "M" : "MON");
        else if (Calendar.TUESDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "T" : "TUE");
        else if (Calendar.WEDNESDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "W" : "WED");
        else if (Calendar.THURSDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "T" : "THU");
        else if (Calendar.FRIDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "F" : "FRI");
        else if (Calendar.SATURDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "S" : "SAT");
        else if (Calendar.SUNDAY == dayOfWeek) return (dayNameLength == LENGTH_SHORT ? "S" : "SUN");
        return "";
    }
    
}
