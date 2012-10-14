package com.davecoss.android.RealGPS;

import com.davecoss.android.lib.Notifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import com.davecoss.android.RealGPS.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.location.*;
import android.content.Context;
import android.content.pm.PackageManager;

public class RealGPSMain extends Activity {
	private static final String TAG = "RealGPSMain";
	private LocationManager locationManager;
	LocationListener mlocListener;
	Notifier notifier;
	String last_loc;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        last_loc = "";

        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new DRCLocationListener();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        
        setContentView(R.layout.activity_main);
        
        notifier = new Notifier(getApplicationContext());
        
        try
        {
        	TextView version = (TextView) findViewById(R.id.txt_version);
            String app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            version.setText(app_ver);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e(TAG, e.getMessage());
        }

        
    }

    public void save_location(View view)
    {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	
    	if(last_loc == "")
    	{
    		notifier.toast_message(getString(R.string.no_position_info));
    	}
    	
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
    				notifier.toast_message("Could not make directory.");
    				return;
    			}
    		}
    		File file = new File(dir, "gps_log.txt");
    		try {
				OutputStream os = new FileOutputStream(file,true);
				EditText edit_tag = (EditText) findViewById(R.id.edit_tag);
				String tag = edit_tag.getText().toString().trim();
				os.write((tag+":").getBytes());
				os.write(last_loc.getBytes());
    	        os.write('\n');
    	        os.close();
    	        notifier.toast_message("Coordinates saved.");
			
    	    } catch (IOException e) {
    	        notifier.toast_message("ExternalStorage: Error writing " + file.getName() + "\n" + e.getMessage());
    	    }
    	}
    	else
    	{
    		notifier.toast_message("Cannot write file.");
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
    	notifier.toast_message("Please enable GPS");
        //Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(settingsIntent);
    } 
    
    public class DRCLocationListener implements LocationListener
    {
    	private void updateTextView(TextView tv, double val)
    	{
    		DecimalFormat df = new DecimalFormat("#.######");
    		tv.setText(df.format(val));
    	}
    	
    	private void date2textview(TextView view, Date date)
    	{
    		view.setText(DateFormat.getInstance().format(date));
    		
    	}
    	
    @Override
    public void onLocationChanged(Location loc)
    {
    	TextView lat = (TextView) findViewById(R.id.txt_latitude);
        TextView lon = (TextView) findViewById(R.id.txt_longitude);
        TextView bearing = (TextView) findViewById(R.id.txt_bearing);
        TextView speed = (TextView) findViewById(R.id.txt_speed);
        TextView altitude = (TextView) findViewById(R.id.txt_altitude);
        TextView time = (TextView) findViewById(R.id.txt_time);
        Date date = new Date(loc.getTime());
        
        updateTextView(lat,loc.getLatitude());
        updateTextView(lon,loc.getLongitude());
        updateTextView(bearing,loc.getBearing());
        updateTextView(speed,loc.getSpeed());
        updateTextView(altitude,loc.getAltitude());
        date2textview(time,date);
        
        last_loc = loc.getLatitude() + "," + loc.getLongitude() + "," + loc.getAltitude() + "," + loc.getBearing() + "," + loc.getSpeed() + "," + loc.getTime();
        
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    	enableLocationSettings();
    }

    @Override

    public void onProviderEnabled(String provider)
    {
    	notifier.toast_message("GPS Enabled");
    }

    @Override

    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    }/* End of Class MyLocationListener */

}

