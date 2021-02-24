package lu.poucy.jsa;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.Date;

import lu.poucy.jsa.exceptions.InvalidKeyException;
import lu.poucy.jsa.packets.prepared.PacketCrypter;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.sender.PacketSender;

public interface JSA<T> {
	
	PacketSender write(PreparedPacket ppacket);
	T read() throws IOException, InvalidKeyException;
	void close() throws IOException;
	PacketCrypter getPacketCrypter();
	void setPacketCrypter(PacketCrypter pc);
	
	public static void error(Exception e, JSAType type, JSALogType log, ServerSocket sock) {
		//String s = "";
		//for(StackTraceElement ss : e.getStackTrace()/*[e.getStackTrace().length-1]*/)
			//s += ss+"; ";
		System.err.println("["+time()+"]> [JSA]["+type+"]["+log+"]<"+(e != null ? (e.getStackTrace() != null ? /*s*/ e.getStackTrace()[e.getStackTrace().length-1] : "") : "")+">"+(sock == null ? "" : "{Port: "+sock.getLocalPort()+"}")+"> "+(e != null ? e.getLocalizedMessage() : "No info"));
	}
	
	@SuppressWarnings("deprecation")
	static String time() {
		Date date= Calendar.getInstance().getTime();
		return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	}

	public enum JSALogType {
		INFO("Info"), WARN("Warn"), CRITICAL("Critical");
		private String text;
		private JSALogType(String t) {this.text = t;}
		public String getText() {return text;}
		@Override public String toString() {return getText();}
	}

	public enum JSAType {
		CLIENT("Client"), SERVER("Server");
		private String text;
		private JSAType(String t) {this.text = t;}
		public String getText() {return text;}
		@Override public String toString() {return getText();}
	}

}
