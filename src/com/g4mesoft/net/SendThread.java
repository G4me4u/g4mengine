package com.g4mesoft.net;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.g4mesoft.net.packet.Packet;

public final class SendThread extends Thread {
	
	private final NetworkManager manager;

	private final LinkedBlockingQueue<PacketEntry> sendQueue;
	
	// We need to have a volatile boolean
	// to make sure it can be handled by
	// multiple threads properly.
	private volatile boolean canSend;
	
	SendThread(NetworkManager manager) {
		this.manager = manager;
		
		sendQueue = new LinkedBlockingQueue<PacketEntry>();
		
		// Make sure the thread
		// terminates when the
		// main thread stops.
		setDaemon(true);
		
		// Upon construction and before
		// stopSending has been called,
		// this thread is valid and can
		// send packets. If stopSending
		// is called, the thread will
		// terminate.
		canSend = true;
	}
	
	void addPacketToSend(Packet packet, UUID receiverUUID) {
		sendQueue.add(new PacketEntry(packet, receiverUUID));
	}

	void stopSending() {
		canSend = false;
		
		// If we're waiting for a packet 
		// to be sent, we should interrupt 
		// the thread.
		interrupt();
	}
	
	@Override
	public void run() {
		while (canSend) {
			PacketEntry entry;
			try {
				entry = sendQueue.take();
			} catch (InterruptedException e) {
				// We were interrupted during
				// our queue-polling. The packet
				// will be undefined - continue.
				continue;
			}
			
			manager.sendPacket(entry.packet, entry.receiverUUID);
		}
	}
	
	private class PacketEntry {
		
		private final Packet packet;
		private final UUID receiverUUID;
		
		private PacketEntry(Packet packet, UUID receiverUUID) {
			this.packet = packet;
			this.receiverUUID = receiverUUID;
		}
	}
}
