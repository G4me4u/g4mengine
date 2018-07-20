package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

public class NetworkConnection {

	private final UUID networkUUID;
	private final SocketAddress address;
	
	NetworkConnection(UUID networkUUID, SocketAddress address) {
		this.networkUUID = networkUUID;
		this.address = address;
	}
	
	public UUID getNetworkUUID() {
		return networkUUID;
	}
	
	public SocketAddress getAddress() {
		return address;
	}
}
