package com.course.example.receiverdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	boolean isRunning = true;
	String tag = "Dynamic Receiver";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(tag, "Service Created");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.e(tag, "Service Started");

		// we place the slow work of the service in its own
		// thread so the response we send our caller who ran
		// "startService(...)" gets a quick OK.
		Thread triggerService = new Thread(background);
		triggerService.start();
		return Service.START_STICKY;
	}// onStart

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(tag, "Service destroyed");
		isRunning = false;
	}// onDestroy

	//background thread
	Runnable background = new Runnable() {
		long startingTime = System.currentTimeMillis();
		long tics = 0;

		public void run() {
			for (int i = 0; (i < 120) & isRunning; i++) { 
				try {
					// fake that you are very busy here
					tics = System.currentTimeMillis() - startingTime;
					Intent myObserverSender = new Intent(
							"Bentley.action.GOSERVICE");
					String msg = i + " value: " + tics;
					myObserverSender.putExtra("serviceData", msg);
					sendBroadcast(myObserverSender);
					Thread.sleep(1000); 

				} catch (Exception e) {
					e.printStackTrace();
				}
			}// for

		}// run
	};

}