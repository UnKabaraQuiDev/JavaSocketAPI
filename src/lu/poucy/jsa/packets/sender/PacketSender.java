package lu.poucy.jsa.packets.sender;

import java.util.function.Consumer;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.JSA.JSALogType;
import lu.poucy.jsa.JSA.JSAType;
import lu.poucy.jsa.exceptions.PacketSenderAlreadyUsedException;

public class PacketSender {

	private PacketSenderState state = PacketSenderState.UNKNOWN;
	private boolean used = false;
	
	private PacketSenderRunnable psr;
	
	private JSAType type;
	
	public PacketSender(PacketSenderRunnable packetSenderRunnable, JSAType type) {
		this.psr = packetSenderRunnable;
		this.type = type;
		setState(PacketSenderState.BEFORE);
	}
	
	public Thread start(Consumer<Exception> exception,
			Consumer<PacketSender> success) {
		PacketSender t = this;
		if(!used) {
			used = true;
			Thread th = new Thread(() -> {
				try {
					setState(PacketSenderState.STARTING);
					psr.run(t);
					setState(PacketSenderState.STOPPED);
					if(success != null)
						success.accept(this);
				} catch (Exception e) {
					setState(PacketSenderState.ERROR);
					JSA.error(e, type, JSALogType.WARN, null);
					if(exception != null)
						exception.accept(e);
				}
			});
			th.start();
			return th;
		}else {
			JSA.error(new PacketSenderAlreadyUsedException(state, psr, type, this), type, JSALogType.WARN, null);
			return null;
		}
	}
	
	public PacketSenderState getState() {return state;}
	public void setState(PacketSenderState state) {this.state = state;}
	public JSAType getType() {return type;}

}
