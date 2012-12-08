package org.oep.pong;

import org.oep.pong.wifi.PongWifiReciever;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Pong extends Activity {
	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.act_title);
		setListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			Intent i = new Intent(this, PongPreferencesActivity.class);
			startActivity(i);
			break;
		}
		return false;
	}

	protected void setListeners() {
		this.findViewById(R.id.title_btnNoPlayer).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						startGame(false, false);
					}
				});

		this.findViewById(R.id.title_btnOnePlayer).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						startGame(false, true);
					}
				});

		this.findViewById(R.id.title_btnTwoPlayer).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						startGame(true, true);
					}
				});

		this.findViewById(R.id.title_btnWifiPlay).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						searchForOpponents();
					}
				});
	}

	protected void startGame(boolean redPlayer, boolean bluePlayer) {
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.EXTRA_BLUE_PLAYER, bluePlayer);
		i.putExtra(GameActivity.EXTRA_RED_PLAYER, redPlayer);
		startActivity(i);
	}

	protected void searchForOpponents() {
		initializeWifiManager();
		
		//Find other peers
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			public void onSuccess() {
				WifiP2pDevice device;
				WifiP2pConfig config = new WifiP2pConfig();
//				config.deviceAddress = device.deviceAddress;
				mManager.connect(mChannel, config, new ActionListener() {

				    public void onSuccess() {
				        //success logic
				    }

				    public void onFailure(int reason) {
				        //failure logic
				    }
				});
			}

			public void onFailure(int reasonCode) {
			}
		});
	}
	
	private void initializeWifiManager(){
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new PongWifiReciever(mManager, mChannel, this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	public static final String PREF_BALL_SPEED = "ball_speed",
			PREF_STRATEGY = "strategy", PREF_LIVES = "lives",
			PREF_HANDICAP = "handicap", PREF_MUTED = "muted";

	public static final String KEY_AI_STRATEGY = "key_ai_strategy";
}