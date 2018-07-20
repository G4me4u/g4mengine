package com.g4mesoft.net.server;

import java.net.SocketAddress;
import java.net.SocketException;

import com.g4mesoft.Application;
import com.g4mesoft.net.IPacketRegistry;
import com.g4mesoft.net.NetworkException;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;

public class ServerNetworkManager extends NetworkManager {

	public ServerNetworkManager(Application app, IPacketRegistry registry, SocketAddress address) {
		super(app, registry, NetworkSide.SERVER);

		try {
			socket.bind(address);
		} catch (SocketException e) {
			throw new NetworkException("Unable to bind socket: " + address, e);
		}
	}
}
