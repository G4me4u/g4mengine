package com.g4mesoft.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.g4mesoft.Application;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.standard.C00ClientIdentification;
import com.g4mesoft.net.packet.standard.S00ServerIdentification;

public abstract class NetworkManager implements Closeable {
	
	/**
	 * The maximum size of a single packet sent or 
	 * received by this network-manager specified
	 * in number of bytes.
	 */
	public static final int MAX_PACKET_SIZE = 1024 * 4;
	
	/**
	 * The version of the protocol used for connection
	 * and general purpose network communication. In the
	 * case where a newer version of the NetworkManager 
	 * would no longer be able to communicate with the
	 * previous version the protocol version should be
	 * changed - usually it would be incremented by 1.
	 */
	public static final byte PROTOCOL_VERSION = (byte)0x01;
	
	/**
	 * When a client connects to a server the networkUUID
	 * will not have been received yet, meaning the client
	 * wont have any UUID in the specified network. In the
	 * case of this happening, instead of the client generating
	 * it's own UUID, the shared NO_UUID constant should be
	 * associated with any client that hasn't been connected
	 * to a server yet.
	 */
	protected static final UUID NO_UUID = new UUID(0L, 0L);
	
	protected static final int STANDARD_PACKET_FLAG = 0x01;
	
	@SuppressWarnings("unchecked")
	private static final Class<? extends Packet>[] STANDARD_PACKETS = new Class[] {
		C00ClientIdentification.class,
		S00ServerIdentification.class
	};
	
	protected final Application app;
	private final IPacketRegistry registry;
	protected final NetworkSide side;
	
	private final IPacketRegistry standardRegistry;
	
	protected final DatagramSocket socket;
	
	private final SendThread sendThread;
	private final ReceiveThread receiveThread;
	
	private final PacketByteBuffer sendBuffer;
	
	protected InetSocketAddress remoteAddress;
	
	protected final Map<UUID, NetworkConnection> networkConnections;
	protected UUID networkUUID;
	
	private final LinkedBlockingQueue<Packet> packetsToProcess;
	
	public NetworkManager(Application app, IPacketRegistry registry, NetworkSide side) {
		this.app = app;
		this.registry = registry;
		this.side = side;
		
		standardRegistry = new BasicPacketRegistry(STANDARD_PACKETS);
		
		try {
			socket = new DatagramSocket(null);
		} catch (SocketException e) {
			throw new NetworkException("Unable to construct a socket", e);
		}
		
		sendThread = new SendThread(this);
		receiveThread = new ReceiveThread(this);
		
		sendBuffer = new PacketByteBuffer(MAX_PACKET_SIZE);

		remoteAddress = null;

		networkConnections = new HashMap<UUID, NetworkConnection>();
		networkUUID = NO_UUID;
		
		packetsToProcess = new LinkedBlockingQueue<Packet>();
	}

	protected void startThreads() {
		receiveThread.start();
		sendThread.start();
	}
	
	public void update() {
		// Process all packets. Multi-
		// threading is handled by the
		// LinkedQueue implementation.
		Packet packet;
		while ((packet = packetsToProcess.poll()) != null) {
			// Validate packet before processing
			if (validateReceivedPacket(packet))
				packet.processPacket(this);
		}
	}

	protected boolean validateReceivedPacket(Packet packet) {
		return true;
	}
	
	public void addPacketToSend(Packet packet) {
		addPacketToSend(packet, null);
	}

	public void addPacketToSend(Packet packet, UUID receiverUUID) {
		addPacketToSend(packet, receiverUUID, 0);
	}

	protected void addPacketToSend(Packet packet, UUID receiverUUID, int flags) {
		sendThread.addPacketToSend(packet, receiverUUID, flags);
	}
	
