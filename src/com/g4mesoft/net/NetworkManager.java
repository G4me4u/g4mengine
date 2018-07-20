package com.g4mesoft.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.g4mesoft.Application;
import com.g4mesoft.net.packet.Packet;

public abstract class NetworkManager implements Closeable {
	
	/**
	 * The maximum size of a single packet sent or 
	 * received by this network-manager specified
	 * in number of bytes.
	 */
	public static final int MAX_PACKET_SIZE = 1024 * 4;
	
	static final UUID NO_UUID = new UUID(0L, 0L);
	
	protected final Application app;
	private final IPacketRegistry registry;
	protected final NetworkSide side;
	
	protected final DatagramSocket socket;
	
	private final SendThread sendThread;
	private final ReceiveThread receiveThread;
	
	private final PacketByteBuffer sendBuffer;
	
	private final Map<UUID, NetworkConnection> networkConnections;
	private UUID networkUUID;
	
	private final LinkedBlockingQueue<Packet> packetsToProcess;
	
	public NetworkManager(Application app, IPacketRegistry registry, NetworkSide side) {
		this.app = app;
		this.registry = registry;
		this.side = side;
		
		try {
			socket = new DatagramSocket(null);
		} catch (SocketException e) {
			throw new NetworkException("Unable to construct a socket", e);
		}
		
		sendThread = new SendThread(this);
		sendThread.start();
		receiveThread = new ReceiveThread(this);
		receiveThread.start();
		
		sendBuffer = new PacketByteBuffer(MAX_PACKET_SIZE);

		networkConnections = new HashMap<UUID, NetworkConnection>();
		networkUUID = NO_UUID;
		
		packetsToProcess = new LinkedBlockingQueue<Packet>();
	}
	
	public void update() {
		// Process all packets. Multi-
		// threading is handled by the
		// LinkedQueue implementation.
		Packet packet;
		while ((packet = packetsToProcess.poll()) != null)
			packet.processPacket(this);
	}

	public void addPacketToSend(Packet packet) {
		addPacketToSend(packet, null);
	}

	public void addPacketToSend(Packet packet, UUID receiverUUID) {
		sendThread.addPacketToSend(packet, receiverUUID);
	}
	
	/**
	 * Called from SendThread
	 * 
	 * @param packet
	 * @param receiverUUID
	 */
	final void sendPacket(Packet packet, UUID receiverUUID) {
		// Setup buffer with 20 bytes 
		// of the packetClazzId and the 
		// networkUUID. The packet
		// registry has to be the same
		// on the remote address and the
		// local address (this instance).
		sendBuffer.clear();
		int packetClazzId = registry.getPacketClazzId(packet.getClass());
		if (packetClazzId == -1)
			throw new NetworkException("Non-registered packet clazz: " + packet.getClass());
		sendBuffer.putInt(packetClazzId);
		sendBuffer.putUUID(networkUUID);
		
		// Load data from 
		packet.write(sendBuffer);

		// Setup DatagramPacket data
		byte[] data = sendBuffer.getData();
		int len = sendBuffer.getSize();
		DatagramPacket dgp = new DatagramPacket(data, len);
		
		if (socket.isConnected() || receiverUUID != null) {
			// If we're connected to a remote
			// address, we can ignore the
			// receiverUUID.
			if (!socket.isConnected()) {
				NetworkConnection nc = getNetworkConnection(receiverUUID);
				if (nc != null)
					dgp.setSocketAddress(nc.getAddress());
			}
			
			// In both cases, when the socket 
			// is connected or when the 
			// receiverUUID is non-null, we're
			// sending to a single address.
			try {
				socket.send(dgp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		// Send to all clients, if
		// receiverUUID is null and
		// if we're not connected to
		// a remote address.
		for (NetworkConnection nc : networkConnections.values()) {
			dgp.setSocketAddress(nc.getAddress());
			
			try {
				socket.send(dgp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public NetworkConnection getNetworkConnection(UUID networkUUID) {
		return networkConnections.get(networkUUID);
	}
	
	/**
	 * Called from ReceiveThread
	 * 
	 * @param dgPacket
	 */
	final void receiveDatagramPacket(DatagramPacket dgPacket) {
		PacketByteBuffer buffer = new PacketByteBuffer(dgPacket.getData(), 
				dgPacket.getOffset(), dgPacket.getLength(), false);

		// Make sure we have enough
		// data in the buffer.
		if (buffer.remaining() < 4 + 16) // int(4) uuid(16)
			return;
		
		// Read 20 bytes of header info.
		// (Information about sender and
		// the type of packet sent).
		int packetId = buffer.getInt();
		UUID senderUUID = buffer.getUUID();

		Class<? extends Packet> packetClazz = registry.getPacketClazz(packetId);
		
		// There may be a packet-registry
		// mismatch.
		if (packetClazz == null)
			return;
		
		Packet packet;
		try {
			packet = packetClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// The packet class may be private
			// or could possibly not have an
			// empty constructor.
			throw new NetworkException("The packet class " + packetClazz.getName() +
					" may be private or does not contain an empty constructor.", e);
		}
		
		if (!packet.checkSize(buffer.remaining()))
			return;
		
		// Set packet meta-information
		packet.setSender(dgPacket.getSocketAddress(), senderUUID);
		// Decode packet data
		packet.read(buffer);
		
		if (validateReceivedPacket(packet))
			packetsToProcess.add(packet);
	}
	
	protected boolean validateReceivedPacket(Packet packet) {
		return true;
	}

	public boolean isServer() {
		return side == NetworkSide.SERVER;
	}
	
	public boolean isClient() {
		return side == NetworkSide.CLIENT;
	}
	
	public NetworkSide getSide() {
		return side;
	}
	
	public SocketAddress getAddress() {
		return socket.getLocalSocketAddress();
	}

	public UUID getConnectionUUID() {
		return networkUUID;
	}
	
	@Override
	public void close() {
		sendThread.stopSending();
		receiveThread.stopReceiving();
		socket.close();
	}
}
