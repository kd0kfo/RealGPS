package com.davecoss.android.RealGPS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.davecoss.android.RealGPS.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.location.*;
import android.content.Context;

public class RealGPSMain extends Activity {
	private LocationManager locationManager;
	LocationListener mlocListener;
	double lat;
	double lon;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        lat = 1;
        lon = 2;

        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new DRCLocationListener();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        
//        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if(!gpsEnabled)
//        {
//        	enableLocationSettings();
//        }
//        else
//        {
//	        LocationProvider provider =
//	                locationManager.getProvider(LocationManager.GPS_PROVIDER);
//        }
        setContentView(R.layout.activity_main);
    }

    public void save_location(View view)
    {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	
    	if(mExternalStorageAvailable && mExternalStorageWriteable)
    	{
    		File dir = getExternalFilesDir(null);
    		if(!dir.exists())
    		{
    			if(!dir.mkdirs())
    			{
    				write_message("Could not make directory.");
    				return;
    			}
    		}
    		File file = new File(dir, "gps_log.txt");
    		try {
				OutputStream os = new FileOutputStream(file,true);
    	        os.write((lat + ", " + lon+"\n").getBytes());
    	        os.close();
			
    	    } catch (IOException e) {
    	        write_message("ExternalStorage: Error writing " + file.getName() + "\n" + e.getMessage());
    	    }
    	}
    	else
    	{
    		write_message("Cannot write file.");
    	}
    }
    
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(mlocListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void enableLocationSettings() {
    	TextView location_text = (TextView) findViewById(R.id.location_text);
    	location_text.setText("Please enable GPS");
        //Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(settingsIntent);
    }
    
    private void write_message(String msg)
    {
    	TextView text = (TextView) findViewById(R.id.location_text);
        text.setText(msg);
    }
    
    public class DRCLocationListener implements LocationListener
    {
    @Override
    public void onLocationChanged(Location loc)
    {

    lat = loc.getLatitude();

    lon = loc.getLongitude();

    String bean = "My current location is: Lat = " + lat + "Lon = " + lon;

    TextView text = (TextView) findViewById(R.id.location_text);
    text.setText(bean);

    }

    @Override
    public void onProviderDisabled(String provider)
    {
    	enableLocationSettings();
    }

    @Override

    public void onProviderEnabled(String provider)
    {
    	TextView text = (TextView) findViewById(R.id.location_text);
        text.setText("GPS Enabled");
    }

    @Override

    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    }/* End of Class MyLocationListener */

}

