package lu.poucy.jsa.exceptions;

import lu.poucy.jsa.JSA.JSAType;
import lu.poucy.jsa.packets.sender.PacketSender;
import lu.poucy.jsa.packets.sender.PacketSenderRunnable;
import lu.poucy.jsa.packets.sender.PacketSenderState;

@SuppressWarnings("serial")
public class PacketSenderAlreadyUsedException extends Exception {

	PacketSenderState state;
	PacketSenderRunnable psr;
	JSAType type;
	PacketSender packetSender;
	
	public PacketSenderAlreadyUsedException(PacketSenderState state,
			PacketSenderRunnable psr,
			JSAType type,
			PacketSender packetSender) {
		super("PacketSender already used !");
		
		this.state = state;
		this.psr = psr;
		this.type = type;
		this.packetSender = packetSender;
	}

}
