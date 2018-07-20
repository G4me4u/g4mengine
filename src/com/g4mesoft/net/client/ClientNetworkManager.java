package com.g4mesoft.net.client;

import java.net.SocketAddress;
import java.net.SocketException;

import com.g4mesoft.Application;
import com.g4mesoft.net.IPacketRegistry;
import com.g4mesoft.net.NetworkException;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;

public class ClientNetworkManager extends NetworkManager {

	private SocketAddress serverAddress;
	
	public ClientNetworkManager(Application app, IPacketRegistry registry) {
		super(app, registry, NetworkSide.CLIENT);

		try {
			socket.bind(null);
		} catch (SocketException e) {
			throw new NetworkException("Unable to bind socket", e);
		}
	}
	
	public boolean connect(SocketAddress serverAddress) {
		try {
			socket.connect(serverAddress);
		} catch (SocketException e) {
			throw new NetworkException("Unable to connect to remote server", e);
		}
		
		this.serverAddress = serverAddress;
		
		return true;
	}
	
	protected boolean validateReceivedPacket(Packet packet) {
		if (!socket.isConnected() || serverAddress == null)
			return false;
		return serverAddress.equals(packet.senderAddress);
	}
}
