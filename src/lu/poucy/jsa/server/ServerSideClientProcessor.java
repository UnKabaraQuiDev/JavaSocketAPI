package lu.poucy.jsa.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.InvalidKeyException;
import lu.poucy.jsa.packets.received.PacketChannel;
import lu.poucy.jsa.utils.JSAListener;
import lu.poucy.jsa.utils.JSAProcessor;

public class ServerSideClientProcessor extends Thread implements JSAProcessor {
	
	private Socket socket;
	private List<JSAListener> listeners;
	private int[] key;
	private JSA<?> in;
	
	public ServerSideClientProcessor(Socket socket, List<JSAListener> list, int[] key, JSA<?> in) throws IOException, InvalidKeyException {
		super("ServerSideClientProcessor:"+socket.toString());
		
		this.socket = socket;
		this.listeners = list;
		this.key = key;
		this.in = in;
		
		start();
		process();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void process() throws IOException, InvalidKeyException {
		
		Scanner s = new Scanner(socket.getInputStream()).useDelimiter("\\A");
		String in = s.hasNext() ? s.next() : "";
		PacketChannel p = new PacketChannel(this.in.getPacketCrypter().Decrypt(new JSONObject(in), key), socket, key, this.in);
		for(JSAListener l : listeners)
			l.onPacketReceived(p);
		s.close();
		socket.close();
		stop();
		
	}
	
	public Socket getSocket() {return socket;}

}
