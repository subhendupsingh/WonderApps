package com.techiela.flyingdeals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ResponseReceiver extends BroadcastReceiver {
	   public static final String ACTION_RESP =
	      "com.techiela.intent.action.MESSAGE_PROCESSED";
	   @Override
	    public void onReceive(Context context, Intent intent) {
	       
	       String text = intent.getStringExtra(MessageService.PARAM_OUT_MSG);
	       Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	       
	    }
	}
