package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.AbstractPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow extends AbstractPacket<INetHandlerPlayClient> {
	private int windowId;

	public S2EPacketCloseWindow() {
	}

	public S2EPacketCloseWindow(int windowIdIn) {
		this.windowId = windowIdIn;
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		super.processPacket(handler);
		if (cancelled) {
			return;
		}
		handler.handleCloseWindow(this);
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.windowId = buf.readUnsignedByte();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeByte(this.windowId);
	}
}
