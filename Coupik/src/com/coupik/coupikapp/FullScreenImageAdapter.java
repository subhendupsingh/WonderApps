package com.coupik.coupikapp;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FullScreenImageAdapter extends Activity {

	LinearLayout asthmaActionPlan, controlledMedication, asNeededMedication,
			rescueMedication, yourSymtoms, yourTriggers, wheezeRate, peakFlow;
	LayoutParams params;
	LinearLayout next, prev;
	int viewWidth;
	GestureDetector gestureDetector = null;
	HorizontalScrollView horizontalScrollView;
	ArrayList<LinearLayout> layouts;
	int parentLeft, parentRight;
	int mWidth;
	int currPosition, prevPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fullscreen_view);
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/Antonio-Bold.ttf");
        TextView tv = (TextView) findViewById(R.id.myTitle);
        TextView tv1 = (TextView) findViewById(R.id.heading1);
        tv.setTypeface(tf);
        tv1.setTypeface(tf);
        
		prev = (LinearLayout) findViewById(R.id.prev);
		next = (LinearLayout) findViewById(R.id.next);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		asthmaActionPlan = (LinearLayout) findViewById(R.id.asthma_action_plan);
		controlledMedication = (LinearLayout) findViewById(R.id.controlled_medication);
		asNeededMedication = (LinearLayout) findViewById(R.id.as_needed_medication);
		rescueMedication = (LinearLayout) findViewById(R.id.rescue_medication);
		yourSymtoms = (LinearLayout) findViewById(R.id.your_symptoms);
		yourTriggers = (LinearLayout) findViewById(R.id.your_triggers);
		wheezeRate = (LinearLayout) findViewById(R.id.wheeze_rate);
		Display display = getWindowManager().getDefaultDisplay();
		mWidth = display.getWidth(); // deprecated
		viewWidth = mWidth;
		layouts = new ArrayList<LinearLayout>();
		params = new LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);
		asthmaActionPlan.setLayoutParams(params);
		controlledMedication.setLayoutParams(params);
		asNeededMedication.setLayoutParams(params);
		rescueMedication.setLayoutParams(params);
		yourSymtoms.setLayoutParams(params);
		yourTriggers.setLayoutParams(params);
		wheezeRate.setLayoutParams(params);
		layouts.add(asthmaActionPlan);
		layouts.add(controlledMedication);
		layouts.add(asNeededMedication);
		layouts.add(rescueMedication);
		layouts.add(yourSymtoms);
		layouts.add(yourTriggers);
		layouts.add(wheezeRate);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						horizontalScrollView.smoothScrollTo(
								(int) horizontalScrollView.getScrollX()
										+ viewWidth,
								(int) horizontalScrollView.getScrollY());
					}
				}, 100L);
			}
		});
		prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						horizontalScrollView.smoothScrollTo(
								(int) horizontalScrollView.getScrollX()
										- viewWidth,
								(int) horizontalScrollView.getScrollY());
					}
				}, 100L);
			}
		});
		horizontalScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		});
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() < e2.getX()) {
				currPosition = getVisibleViews("left");
			} else {
				currPosition = getVisibleViews("right");
			}

			horizontalScrollView.smoothScrollTo(layouts.get(currPosition)
					.getLeft(), 0);
			return true;
		}
	}

	public int getVisibleViews(String direction) {
		Rect hitRect = new Rect();
		int position = 0;
		int rightCounter = 0;
		for (int i = 0; i < layouts.size(); i++) {
			if (layouts.get(i).getLocalVisibleRect(hitRect)) {
				if (direction.equals("left")) {
					position = i;
					break;
				} else if (direction.equals("right")) {
					rightCounter++;
					position = i;
					if (rightCounter == 2)
						break;
				}
			}
		}
		return position;
	}
}
