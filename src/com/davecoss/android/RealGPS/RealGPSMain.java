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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class RealGPSMain extends Activity implements OnItemSelectedListener {
	private static final String TAG = "RealGPSMain";
	public static final String SEND_SMS = "RealGPSMain.SEND_SMS";
	private static final int SHOW_SEND_SMS = 0;
	private static final int SHOW_COMPASS = 1;
	
	
	public enum Units {UNITS_METRIC, UNITS_IMPERIAL};
	
	private LocationManager locationManager;
	LocationListener mlocListener;
	Notifier notifier;
	String last_loc;
	Units units;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        last_loc = "";
        units = Units.UNITS_METRIC;

        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new DRCLocationListener();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        
        setContentView(R.layout.activity_main);
        
        notifier = new Notifier(getApplicationContext());

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(this);
        
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
    } 
    
    public String getUnitsString()
    {
    	switch(units)
    	{
    	case UNITS_IMPERIAL:
    		return "imperial";
    	case UNITS_METRIC:
    		return "metric";
    	default:
    		break;
    	}
    	return "UNKNOWN";
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
        double val = loc.getSpeed();
        if(units == Units.UNITS_IMPERIAL)
        {
            val *= 2.23694;
        }
        else
        {
        	val *= 3.6;
        }
        updateTextView(speed,val);
        
        val = loc.getAltitude();
        if(units == Units.UNITS_IMPERIAL)
        {
        	val *= 3.28084;
        }
        updateTextView(altitude,val);
        date2textview(time,date);
        
        last_loc = loc.getLatitude() + "," + loc.getLongitude() + "," + loc.getAltitude() + "," + loc.getBearing() + "," + loc.getSpeed() + "," + loc.getTime()+","+getUnitsString();
        
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Resources res = getResources();
		String[] unit_types = res.getStringArray(R.array.units_array);
		if(unit_types[arg2].equals("Metric"))
			units = Units.UNITS_METRIC;
		if(unit_types[arg2].equals("Imperial"))
			units = Units.UNITS_IMPERIAL;
		
		TextView unit_label;
		switch(units)
		{
		case UNITS_IMPERIAL:
			unit_label = (TextView) findViewById(R.id.textView8);
			unit_label.setText("mph");
			unit_label = (TextView) findViewById(R.id.textView10);
			unit_label.setText("ft");
			break;
		case UNITS_METRIC: default:
			unit_label = (TextView) findViewById(R.id.textView8);
			unit_label.setText("km/hr");
			unit_label = (TextView) findViewById(R.id.textView10);
			unit_label.setText("m");
			break;
		}
		
		notifier.toast_message("Changed Units to " + unit_types[arg2]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_compass:
        	Intent compass_activity = new Intent(getBaseContext(), Compass.class);
        	startActivityForResult(compass_activity,SHOW_COMPASS);
        	return true;
        case R.id.menu_sms:
        	Intent sms_activity = new Intent(getBaseContext(), SMSLocation.class);
        	EditText edit_tag = (EditText) findViewById(R.id.edit_tag);
			String gps_data = edit_tag.getText().toString().trim();
			if(gps_data.length() > 0)
				gps_data += ":";
			gps_data += last_loc;
			sms_activity.putExtra(SEND_SMS, gps_data);
        	startActivityForResult(sms_activity,SHOW_SEND_SMS);
        	return true;
        case R.id.menu_version:
        	String app_ver;
			try {
				app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				app_ver = "Version " + app_ver;
	        } catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				app_ver = "Error getting version";
			}
			notifier.toast_message(app_ver);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
	
}

