package com.davecoss.android.RealGPS;

import java.text.DecimalFormat;

import com.davecoss.android.lib.Notifier;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class Compass extends Activity implements SensorEventListener {

	private SensorManager sensor_manager;
	private Sensor orient_sensor;
	private Notifier notifier;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        notifier = new Notifier(getApplicationContext());
        
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        orient_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    protected void onResume() {
      super.onResume();
      sensor_manager.registerListener(this, orient_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
      super.onPause();
      sensor_manager.unregisterListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_compass, menu);
        return true;
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateTextView(TextView tv, double val)
	{
		DecimalFormat df = new DecimalFormat("#.######");
		tv.setText(df.format(val));
	}

	float[] orientation = new float[3];
	int counter = 0;
	
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		synchronized(this) {
			try{

				if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION) 
                    System.arraycopy(arg0.values,0,orientation,0,3);
                
				if(counter < 8)
				{
					counter++;
					return;
				}
				counter = 0;
				updateTextView((TextView) findViewById(R.id.orientation), orientation[0]);

			}
			catch(Exception e)
			{
				notifier.toast_message(e.toString());
			}
		}
	}
}
