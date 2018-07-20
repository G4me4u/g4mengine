package com.g4mesoft.net.packet;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;

public abstract class Packet {

	public SocketAddress senderAddress;
	public UUID senderUUID;
	
	public boolean received;
	
	protected Packet() {
		senderAddress = null;
		senderUUID = null;

		received = false;
	}

	/**
	 * Called to set the information used
	 * to identify the connection which sent
	 * this packet.
	 * 
	 * @param senderAddress - the address of the sender
	 * @param senderUUID - the connection UUID of the sender
	 */
	public final void setSender(SocketAddress senderAddress, UUID senderUUID) {
		this.senderAddress = senderAddress;
		this.senderUUID = senderUUID;

		received = true;
	}

	public abstract void read(PacketByteBuffer buffer);
	
	public abstract void write(PacketByteBuffer buffer);
	
	public abstract void processPacket(NetworkManager manager);

	public abstract boolean checkSize(int bytesToRead);
}
