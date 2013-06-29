package de.thomaslaemmlein.ttc.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BluetoothManager {

    // Local Bluetooth adapter
    private BluetoothAdapter m_BluetoothAdapter = null;
    
    
    //Get the default Bluetooth adapter.
    public boolean getAdapter()
    {
    	// Get local Bluetooth adapter
    	m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	if (m_BluetoothAdapter == null)
    	{
    		return false;
    	}
    	
    	return true;
    }
    
    // Check if Bluetooth is enabled. 
    public boolean isBluetoothEnabled()
    {
    	if (m_BluetoothAdapter == null)
    	{
    		return false;
    	}

    	return m_BluetoothAdapter.isEnabled();
    }
    
    public int getScanMode()
    {
    	return m_BluetoothAdapter.getScanMode();
    }

    public BluetoothDevice getRemoteDevice(String address)
    {
    	return m_BluetoothAdapter.getRemoteDevice(address);
    }
}
