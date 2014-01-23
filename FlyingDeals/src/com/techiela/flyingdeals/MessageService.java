package com.techiela.flyingdeals;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MessageService extends Service {
public static final String BROADCAST_ACTION = "Hello World";
private static final int TWO_MINUTES = 1000 * 60 * 2;
public static final String PARAM_OUT_MSG = null;
public LocationManager locationManager;
public MyLocationListener listener;
public Location previousBestLocation = null;
private Timer timer = new Timer();
String coordinates, latitude, longitude;
Double lat, lng,distance;
Intent intent = new Intent();
int counter = 0,updateInterval=1000*10;

private View mView;
private WindowManager.LayoutParams params;
private WindowManager mWindowManager;
private ImageView chatHead;




@Override
public void onCreate() {
    super.onCreate();
    intent = new Intent(BROADCAST_ACTION); 
   /* mView = new MyLoadView(this);
    mParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, 150, 10, 10,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);

    mParams.gravity = Gravity.CENTER;*/
    mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    chatHead = new ImageView(this);
    chatHead.setImageResource(R.drawable.ic_launcher);

        params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);

    params.gravity = Gravity.TOP | Gravity.LEFT;
    params.x = 0;
    params.y = 100;

    mWindowManager.addView(chatHead, params);
    
    chatHead.setOnTouchListener(new View.OnTouchListener() {
    	  private int initialX;
    	  private int initialY;
    	  private float initialTouchX;
    	  private float initialTouchY;

    	  @Override public boolean onTouch(View v, MotionEvent event) {
    	    switch (event.getAction()) {
    	      case MotionEvent.ACTION_DOWN:
    	        initialX = params.x;
    	        initialY = params.y;
    	        initialTouchX = event.getRawX();
    	        initialTouchY = event.getRawY();
    	        return true;
    	      case MotionEvent.ACTION_UP:
    	        return true;
    	      case MotionEvent.ACTION_MOVE:
    	        params.x = initialX + (int) (event.getRawX() - initialTouchX);
    	        params.y = initialY + (int) (event.getRawY() - initialTouchY);
    	        mWindowManager.updateViewLayout(chatHead, params);
    	        return true;
    	    }
    	    return false;
    	  }
    	});
    
}

@Override
public int onStartCommand(Intent intent, int flags, int startId) {             
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    listener = new MyLocationListener();
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, listener);
    return START_STICKY;
}



@Override
public IBinder onBind(Intent intent) {
    return null;
}

protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
        // A new location is always better than no location
        return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
        return true;
    // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
        return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
            currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
        return true;
    } else if (isNewer && !isLessAccurate) {
        return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
        return true;
    }
    return false;
}

/** Checks whether two providers are the same */
private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
}

@Override
public void onDestroy() {       
   // handler.removeCallbacks(sendUpdatesToUI);     
    super.onDestroy();
    Log.v("STOP_SERVICE", "DONE");
    locationManager.removeUpdates(listener);  
    if (chatHead != null) mWindowManager.removeView(chatHead);
}   

public static Thread performOnBackgroundThread(final Runnable runnable) {
    final Thread t = new Thread() {
        @Override
        public void run() {
            try {
                runnable.run();
            } finally {

            }
        }
    };
    t.start();
    return t;
}



public class MyLocationListener implements LocationListener
{

    public void onLocationChanged(final Location loc)
    {
        Log.i("**************************************", "Location changed");
        if(isBetterLocation(loc, previousBestLocation)) {
        	lat = loc.getLatitude();
            lng = loc.getLongitude();
            latitude = Double.toString(lat);
            longitude = Double.toString(lng); 
            coordinates = "Coordinates: " + latitude + ", " + longitude; 
            distance= distance(lat, lng, 28.632196, 77.367169, 'K');
            Log.v("service status","running");
            if(distance<0.6){
                Log.v("Distance of Shipra","Distance is less than 800 Meters");
                intent.setAction(ResponseReceiver.ACTION_RESP);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(PARAM_OUT_MSG, "Time To Display Deals!!!");                
                sendBroadcast(intent); 
                try{
                   // mParams.setTitle("Window test");
                   // mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
                   // mWindowManager.addView(mView, mParams);
                    }catch(Exception e){
                    	   
                    	  
                    	 }
                }
        	else{
            	Log.v("Distance of Shipra","Distance is more than 800 Meters, no deals found");
            	intent.setAction(ResponseReceiver.ACTION_RESP);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra(PARAM_OUT_MSG, "You are heading towards deals!!!");                
                sendBroadcast(intent); 
                //if (mView.getVisibility() == View.VISIBLE) {
                //	mView.setVisibility(View.GONE);
                //} 
                
        	}

            }  
    }
    
    
    	
    public void onProviderDisabled(String provider)
    {
        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
    }


    public void onProviderEnabled(String provider)
    {
        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
    }


    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

}

private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {

	  double theta = lon1 - lon2;

	  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

	  dist = Math.acos(dist);

	  dist = rad2deg(dist);

	  dist = dist * 60 * 1.1515;

	  if (unit == 'K') {

	    dist = dist * 1.609344;

	  } else if (unit == 'N') {

	    dist = dist * 0.8684;

	    }

	  return (dist);

	}

private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);

	}

private double rad2deg(double rad) {

	  return (rad * 180 / Math.PI);

	}

public class MyLoadView extends View {

    private Paint mPaint;

    public MyLoadView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setTextSize(50);
        mPaint.setARGB(200, 200, 200, 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("100% OFF ON PIZZA In DOminos!!", 0, 100, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

}