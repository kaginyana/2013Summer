package me.xiangchen.app.usbstreaming;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	final String TAG = "USB Streaming";
	UsbManager usbManager;
	UsbAccessory[] accessories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//		accessories = usbManager.getAccessoryList();
//		
//		if(accessories == null) {
//			Log.e(TAG, "couldn't get accessory list");
//		}
//		else {
//			for (UsbAccessory accessory : accessories) {
//				Log.d(TAG, accessory.getSerial());
//			}
//		}
		
		Intent intent = getIntent();
		UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if(device == null) {
			Log.e(TAG, "couldn't get device");
		} else {
			Log.d(TAG, device.getDeviceName());
		}
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
