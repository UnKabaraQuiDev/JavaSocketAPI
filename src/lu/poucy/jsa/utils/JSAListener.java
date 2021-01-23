package lu.poucy.jsa.utils;

import lu.poucy.jsa.packets.Packet;

public interface JSAListener {
	void onPacketReceived(Packet packet);
	void onPacketSended(Packet packet);
}
