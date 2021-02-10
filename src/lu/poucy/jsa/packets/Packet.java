package lu.poucy.jsa.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import lu.poucy.jsa.utils.Pair;

public class Packet {

	private List<Pair<String, Object>> args = new ArrayList<>();
	
	public Packet(List<Pair<String, Object>> a) {
		this.args = a;
	}
	public Packet(JSONObject obj) {
		for(String s : obj.keySet())
			args.add(new Pair<String, Object>(s, obj.get(s)));
	}

	public List<Pair<String, Object>> getArgs() {return args;}
	public Pair<String, Object> getArg(String key) {for(Pair<String, Object> o : getArgs()) if(o.getKey().equals(key)) return o; return null;}
	public Packet removeArg(String s) {for(Pair<String, Object> p : args) if(p.getKey().equals(s))args.remove(p); return this;}
	public Packet addArg(Pair<String, Object> obj) {if(!args.contains(obj)) args.add(obj); return this;}
	public Packet addArg(String key, Object value) {addArg(new Pair<String, Object>(key, value)); return this;}
	
	@Override
	public String toString() {
		HashMap<String, Object> map = new HashMap<>();
		for(Pair<String, Object> obj : args)
			map.put(obj.getKey(), obj.getValue());
		return new JSONObject(map).toString();
	}
	
}
