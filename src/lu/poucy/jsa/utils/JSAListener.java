package lu.poucy.jsa.utils;

import lu.poucy.jsa.packets.received.PacketChannel;

public interface JSAListener {
	void onPacketReceived(PacketChannel p);
	void onPacketSended(PacketChannel packet);
}
