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
	private Sensor geomag_sensor,grav_sensor;
	private Notifier notifier;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        notifier = new Notifier(getApplicationContext());
        
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        geomag_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        grav_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
      super.onResume();
      sensor_manager.registerListener(this, grav_sensor, SensorManager.SENSOR_DELAY_NORMAL);
      sensor_manager.registerListener(this, geomag_sensor, SensorManager.SENSOR_DELAY_NORMAL);
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

	float[] geomagnetic;
	float[] gravity;
	
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		try{
		if(arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			gravity = arg0.values;
		if(arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			geomagnetic = arg0.values;
		if(gravity == null || geomagnetic == null)
			return;
		float Rot[] = new float[9];
		float I[] = new float[9];
		boolean success = SensorManager.getRotationMatrix(Rot, I, gravity, geomagnetic);
		if (success) {
			float orientation[] = new float[3];
			double bearing;
			SensorManager.getOrientation(Rot, orientation);
			bearing = (180.0/Math.PI)*orientation[0];
			while(bearing < 0.0)
				bearing += 360.0;
			updateTextView((TextView) findViewById(R.id.orientation), bearing);
		}
		
		}
		catch(Exception e)
		{
			notifier.toast_message(e.toString());
		}
	}
}
