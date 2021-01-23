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
	
	public List<Pair<String, Object>> getArgs() {return args;}
	public Packet removeArg(String s) {for(Pair<String, Object> p : args) if(p.getKey().equals(s))args.remove(p); return this;}
	public Packet addArg(Pair<String, Object> obj) {if(!args.contains(obj)) args.add(obj); return this;}
	
	@Override
	public String toString() {
		HashMap<String, Object> map = new HashMap<>();
		for(Pair<String, Object> obj : args)
			map.put(obj.getKey(), obj.getValue());
		return new JSONObject(map).toString();
	}
	
}
