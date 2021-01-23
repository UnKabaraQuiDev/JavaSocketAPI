package lu.poucy.jsa.packets.sender;

import java.util.function.Consumer;

import lu.poucy.jsa.utils.JSAUtils;
import lu.poucy.jsa.utils.JSAUtils.JSALogType;
import lu.poucy.jsa.utils.JSAUtils.JSAType;

public class PacketSender {

	private PacketSenderState state = PacketSenderState.UNKNOWN;
	
	private boolean runned = false;
	private PacketSenderRunnable psr;
	
	private JSAType type;
	
	public PacketSender(PacketSenderRunnable packetSenderRunnable, JSAType type) {
		this.psr = packetSenderRunnable;
		this.type = type;
	}
	
	public boolean start(Consumer<Exception> exception) {
		try {
			if(!runned) {
				setState(PacketSenderState.STARTING);
				psr.run(this);
				runned = true;
				setState(PacketSenderState.STOPPED);
			}
		} catch (Exception e) {
			setState(PacketSenderState.ERROR);
			JSAUtils.error(e, type, JSALogType.WARN, null);
			exception.accept(e);
		}
		return runned;
	}
	
	public PacketSenderState getState() {return state;}
	public void setState(PacketSenderState state) {this.state = state;}
	public JSAType getType() {return type;}

}
