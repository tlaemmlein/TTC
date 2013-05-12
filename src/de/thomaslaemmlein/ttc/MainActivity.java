package de.thomaslaemmlein.ttc;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;

public class MainActivity extends SherlockActivity implements INumberReceiver {
	
	private boolean m_bBluetoothConnectionStateIcon;
	
    private CounterView m_ScorePlayerLeft;
    private CounterView m_SetPointsPlayerLeft;
    private CounterView m_SetPointsPlayerRight;
    private CounterView m_ScorePlayerRight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#262626")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>TTC</font>"));
		
		float textSizeScoreScalefactor = 1.0f/4.65f; //Text size 200sp Looks good for 800 pixel width. Hence -> 200/800 = 1/4
		float textSizeSetPointsScalefactor = textSizeScoreScalefactor/2.0f;
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();  // deprecated
		float textSizeScoreView = width * textSizeScoreScalefactor;
		float textSizeSetPointsView = width * textSizeSetPointsScalefactor;

		m_ScorePlayerLeft = (CounterView) findViewById(R.id.scorePlayerLeft);
		m_ScorePlayerLeft.SetNumberReceiver(this);
		m_ScorePlayerLeft.setIntegerDigitsNumber(2);
		m_ScorePlayerLeft.SetNumber(0);
		m_ScorePlayerLeft.setTextSize(textSizeScoreView);
		
		m_SetPointsPlayerLeft = (CounterView) findViewById(R.id.setPointsPlayerLeft);
		m_SetPointsPlayerLeft.SetNumberReceiver(this);
		m_SetPointsPlayerLeft.SetNumber(0);
		m_SetPointsPlayerLeft.setTextSize(textSizeSetPointsView);

		m_SetPointsPlayerRight = (CounterView) findViewById(R.id.setPointsPlayerRight);
		m_SetPointsPlayerRight.SetNumberReceiver(this);
		m_SetPointsPlayerRight.SetNumber(0);
		m_SetPointsPlayerRight.setTextSize(textSizeSetPointsView);

		m_ScorePlayerRight = (CounterView) findViewById(R.id.scorePlayerRight);
		m_ScorePlayerRight.SetNumberReceiver(this);
		m_ScorePlayerRight.setIntegerDigitsNumber(2);
		m_ScorePlayerRight.SetNumber(0);
		m_ScorePlayerRight.setTextSize(textSizeScoreView);
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

	@Override
	public void SetNumber(int newNumber) {
		// TODO Auto-generated method stub
		
	}    

}
