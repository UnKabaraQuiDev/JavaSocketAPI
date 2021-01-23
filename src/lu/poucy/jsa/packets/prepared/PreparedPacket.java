package lu.poucy.jsa.packets.prepared;

import java.net.InetAddress;
import java.util.ArrayList;

import org.json.JSONObject;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.Packet;
import lu.poucy.jsa.packets.sender.PacketSender;
import lu.poucy.jsa.utils.Pair;

public class PreparedPacket {

	private Packet packet;
	
	private InetAddress host;
	private int port;
	
	public PreparedPacket(Packet p, InetAddress _host, int _port) {
		this.packet = p;
		this.host = _host;
		this.port = _port;
	}
	
	public InetAddress getHost() {return host;}
	public PreparedPacket setHost(InetAddress host) {this.host = host;return this;}
	public int getPort() {return port;}
	public PreparedPacket setPort(int port) {this.port = port;return this;}
	public Packet getPacket() {return packet;}
	
	public PacketSender send(JSA server) {return server.write(this).start((e) -> e.printStackTrace());}

	public String crypt(int[] key) throws KeyToShortException {
		if(key.length <= 3)
			throw new KeyToShortException(key);
		
		String p = packet.toString();
		JSONObject ret = new JSONObject(
				//"{\"key\":\""+new String(key).toString()+"\"}"
		);
		String pro = "";
		
		for(int i = 0; i < p.length(); i++) {
			//System.out.println(i+" : "+key.length+" : "+round(i % key.length)+" : "+Integer.valueOf(key[round(i % key.length)]+"")+" : "+getAlpha(p.toCharArray()[i]+"")+" : "+(getAlpha(p.toCharArray()[i]+"")+Integer.valueOf(key[round(i % key.length)]+"")));
			pro += getAlpha(p.toCharArray()[i]+"")+key[round(i % key.length)];
			if(i != (p.length()-1))
				pro += ";";
		}
		
		ret.put("packet", pro.toString());
		
		return ret.toString();
	}
	
	public static Packet decrypt(JSONObject obj, int[] key) {
		String str = obj.getString("packet");
		String[] _str = str.split(";");
		str = "";
		
		for (int i = 0; i < _str.length; i++) {
			//System.out.println(Integer.valueOf(_str[i])-Integer.valueOf(key[round(i % key.length)]+""));
			str += getChar(Integer.valueOf(_str[i])-key[round(i % key.length)]);
		}
		
		obj = new JSONObject(str);
		Packet p = new Packet(new ArrayList<>());
		for(String o : obj.keySet())
			p.addArg(new Pair<String, Object>(o, obj.get(o)));
		return p;
		
	}
	
	public static String[] alpha = "0123456789.,;:?!§/*-+&\"'(-_)=&~#{[|`\\^@]}$*ù%µ£¨ù*¤ ²<>azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBNèêëïîôöùûüÿçæÆŒÀÂÄÇÉÈÊËÎÏÔÖÙÛÜŸàâäé".split("");
	
	public static int getAlpha(String c) {for(int i = 0; i < alpha.length; i++) if(alpha[i].equals(c)) return i;return 0;}
	public static String getChar(int c) {return alpha[c];}
	public static int round(double d) {if(Math.round(d) < d) return (int) (Math.round(d)+1); return (int) Math.round(d);}
	
}
