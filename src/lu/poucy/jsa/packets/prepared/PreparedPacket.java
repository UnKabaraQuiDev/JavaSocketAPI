package lu.poucy.jsa.packets.prepared;

import java.net.InetAddress;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.packets.Packet;

public class PreparedPacket {

	private Packet packet;
	
	private InetAddress host;
	private int port;
	
	public PreparedPacket(Packet p, InetAddress _host, int _port) {
		this.packet = p;
		this.host = _host;
		this.port = _port;
	}
	public PreparedPacket(Packet p, InetAddress _host, int _port, PacketCrypter pc) {
		this.packet = p;
		this.host = _host;
		this.port = _port;
	}
	
	public InetAddress getHost() {return host;}
	public PreparedPacket setHost(InetAddress host) {this.host = host;return this;}
	public int getPort() {return port;}
	public PreparedPacket setPort(int port) {this.port = port;return this;}
	public Packet getPacket() {return packet;}
	
	public Thread send(JSA<?> jsa) {return jsa.write(this).start((e) -> e.printStackTrace(), null);}
	
	public static String[] alpha = "0123456789.,;:?!§/*-+&\"'(-_)=&~#{[|`\\^@]}$*ù%µ£¨ù*¤ ²<>azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBNèêëïîôöùûüÿçæÆŒÀÂÄÇÉÈÊËÎÏÔÖÙÛÜŸàâäé".split("");
	
	public static int getAlpha(String c) {for(int i = 0; i < alpha.length; i++) if(alpha[i].equals(c)) return i;return 0;}
	public static String getChar(int c) {return alpha[c];}
	public static int round(double d) {if(Math.round(d) < d) return (int) (Math.round(d)+1); return (int) Math.round(d);}
	
}
