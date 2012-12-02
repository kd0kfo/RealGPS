package com.davecoss.android.RealGPS;

import com.davecoss.android.lib.Notifier;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class SMSLocation extends Activity {

	private static final int PICK_CONTACT_REQUEST = 1;
	private Notifier notifier;
	private String contact_number;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smslocation);
        notifier = new Notifier(getApplicationContext());
        contact_number = "";

        // Get the message from the intent
        Intent intent = getIntent();
        String gps_data = intent.getStringExtra(RealGPSMain.SEND_SMS);
        
        // Set message field
        TextView txt_data = (TextView) findViewById(R.id.txt_sms_message);
        txt_data.setText(gps_data);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_smslocation, menu);
        return true;
    }
    
    public void selectContact(View view) {
    	Uri contact_uri = Contacts.CONTENT_URI;
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, contact_uri);
        pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        switch(requestCode)
        {
        case PICK_CONTACT_REQUEST:
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {Phone.NUMBER};

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(column);
                TextView editText = (TextView) findViewById(R.id.txt_contact_name);
            	editText.setText(number);
            	contact_number = number;
            	 
            }
            break; 
        default:
        	break;
        }
    }
    
    public void sendSMS(View view){
    	
    	if(contact_number.length() == 0)
    	{
    		notifier.toast_message("Recipient required to send SMS.");
    		return;
    	}
    	
    	SmsManager sm = SmsManager.getDefault();
    	// here is where the destination of the text should go
    	TextView txt_sms_msg = (TextView) findViewById(R.id.txt_sms_message);
    	String msg = txt_sms_msg.getText().toString();
    	if(msg.length() == 0)
    	{
    		notifier.toast_message("Will not send bad GPS data.");
    		return;
    	}
    	
    	sm.sendTextMessage(contact_number, null, msg, null, null);
    	notifier.toast_message("GPS data sent.");
    }
}
