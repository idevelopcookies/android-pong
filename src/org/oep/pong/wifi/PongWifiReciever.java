//package org.oep.pong.wifi;
//
//import java.util.Collection;
//import java.util.logging.Logger;
//
//import org.oep.pong.Pong;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pDeviceList;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.net.wifi.p2p.WifiP2pManager.Channel;
//import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
//
//public class PongWifiReciever extends BroadcastReceiver {
//	private WifiP2pManager manager;
//	private Channel channel;
//	private Pong activity;
//	private Collection<WifiP2pDevice> availableDevices;
//	
//	private PeerListListener myPeerListListener = new PeerListListener() {
//		
//		public void onPeersAvailable(WifiP2pDeviceList peers) {
//			availableDevices = peers.getDeviceList();			
//		}
//	};
//	
//	public PongWifiReciever(WifiP2pManager manager, Channel channel, Pong pong) {
//		this.manager  = manager;
//		this.channel  = channel;
//		this.activity = pong;
//		
//	
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		String action = intent.getAction();
//
//		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
//			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//				// Wifi Direct is enabled
//			} else {
//				// Wi-Fi Direct is not enabled
//			}
//		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
//			 // request available peers from the wifi p2p manager. This is an
//		    // asynchronous call and the calling activity is notified with a
//		    // callback on PeerListListener.onPeersAvailable()
//		    if (manager != null) {
//		        manager.requestPeers(channel, myPeerListListener);
//		    }
//		    Logger.getAnonymousLogger(myPeerListListener.toString());
//		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
//				.equals(action)) {
//			// Respond to new connection or disconnections
//		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
//				.equals(action)) {
//			// Respond to this device's wifi state changing
//		}
//
//	}
//
//}
