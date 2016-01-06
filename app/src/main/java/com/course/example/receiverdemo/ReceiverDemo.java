/*
 * Start a service. Button click ends service.
 * Dynamic BroadcastReceiver is created from inner class.
 * When activity is killed, receiver is unregistered and service is ended.
 * Manifest entry for service.
 */

package com.course.example.receiverdemo;

import java.util.Date;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import java.text.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
					txtMsg.setText("Service Stopped: \n" + 
							        service.getClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}        	
        }); 
        
		// register & define filter for BroadcastReveiver
        IntentFilter mainFilter = new IntentFilter("Bentley.action.GOSERVICE");
		receiver = new MyMainLocalReceiver();
		registerReceiver(receiver, mainFilter);
		
		
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

    
	//////////////////////////////////////////////////////////////////////
	// local RECEIVER inner class
	public class MyMainLocalReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context localContext, Intent callerIntent) {
			String serviceData = callerIntent.getStringExtra("serviceData");
					
			Log.e (tag, serviceData + " -receiving data " 
					+ SystemClock.elapsedRealtime() );
			
			DateFormat df = DateFormat.getDateTimeInstance();
			String date = df.format(new Date());
			
			String now = "\n" + serviceData + " --- "  + date;			
			txtMsg.append(now);
		}		
	}//MyMainLocalReceiver

}//ReceiverDemo