package lu.poucy.jsa.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import lu.poucy.jsa.exceptions.InvalidKeyException;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.received.PacketChannel;
import lu.poucy.jsa.utils.JSAListener;
import lu.poucy.jsa.utils.JSAProcessor;

public class ServerSideClientProcessor extends Thread implements JSAProcessor {
	
	private Socket socket;
	private List<JSAListener> listeners;
	private int[] key;
	
	public ServerSideClientProcessor(Socket socket, List<JSAListener> list, int[] key) throws IOException, InvalidKeyException {
		super("ServerSideClientProcessor:"+socket.toString());
		
		this.socket = socket;
		this.listeners = list;
		this.key = key;
		
		start();
		process();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void process() throws IOException, InvalidKeyException {
		
		Scanner s = new Scanner(socket.getInputStream()).useDelimiter("\\A");
		String in = s.hasNext() ? s.next() : "";
		PacketChannel p = new PacketChannel(PreparedPacket.Decrypt(new JSONObject(in), key), socket, key);
		for(JSAListener l : listeners)
			l.onPacketReceived(p);
		s.close();
		socket.close();
		stop();
		
	}
	
	public Socket getSocket() {return socket;}

}
