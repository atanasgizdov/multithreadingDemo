package com.course.example.receiverdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

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
		Thread thread1 = new Thread(background);
		Thread thread2 = new Thread(background);
		thread1.start();
		thread2.start();
		return Service.START_STICKY;


	}// onStart

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(tag, "Service destroyed");
		isRunning = false;
	}// onDestroy

	//background thread
	Thread background = new Thread (new Runnable () {

		public void run() {
			do {
				try {
					Thread.sleep(1000);
					// fake that you are very busy here
					int random = randomNumber();
					Intent myObserverSender = new Intent(
							"Bentley.action.GOSERVICE");
					//string for log
					String msg = "Found Number " + random + " on thread named:" + Thread.currentThread().getName() + "";
					//array to send
					ArrayList<String> msgToSend = new ArrayList<String>();
					msgToSend.add(Thread.currentThread().getName());
					msgToSend.add(random + "");

;					myObserverSender.putExtra("randomNumber", msgToSend);
					//write to log
					Log.e (tag, msg + " -receiving data "
					);
					//send broadcast
					sendBroadcast(myObserverSender);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (isRunning);

		}// run
	});



	//method to generate a random 4-digit number
	public int randomNumber (){
		Random random = new Random();
		int n = random.nextInt(9999)+ 1;
		return n;
	}
}