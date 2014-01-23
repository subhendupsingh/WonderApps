package com.techiela.flyingdeals;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	  private ResponseReceiver receiver;
	  

	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar cal = Calendar.getInstance();

		Intent intent = new Intent(this, MessageService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent); 
		setContentView(R.layout.activity_main);
		//startService(new Intent(getApplicationContext(), MessageService.class)); 
		IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
	}

	// Start the  service
	public void startNewService(View view) {

		startService(new Intent(this, MessageService.class));
	}

	// Stop the  service
	public void stopNewService(View view) {

		stopService(new Intent(this, MessageService.class));
		
	}
	
	@Override
	protected void onStop()
	{
	    unregisterReceiver(receiver);
	    
	    super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}