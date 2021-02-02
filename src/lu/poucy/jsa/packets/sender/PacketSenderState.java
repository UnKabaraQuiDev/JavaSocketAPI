package lu.poucy.jsa.packets.sender;

public enum PacketSenderState {
	BEFORE("Not started"),
	UNKNOWN("Unknown"),
	STARTING("Starting"),
	ALIVE("Alive"),
	ENDING("Ending"),
	STOPPED("Stopped"),
	ERROR("Stopped by error");
	private String s;
	private PacketSenderState(String _s) {
		this.s = _s;
	}
	public String getText() {return s;}
}
