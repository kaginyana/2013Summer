package me.xiangchen.app.eerv2;

import java.io.IOException;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

@SuppressLint({ "NewApi", "UseValueOf" })
public class EERv2 extends SmallApplication {
	
	/*
	 * interface dimension
	 */
//	final int width	 = 200;
//	final int height = 300;
	RelativeLayout layout;
	
	final static int MAXTOUCHPOINTS 	=2;
	final static int WATCHWIDTH 		=128;
	final static int WATCHHEIGHT 		=128;
	xacSwipe swipe;
	xacTextEntry textEntry;
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        // interface dimensions
        layout = new RelativeLayout(this);
        setContentView(layout);
        
        SmallAppWindow.Attributes attr = getWindow().getAttributes();
        attr.width = WATCHWIDTH;
        attr.height = WATCHHEIGHT;
        
        getWindow().setAttributes(attr);
        
        // swipe
        swipe = new xacSwipe();
        textEntry = new xacTextEntry();
        
        layout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getActionMasked();
				switch(action) {
					case MotionEvent.ACTION_DOWN:
						doTouchDown(event);
						break;
					case MotionEvent.ACTION_MOVE:
						doTouchMove(event);
						break;
					case MotionEvent.ACTION_UP:
					try {
						doTouchUp(event);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						break;
					default:
						break;
				}
				return true;
			}
		});
        
        
        textEntry.initVisualView(layout, this);
        textEntry.initTextView(layout, this);
	}
	
	private void doTouchDown(MotionEvent event) {
		int numTouches = event.getPointerCount();
		if(numTouches > MAXTOUCHPOINTS) {
			return;
		}
		
		for(int i=0; i<numTouches; i++) {
			String key = new Integer(event.getPointerId(i)).toString();
			PointerCoords outPointerCoords = new PointerCoords();
			event.getPointerCoords(i, outPointerCoords);
			swipe.addTouchPoint(outPointerCoords, key);
		}
	}
	
	private void doTouchMove(MotionEvent event) {
		
	}
	
	private void doTouchUp(MotionEvent event) throws CloneNotSupportedException {
		int numTouches = event.getPointerCount();
		for(int i=0; i<numTouches; i++) {
			String key = new Integer(event.getPointerId(i)).toString();
			PointerCoords outPointerCoords = new PointerCoords();
			event.getPointerCoords(i, outPointerCoords);
			swipe.endTouchPoint(outPointerCoords, key);
		}
		
		swipe.getGesture();
		textEntry.update(swipe);
		swipe.cleanTouchPoin();
	}
}
