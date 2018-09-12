package com.g4mesoft.net.client;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.UUID;

import com.g4mesoft.Application;
import com.g4mesoft.net.IPacketRegistry;
import com.g4mesoft.net.NetworkException;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.standard.C00ClientIdentification;
import com.g4mesoft.net.packet.standard.S00ServerIdentification;

public class ClientNetworkManager extends NetworkManager {

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	
	private int connectState;
	
	public ClientNetworkManager(Application app, IPacketRegistry registry) {
		super(app, registry, NetworkSide.CLIENT);

		connectState = STATE_DISCONNECTED;
		
		try {
			socket.bind(new InetSocketAddress(0));
		} catch (SocketException e) {
			throw new NetworkException("Unable to bind socket", e);
		}

		startThreads();
	}
	
	public boolean connect(InetSocketAddress serverAddress) {
		if (serverAddress == null)
			throw new IllegalArgumentException("Server address is null!");
		if (connectState != STATE_DISCONNECTED)
			throw new IllegalStateException("Already connected / connecting to a server");
		connectState = STATE_CONNECTING;
		
		setRemoteAddress(serverAddress);
		
		Packet pIdentification = new C00ClientIdentification(getProtocolVersion());
		addPacketToSend(pIdentification, null, STANDARD_PACKET_FLAG);
		
		return true;
	}
	
	@Override
	protected boolean validateReceivedPacket(Packet packet) {
		// Make sure we've actually
		// had a successful call to
		// #connect(InetSocketAddress)
		if (remoteAddress == null || connectState == STATE_DISCONNECTED)
			return false;
		
		// Make sure we're receiving
		// from the server address.
		// If it is not the server address
		// it has probably slipped through
		// the security check before connecting
		// to the remote address.
		if (!remoteAddress.equals(packet.senderAddress))
			return false;
		
		// Unless we're connected we
		// should only receive the
		// S00ServerIdentification
		// packet from the server.
		if (connectState == STATE_CONNECTED)
			return true;
		return (packet instanceof S00ServerIdentification);
	}

	public void serverIdentified(S00ServerIdentification serverIdentification) {
		// If we're currently not in 
		// the process of connecting
		// to a server the packet is
		// invalid.
		if (remoteAddress == null || connectState != STATE_CONNECTING)
			return;
		
		// Check protocol compatibility
		if (!isProtocolCompatible(serverIdentification.protocolVersion))
			return;
		
		UUID serverUUID = serverIdentification.senderUUID;
		networkUUID = serverIdentification.clientUUID;

		if (addNetworkConnection(serverUUID, remoteAddress))
			connectState = STATE_CONNECTED;
	}
	
	public InetSocketAddress getServerAddress() {
		return remoteAddress;
	}
	
	public boolean isConnected() {
		return connectState == STATE_CONNECTED;
	}
}
