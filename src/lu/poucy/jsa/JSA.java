package lu.poucy.jsa;

import java.io.IOException;

import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.sender.PacketSender;

public interface JSA {
	PacketSender write(PreparedPacket ppacket);
	void read() throws IOException;
}
