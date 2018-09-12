package com.g4mesoft.net.server;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.UUID;

import com.g4mesoft.Application;
import com.g4mesoft.net.IPacketRegistry;
import com.g4mesoft.net.NetworkConnection;
import com.g4mesoft.net.NetworkException;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.standard.C00ClientIdentification;
import com.g4mesoft.net.packet.standard.S00ServerIdentification;

public class ServerNetworkManager extends NetworkManager {

	public ServerNetworkManager(Application app, IPacketRegistry registry, InetSocketAddress address) {
		super(app, registry, NetworkSide.SERVER);

		try {
			socket.bind(address);
		} catch (SocketException e) {
			throw new NetworkException("Unable to bind socket: " + address, e);
		}
		
		startThreads();
	}

	@Override
	protected boolean validateReceivedPacket(Packet packet) {
		// Test if we're dealing with
		// a valid, connected client.
		if (packet.senderUUID != null && !NO_UUID.equals(packet.senderUUID)) {
			NetworkConnection connection = networkConnections.get(packet.senderUUID);
			if (connection == null)
				return false;
			return connection.getAddress().equals(packet.senderAddress);
		}
		
		// In case of the client sending
		// a C00ClientIdentification packet,
		// we have to let the packet pass
		// validation.
		return packet instanceof C00ClientIdentification;
	}
	
	public boolean connectClient(C00ClientIdentification clientIdentification) {
		if (!clientIdentification.received)
			return false;

		// We may want to ban some
		// addresses from connecting.
		// If that is to be the case
		// it can be implemented using
		// the validateClientAddress
		// function.
		if (!validateClientAddress(clientIdentification.senderAddress))
			return false;
		
		// Check protocol compatibility
		if (!isProtocolCompatible(clientIdentification.protocolVersion))
			return false;
		
		// Check if client is already connected
		if (isClientConnected(clientIdentification.senderAddress))
			return false;
		
		// Generate a random, currently 
		// unused UUID. Even though the 
		// UUID is guaranteed to be unique
		// we still test if it is already
		// part of the network and generate
		// a new one if it is.
		UUID clientUUID;
		do {
			clientUUID = UUID.randomUUID();
		} while (networkConnections.containsKey(clientUUID));
		
		addNetworkConnection(clientUUID, clientIdentification.senderAddress);
		Packet pIdentification = new S00ServerIdentification(getProtocolVersion(), clientUUID);
		addPacketToSend(pIdentification, clientUUID, STANDARD_PACKET_FLAG);
		
		return true;
	}
	
	private boolean isClientConnected(InetSocketAddress addr) {
		return getNetworkConnection(addr) != null;
	}

	protected boolean validateClientAddress(InetSocketAddress addr) {
		return addr != null;
	}
}
