package com.g4mesoft.net;

import com.g4mesoft.net.packet.Packet;

public class BasicPacketRegistry implements IPacketRegistry {

	private final Class<? extends Packet>[] registeredPacketClazzes;
	
	@SuppressWarnings("unchecked")
	public BasicPacketRegistry(Class<? extends Packet>[] packetClazzes) {
		registeredPacketClazzes = new Class[packetClazzes.length];
		for (int i = 0; i < packetClazzes.length; i++)
			registeredPacketClazzes[i] = packetClazzes[i];
	}
	
	@Override
	public boolean hasPacketClazz(Class<? extends Packet> packetClazz) {
		return getPacketClazzId(packetClazz) != -1;
	}

	@Override
	public boolean hasPacketClazzId(int packetClazzId) {
		return packetClazzId >= 0 && packetClazzId < getRegistrySize();
	}

	@Override
	public Class<? extends Packet> getPacketClazz(int packetClazzId) {
		if (hasPacketClazzId(packetClazzId))
			return registeredPacketClazzes[packetClazzId];
		return null;
	}

	@Override
	public int getPacketClazzId(Class<? extends Packet> packetClazz) {
		if (packetClazz != null) {
			for (int i = 0; i < registeredPacketClazzes.length; i++) {
				if (packetClazz.equals(registeredPacketClazzes[i]))
					return i;
			}
		}
		
		return -1;
	}
	
	public int getRegistrySize() {
		return registeredPacketClazzes.length;
	}
}
