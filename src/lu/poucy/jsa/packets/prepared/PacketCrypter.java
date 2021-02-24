package lu.poucy.jsa.packets.prepared;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import lu.poucy.jsa.exceptions.InvalidKeyException;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.Packet;
import lu.poucy.jsa.utils.Pair;

public abstract class PacketCrypter {

	public abstract String Crypt(Packet packet, int[] key) throws KeyToShortException;
	public abstract Packet Decrypt(JSONObject obj, int[] key) throws InvalidKeyException;
	
	public static final PacketCrypter DEFAULT = new PacketCrypter() {
		@Override
		public String Crypt(Packet packet, int[] key) throws KeyToShortException {
			if(key.length <= 3)
				throw new KeyToShortException(key);
			
			String p = packet.toString();
			JSONObject ret = new JSONObject();
			String pro = "";
			
			for(int i = 0; i < p.length(); i++) {
				pro += getAlpha(p.toCharArray()[i]+"")+key[round(i % key.length)];
				if(i != (p.length()-1))
					pro += ";";
			}
			
			ret.put("packet", pro.toString());
			
			return ret.toString();
		}
		@Override
		public Packet Decrypt(JSONObject obj, int[] key) throws InvalidKeyException {
			try {
				String str = obj.getString("packet");
				String[] _str = str.split(";");
				str = "";
				
				for (int i = 0; i < _str.length; i++)
					str += getChar(Integer.valueOf(_str[i])-key[round(i % key.length)]);
				
				obj = new JSONObject(str);
				Packet p = new Packet(new ArrayList<>());
				for(String o : obj.keySet())
					p.addArg(new Pair<String, Object>(o, obj.get(o)));
				return p;
			}catch(JSONException exc) {
				throw new InvalidKeyException(key);
			}
		}
		private String[] ALPHA = "0123456789.,;:?!§/*-+&\"'(-_)=&~#{[|`\\^@]}$*ù%µ£¨ù*¤ ²<>azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBNèêëïîôöùûüÿçæÆŒÀÂÄÇÉÈÊËÎÏÔÖÙÛÜŸàâäé".split("");
		
		private int getAlpha(String c) {for(int i = 0; i < ALPHA.length; i++) if(ALPHA[i].equals(c)) return i;return 0;}
		private String getChar(int c) {return ALPHA[c];}
		private int round(double d) {if(Math.round(d) < d) return (int) (Math.round(d)+1); return (int) Math.round(d);}
	};
	
}