	/**
	 * Called from SendThread
	 * 
	 * @param packet
	 * @param receiverUUID
	 * @param flags 
	 */
	final void sendPacket(Packet packet, UUID receiverUUID, int flags) {
		// Setup buffer with 24 bytes 
		// of the packetClazzId and the 
		// networkUUID as well as flags. 
		sendBuffer.clear();
		
		// The packet registry has to be 
		// the same on the remote address 
		// and the local address (this 
		// instance) including the standard 
		// packet-registry.
		int packetClazzId; 
		if ((flags & STANDARD_PACKET_FLAG) != 0) {
			packetClazzId = standardRegistry.getPacketClazzId(packet.getClass());
		} else {
			packetClazzId = registry.getPacketClazzId(packet.getClass());
		}
		
		if (packetClazzId == -1)
			throw new NetworkException("Non-registered packet clazz: " + packet.getClass());
		sendBuffer.putInt(packetClazzId);
		
		sendBuffer.putUUID(networkUUID);
		sendBuffer.putInt(flags);
		
		// Load data from the packet
		packet.write(sendBuffer);

		// Setup DatagramPacket data
		byte[] data = sendBuffer.getData();
		int len = sendBuffer.getSize();
		DatagramPacket dgp = new DatagramPacket(data, len);
		
		if (remoteAddress != null || receiverUUID != null) {
			// If we're connected to a remote
			// address, we can ignore the
			// receiverUUID.
			if (remoteAddress == null) {
				NetworkConnection nc = getNetworkConnection(receiverUUID);
				if (nc != null)
					dgp.setSocketAddress(nc.getAddress());
			} else {
				dgp.setSocketAddress(remoteAddress);
			}
			
			// In both cases: when the connected
			// to a remote address or when the 
			// receiverUUID is non-null, we're
			// sending to a single address.
			try {
				socket.send(dgp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		// Send to all connected, if
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
	
	protected void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	protected NetworkConnection getNetworkConnection(InetSocketAddress addr) {
		if (addr == null)
			return null;
		
		for (NetworkConnection nc : networkConnections.values())
			if (addr.equals(nc.getAddress()))
				return nc;
		return null;
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
		if (buffer.remaining() < 4 + 16 + 4) // int(4) uuid(16) int(4)
			return;
		
		// Read 24 bytes of header info.
		// (Information about sender and
		// the type of packet sent).
		int packetId = buffer.getInt();
		UUID senderUUID = buffer.getUUID();
		int flags = buffer.getInt();
		
		Class<? extends Packet> packetClazz;
		if ((flags & STANDARD_PACKET_FLAG) != 0) {
			packetClazz = standardRegistry.getPacketClazz(packetId);
		} else {
			packetClazz = registry.getPacketClazz(packetId);
		}
		
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

		// Extract packet address
		InetAddress senderAddr = dgPacket.getAddress();
		int senderPort = dgPacket.getPort();
		InetSocketAddress addr = new InetSocketAddress(senderAddr, senderPort);
		
		// If we're connected to a remote
		// address, we should only receive
		// packets from that address.
		if (remoteAddress != null && !addr.equals(remoteAddress))
			return;
		
		// Set packet meta-information
		packet.setSender(addr, senderUUID);
		// Decode packet data
		packet.read(buffer);
		
		// Add packet to the process
		// queue. Should be validated
		// later for security reasons.
		packetsToProcess.add(packet);
	}
	
	protected boolean addNetworkConnection(UUID networkUUID, InetSocketAddress addr) {
		if (addr == null || networkUUID == null)
			return false;
		if (networkConnections.containsKey(networkUUID))
			return false;
		if (addr.equals(socket.getLocalSocketAddress()))
			return false;
		
		NetworkConnection connection = new NetworkConnection(networkUUID, addr);
		networkConnections.put(networkUUID, connection);
		
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
	
	public InetSocketAddress getAddress() {
		InetAddress localAddr = socket.getLocalAddress();
		int localPort = socket.getLocalPort();
		return new InetSocketAddress(localAddr, localPort);
	}

	public UUID getConnectionUUID() {
		return networkUUID;
	}
	
	public static byte getProtocolVersion() {
		return PROTOCOL_VERSION;
	}
	
	public static boolean isProtocolCompatible(byte protocolVersion) {
		return protocolVersion == PROTOCOL_VERSION;
	}
	
	@Override
	public void close() {
		sendThread.stopSending();
		receiveThread.stopReceiving();
		socket.close();
	}
}
