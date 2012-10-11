package com.davecoss.android.RealGPS;

import com.davecoss.android.RealGPS.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.location.*;
import android.content.Context;

public class RealGPSMain extends Activity {
	private LocationManager locationManager;
	LocationListener mlocListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

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
    
    public class DRCLocationListener implements LocationListener
    {

    @Override
    public void onLocationChanged(Location loc)
    {

    loc.getLatitude();

    loc.getLongitude();

    String bean = "My current location is: Lat = " + loc.getLatitude() + "Lon = " + loc.getLongitude();

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

