package com.g4mesoft.net;

import com.g4mesoft.net.packet.Packet;

public interface IPacketRegistry {

	public boolean hasPacketClazz(Class<? extends Packet> packetClazz);
	
	public boolean hasPacketClazzId(int packetClazzId);
	
	public Class<? extends Packet> getPacketClazz(int packetClazzId);
	
	public int getPacketClazzId(Class<? extends Packet> packetClazz);
	
}
