package com.g4mesoft.net.packet.standard;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;

public class C00ClientIdentification extends Packet {

	public byte protocolVersion;
	
	public C00ClientIdentification(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public C00ClientIdentification() {
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		protocolVersion = buffer.getByte();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putByte(protocolVersion);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isClient())
			return;
		((ServerNetworkManager)manager).connectClient(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		return bytesToRead == 1; // byte(1)
	}
}
