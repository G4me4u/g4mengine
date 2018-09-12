package com.g4mesoft.net;

import java.net.InetSocketAddress;
import java.util.UUID;

public class NetworkConnection {

	private final UUID networkUUID;
	private final InetSocketAddress address;
	
	NetworkConnection(UUID networkUUID, InetSocketAddress address) {
		this.networkUUID = networkUUID;
		this.address = address;
	}
	
	public UUID getNetworkUUID() {
		return networkUUID;
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
}
