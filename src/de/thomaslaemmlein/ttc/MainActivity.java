package de.thomaslaemmlein.ttc;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.thomaslaemmlein.ttc.bluetooth.BluetoothManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SherlockActivity implements INumberReceiver {
	
	private boolean m_bBluetoothConnectionStateIcon;
	private BluetoothManager m_BluetoothManager;
    private MenuItem m_BluetoothMenuItem;
	
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
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#262626")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>TTC</font>"));
		
		float textSizeScoreScalefactor = 1.0f/4.65f; //Text size 200sp Looks good for 800 pixel width. Hence -> 200/800 = 1/4
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
		
		m_BluetoothManager = new BluetoothManager();
	
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
 	   	inflater.inflate(R.menu.main, (com.actionbarsherlock.view.Menu) menu);    	
 	
 	   	m_bBluetoothConnectionStateIcon = false;
 	   	
 	    m_ScoreView = ScoreView.RefereeView;

 	   	return super.onCreateOptionsMenu(menu);
	}
    
    private void setBluetoothIconState(boolean connected)
    {
    	if ( connected)
    	{
    		m_BluetoothMenuItem.setIcon(R.drawable.bluetooth_connected);
    		m_bBluetoothConnectionStateIcon = true;
    	}
    	else
    	{
    		m_BluetoothMenuItem.setIcon(R.drawable.bluetooth_not_connected);
    		m_bBluetoothConnectionStateIcon = false;
    	}
    	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
        	
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                Toast.makeText(this, "Bluetooth is now enabled, so set up the table tennis session", Toast.LENGTH_SHORT).show();
                setBluetoothIconState(true);
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, "User did not enable Bluetooth or an error occured", Toast.LENGTH_SHORT).show();
                setBluetoothIconState(false);
            }
        }
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
		
		Toast.makeText(this, "SetNumber called", Toast.LENGTH_LONG).show();
		
	}    

}
