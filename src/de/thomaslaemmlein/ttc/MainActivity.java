package de.thomaslaemmlein.ttc;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;

public class MainActivity extends SherlockActivity {
	
	private boolean m_bBluetoothConnectionStateIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#262626")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>TTC</font>"));
	
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
 	   	inflater.inflate(R.menu.main, (com.actionbarsherlock.view.Menu) menu);    	
 	
 	   	m_bBluetoothConnectionStateIcon = false;

 	   	return super.onCreateOptionsMenu(menu);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_connection_button:
            	if ( m_bBluetoothConnectionStateIcon )
            	{
            		item.setIcon(R.drawable.bluetooth_not_connected);
            		m_bBluetoothConnectionStateIcon = false;
            	}
            	else
            	{
            		item.setIcon(R.drawable.bluetooth_connected);
            		m_bBluetoothConnectionStateIcon = true;
            	}
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }    

}
