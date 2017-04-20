/*
 * Start a service. Button click ends service.
 * Dynamic BroadcastReceiver is created from inner class.
 * When activity is killed, receiver is unregistered and service is ended.
 * Manifest entry for service.
 */

package com.course.example.receiverdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;



public class ReceiverDemo extends Activity {
    EditText txtMsg;
    Button btnStopService;
    ComponentName service;
    Intent intentMyService;
    BroadcastReceiver receiver;
    String tag = "Dynamic Receiver";

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtMsg = (EditText) findViewById(R.id.txtMsg);


        //start service
        intentMyService = new Intent(this, MyService.class);        
        service = startService(intentMyService);    
                
        txtMsg.setText("Service started - (see DDMS Log)");
        
        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					stopService(intentMyService);
					txtMsg.setText("Service Stopped: \n");
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}        	
        }); 
        
		// register & define filter for BroadcastReveiver
        IntentFilter mainFilter = new IntentFilter("Bentley.action.GOSERVICE");
		receiver = new MyMainLocalReceiver();
		registerReceiver(receiver, mainFilter);

		//start animation while searching - "loading bar"

		ImageView favicon = (ImageView) findViewById(R.id.intro_pic);
		RotateAnimation r = new RotateAnimation(0,360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		r.setDuration((long) 2*1500);
		r.setRepeatCount(5);
		favicon.startAnimation(r);
		
		
    }//onCreate

    ////////////////////////////////////////////////////////////////////////
    @Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			stopService(intentMyService);
			unregisterReceiver(receiver);
		} catch (Exception e) {
			
			Log.e (tag, e.getMessage() );
		}
		Log.e (tag , "Bye" );
	}

	// create notification
	public void sendNotification (String magic, String thread) {
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MyService.class), 0);
		Resources d = getResources();
		Notification notification = new NotificationCompat.Builder(this)
				.setTicker("Magic")
				.setSmallIcon(android.R.drawable.ic_menu_report_image)
				.setContentTitle("The magic number is: " + magic)
				.setContentText("It was found on thread: " + thread)
				.setContentIntent(pi)
				.setAutoCancel(true)
				.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);

	}



	//////////////////////////////////////////////////////////////////////
	// local RECEIVER inner class
	public class MyMainLocalReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context localContext, Intent callerIntent) {
            ArrayList<String> returnedArray = callerIntent.getStringArrayListExtra("randomNumber");

			boolean foundMagic = false;


			//parse string to int
			int randomNumber = Integer.parseInt(returnedArray.get(1));
			String thread = returnedArray.get(0);

			if (randomNumber % 7 == 0 || (randomNumber % 7 == 0 && (randomNumber % 10 == 2))){
				foundMagic = true;
			}

			//if magic number is found, end threads and print number
			if (foundMagic) {

				//stop "loading screen"
				ImageView favicon = (ImageView) findViewById(R.id.intro_pic);
				favicon.setAnimation(null);

				//notify
				txtMsg.setText(randomNumber + " is a magic number! It was found on thread: " + thread);
				//stop service
				try {
					stopService(intentMyService);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//throw up notification
				sendNotification(Integer.toString(randomNumber),thread);

				//vibrate device
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(400);
			}


		}
	}//MyMainLocalReceiver

}//ReceiverDemo

