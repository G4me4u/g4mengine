package com.g4mesoft.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ReceiveThread extends Thread {
	
	private final NetworkManager manager;
	
	private final byte[] buffer;
	
	// We need to have a volatile boolean
	// to make sure it can be handled by
	// multiple threads properly.
	private volatile boolean canReceive;
	
	ReceiveThread(NetworkManager manager) {
		this.manager = manager;
		
		buffer = new byte[NetworkManager.MAX_PACKET_SIZE];
		
		// Make sure the thread
		// terminates when the 
		// main thread stops.
		setDaemon(true);

		// Upon construction of this
		// thread we can receive packets.
		// When stopReceiving is called
		// the thread will terminate.
		canReceive = true;
	}
	
	void stopReceiving() {
		canReceive = false;
		
		// If we're currently trying to
		// receive a packet, we should
		// interrupt the thread to stop
		// it.
		interrupt();
	}
	
	@Override
	public void run() {
		DatagramPacket packet;
		while (canReceive) {
			packet = new DatagramPacket(buffer, NetworkManager.MAX_PACKET_SIZE);
			try {
				manager.socket.receive(packet);
				manager.receiveDatagramPacket(packet);
			} catch (SocketException se) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
