package org.euss.multi;

import java.util.List;

import android.app.Activity;

public interface IPongP2PAdapter{

	public boolean initializeAdapter();
	
	public List<String> getAvailablePeers();
	
	public boolean connectToPeer(String peerName);
	
	public boolean initializeGame();
	
	public boolean syncGameState();
	
}
