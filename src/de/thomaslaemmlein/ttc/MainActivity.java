package de.thomaslaemmlein.ttc;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.thomaslaemmlein.ttc.bluetooth.BluetoothManager;
import de.thomaslaemmlein.ttc.bluetooth.BluetoothService;
import de.thomaslaemmlein.ttc.bluetooth.DeviceListActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SherlockActivity implements INumberReceiver {
	
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

	private boolean m_bBluetoothConnectionStateIcon;
	private BluetoothManager m_BluetoothManager;
    private MenuItem m_BluetoothMenuItem;
    private BluetoothService m_BluetoothService;
	
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
	private enum ScoreView { RefereeView, PlayerView }
	
	private ScoreView m_ScoreView;
	
    private CounterView m_ScorePlayerLeft;
    public static final int m_ScorePlayerLeftID = 1;

    private CounterView m_SetPointsPlayerLeft;
    public static final int m_SetPointsPlayerLeftID = 2;

    private TextView m_LeftPlayerTextView;

    private CounterView m_SetPointsPlayerRight;
    public static final int m_SetPointsPlayerRightID = 3;
    
    private CounterView m_ScorePlayerRight;
    public static final int m_ScorePlayerRightID = 4;

    private TextView m_RightPlayerTextView;
    
    private TextView m_ConnectionText;
    
    // Name of the connected device
    private String m_ConnectedDeviceName = null;
    
    private Menu m_OptionMenu;
    
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(D) Log.e(TAG, "- ON CREATE -");
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#262626")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>TTC</font>"));
		
		float textSizeScoreScalefactor = 1.0f/5.0f; //Text size 200sp Looks good for 800 pixel width. Hence -> 200/800 = 1/4
		float textSizeSetPointsScalefactor = textSizeScoreScalefactor/2.0f;
		float textSizePlayerNameScalefactor = textSizeScoreScalefactor/10.5f;
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();  // deprecated
		float textSizeScoreView = width * textSizeScoreScalefactor;
		float textSizeSetPointsView = width * textSizeSetPointsScalefactor;
		float textSizePlayerName = width * textSizePlayerNameScalefactor;

		m_ScorePlayerLeft = (CounterView) findViewById(R.id.scorePlayerLeft);
		m_ScorePlayerLeft.SetNumberReceiver(this);
		m_ScorePlayerLeft.setIntegerDigitsNumber(2);
		m_ScorePlayerLeft.SetNumber(0);
		m_ScorePlayerLeft.setTextSize(textSizeScoreView);
		
		m_SetPointsPlayerLeft = (CounterView) findViewById(R.id.setPointsPlayerLeft);
		m_SetPointsPlayerLeft.SetNumberReceiver(this);
		m_SetPointsPlayerLeft.SetNumber(0);
		m_SetPointsPlayerLeft.setTextSize(textSizeSetPointsView);
		
		m_LeftPlayerTextView = (TextView) findViewById(R.id.LeftPlayer_textView);
		m_LeftPlayerTextView.setText(R.string.playerA);
		m_LeftPlayerTextView.setTextSize(textSizePlayerName);

		m_SetPointsPlayerRight = (CounterView) findViewById(R.id.setPointsPlayerRight);
		m_SetPointsPlayerRight.SetNumberReceiver(this);
		m_SetPointsPlayerRight.SetNumber(0);
		m_SetPointsPlayerRight.setTextSize(textSizeSetPointsView);

		m_ScorePlayerRight = (CounterView) findViewById(R.id.scorePlayerRight);
		m_ScorePlayerRight.SetNumberReceiver(this);
		m_ScorePlayerRight.setIntegerDigitsNumber(2);
		m_ScorePlayerRight.SetNumber(0);
		m_ScorePlayerRight.setTextSize(textSizeScoreView);
		
		m_RightPlayerTextView = (TextView) findViewById(R.id.RightPlayer_textView);
		m_RightPlayerTextView.setText(R.string.playerB);
		m_RightPlayerTextView.setTextSize(textSizePlayerName);
		
		m_ConnectionText = (TextView) findViewById(R.id.Connection_textView);
		
		m_BluetoothManager = new BluetoothManager();
	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(D) Log.e(TAG, "- ON START -");
        // If BT is not on, request that it be enabled.
        // onActivityResult will be called
        if (!m_BluetoothManager.isBluetoothEnabled()) 
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        
        }
        // Otherwise, setup the table tennis session
        else
        {
            if (m_BluetoothService == null) 
            {
            		setupService();
            }
        }
	}
	
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (m_BluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (m_BluetoothService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
            	m_BluetoothService.start();
            }
        }
    }	
	
	
	private void setupService()
	{
		if(D) Log.d(TAG, "- setupService -");
        // Initialize the BluetoothService to perform bluetooth connections
		m_BluetoothService = new BluetoothService(this, mHandler);
		m_BluetoothService.start();
	}
	
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (m_BluetoothService != null) m_BluetoothService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }	
    
    private void ensureDiscoverable() {
        if (m_BluetoothManager.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (m_BluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            m_BluetoothService.write(send);
        }
    }   
    
	
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	m_ConnectionText.setText(R.string.title_connected_to);
                    if ( m_ConnectedDeviceName != null )
                    {
                    	m_ConnectionText.append(m_ConnectedDeviceName);
                    }
                    break;
                case BluetoothService.STATE_CONNECTING:
                	m_ConnectionText.setText(R.string.title_connecting);
                    break;
                case BluetoothService.STATE_LISTEN:
                	m_ConnectionText.setText(R.string.title_listen);
                    break;
                case BluetoothService.STATE_NONE:
                	m_ConnectionText.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case BluetoothService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                break;
            case BluetoothService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                
            	//m_CurrentNumberEditText.setText(readMessage);
            	
            	//int number = Integer.parseInt(readMessage);
            	
            	//m_CounterView.SetNumber(number);
                
                break;
            case BluetoothService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
            	m_ConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + m_ConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothService.TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }; 	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(D) Log.e(TAG, "- ON CREATE OPTION MENU -");
    	com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
 	   	inflater.inflate(R.menu.main, (com.actionbarsherlock.view.Menu) menu);   
 	   	
 	   	m_OptionMenu = menu;
 	
 	   	m_bBluetoothConnectionStateIcon = false;
 	   	
 	    m_ScoreView = ScoreView.RefereeView;

 	   	return super.onCreateOptionsMenu(menu);
	}
    
    private void setBluetoothIconState(boolean IsConnected)
    {
    	if ( IsConnected)
    	{
    		m_BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
    		
            if ( null == m_BluetoothService )
            {
            	setupService();
            }    		
    		
    		if(D) Log.d(TAG, "- setBluetoothIconState connected -");
    	}
    	else
    	{
    		m_BluetoothMenuItem.setIcon(R.drawable.bluetooth_not_connected);
    	}
    	m_bBluetoothConnectionStateIcon = IsConnected;
		m_OptionMenu.getItem(0).setVisible(IsConnected);
		m_OptionMenu.getItem(1).setVisible(IsConnected);
		m_OptionMenu.getItem(2).setVisible(IsConnected);
    	
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.bluetooth_connection_button:
            	m_BluetoothMenuItem = item;
            	
            	if ( m_bBluetoothConnectionStateIcon )
            	{
            		setBluetoothIconState(false);
            	}
            	else
            	{
            		if ( !m_BluetoothManager.getAdapter() )
            		{
                        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            		}
            		else
            		{
            			if ( m_BluetoothManager.isBluetoothEnabled() )
            			{
            				setBluetoothIconState(true);
            			}
            			else
            			{
            	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            			}
            		}
            	}
            	return true;
            	
            case R.id.score_view:
            	if ( m_ScoreView == ScoreView.RefereeView )
            	{
            		item.setIcon(R.drawable.player_view);
            		item.setTitle(R.string.player_view);
            		m_ScoreView = ScoreView.PlayerView;
            		m_LeftPlayerTextView.setText(R.string.playerB);
            		m_RightPlayerTextView.setText(R.string.playerA);
            	}
            	else
            	{
            		item.setIcon(R.drawable.referee_view);
            		item.setTitle(R.string.referee_view);
            		m_ScoreView = ScoreView.RefereeView;
            		m_LeftPlayerTextView.setText(R.string.playerA);
            		m_RightPlayerTextView.setText(R.string.playerB);
            	}
            	int temp = m_ScorePlayerLeft.getNumber();
            	m_ScorePlayerLeft.SetNumber(m_ScorePlayerRight.getNumber());
            	m_ScorePlayerRight.SetNumber(temp);
            	
            	temp = m_SetPointsPlayerLeft.getNumber();
            	m_SetPointsPlayerLeft.SetNumber(m_SetPointsPlayerRight.getNumber());
            	m_SetPointsPlayerRight.SetNumber(temp);
            	
            	return true;

            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            	
            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
            
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;        
        case REQUEST_ENABLE_BT:
        	
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a table tennis session
                Toast.makeText(this, "Bluetooth is now enabled, so set up the table tennis session", Toast.LENGTH_LONG).show();
                setBluetoothIconState(true);
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, "User did not enable Bluetooth or an error occured", Toast.LENGTH_LONG).show();
                setBluetoothIconState(false);
            }
        }
    }    
    
    private void connectDevice(Intent data, boolean secure) {
    	if(D) 
		{
			Log.d(TAG, "--- connectDevice called ---");
		}
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = m_BluetoothManager.getRemoteDevice(address);
        // Attempt to connect to the device
        Toast.makeText(this, "Attempt to connect to the device", Toast.LENGTH_SHORT).show();
        m_BluetoothService.connect(device, secure);
    }
    

	@Override
	public void SetNumber(int newNumber, int id) {
		switch (id) {
		case m_ScorePlayerLeftID:
			
			break;

		case m_SetPointsPlayerLeftID:
			
			break;

		case m_SetPointsPlayerRightID:
			
			break;

		case m_ScorePlayerRightID:
			
			break;

		default:
			break;
		}
		
		if(D) 
		{
			Log.d(TAG, "--- SetNumber called ---");
		}
		
	}    

}
