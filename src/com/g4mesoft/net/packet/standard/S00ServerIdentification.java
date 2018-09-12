package com.g4mesoft.net.packet.standard;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;

public class S00ServerIdentification extends Packet {

	public byte protocolVersion;
	public UUID clientUUID;

	public S00ServerIdentification(byte protocolVersion, UUID clientUUID) {
		this.protocolVersion = protocolVersion;
		this.clientUUID = clientUUID;
	}

	public S00ServerIdentification() {
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		protocolVersion = buffer.getByte();
		clientUUID = buffer.getUUID();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putByte(protocolVersion);
		buffer.putUUID(clientUUID);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer() || clientUUID == null)
			return;
		((ClientNetworkManager)manager).serverIdentified(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		return bytesToRead == 17; // byte(1) uuid(16)
	}
}
