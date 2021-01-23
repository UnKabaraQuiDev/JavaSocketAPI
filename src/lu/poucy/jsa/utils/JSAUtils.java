package lu.poucy.jsa.utils;

import java.net.ServerSocket;

public final class JSAUtils {

	public static void error(Exception e, JSAType type, JSALogType log, ServerSocket sock) {
		System.err.println("[JSA]["+type+"]["+log+"]"+(sock == null ? "" : "{Port: "+sock.getLocalPort()+"}")+"> "+e.getLocalizedMessage());
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
