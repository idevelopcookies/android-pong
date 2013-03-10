package org.euss.multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.oep.pong.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BluetoothPeerSelector extends Activity implements IPongP2PAdapter {

	public final static String HOST_TAG = "BluetoothAdapter_Host";
	public final static String CLIENT_TAG = "BluetoothAdapter_Client";
	public final static int REQUEST_ENABLE_BT = 1;
	public final static String SERVICE_NAME = "PONG_SERVICE";
	
	private boolean isHost;
	private ListView peerView;
	private List<String> mArrayAdapter;
//	private final UUID BT_PONG_UUID = UUID.randomUUID();
	 private static final UUID BT_PONG_UUID =
		        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
	private BluetoothDevice btDevice;
	
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(BT_PONG_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	        
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = mmSocket.getInputStream();
	            tmpOut = mmSocket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	        	Log.e(CLIENT_TAG, "About to connect from client");
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	            	Log.e(CLIENT_TAG, "Connection failed", connectException);
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        Log.i(CLIENT_TAG, "CONNECTED!!!");
	        // Do work to manage the connection (in a separate thread)
//	        manageConnectedSocket(mmSocket);
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	    
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	private class AcceptThread extends Thread { 
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(SERVICE_NAME, BT_PONG_UUID);
	        } catch (IOException e) { 
	        	Log.e(HOST_TAG, "Could not create BT Server Socket", e);
	        }
	        mmServerSocket = tmp;
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	                Log.e(HOST_TAG, "Open BT socket");
	                socket = mmServerSocket.accept();
	            } catch (IOException e) {
	            	Log.e(HOST_TAG, "Error opening socket", e);
	                break;
	            }
	            Log.e(HOST_TAG, "No longer accepting connections");
	            // If a connection was accepted
	            if (socket != null) {
	            	Log.e(HOST_TAG, "Server has recieved connection!");
	                // Do work to manage the connection (in a separate thread)
//	                manageConnectedSocket(socket);
	                try {
						mmServerSocket.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				btDevice =  intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				mArrayAdapter
						.add(btDevice.getAddress());
				
				for(String s : mArrayAdapter)
					Log.e(CLIENT_TAG,	s);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, mArrayAdapter);

				// Assign adapter to ListView
				peerView.setAdapter(adapter); 
			}
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mArrayAdapter = new ArrayList<String>();
		setContentView(R.layout.bt_peer_view);
		
		peerView = (ListView) findViewById(R.id.bt_peer_view);
		peerView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String address = ((TextView) arg1).getText().toString();
				Log.e(CLIENT_TAG,"MAC: " + address);
				BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
				ConnectThread ct = new ConnectThread(device);
				ct.start();
			}
		});
	
		Bundle bundle = getIntent().getExtras();
		isHost = bundle.getBoolean("isGameHost");
		
		if(isHost){
			// Make discoverable
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
			AcceptThread at = new AcceptThread();
			at.start();
		} else {
			getAvailablePeers();
		}
	}

	public List<String> getAvailablePeers() {
		// Create a BroadcastReceiver for ACTION_FOUND

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
		return mArrayAdapter;
	}

	public boolean connectToPeer(String peerName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean initializeGame() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean syncGameState() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean initializeAdapter() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(CLIENT_TAG, "Bluetooth is not supported on this device.");
			return false;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!isHost){
			unregisterReceiver(mReceiver);
		}
	}

}
