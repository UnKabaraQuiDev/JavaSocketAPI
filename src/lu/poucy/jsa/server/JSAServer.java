package lu.poucy.jsa.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.IllegalJSAServerState;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.Packet;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.sender.PacketSender;
import lu.poucy.jsa.packets.sender.PacketSenderRunnable;
import lu.poucy.jsa.packets.sender.PacketSenderState;
import lu.poucy.jsa.utils.JSAListener;
import lu.poucy.jsa.utils.JSAUtils;
import lu.poucy.jsa.utils.JSAUtils.JSALogType;
import lu.poucy.jsa.utils.JSAUtils.JSAType;

public class JSAServer implements JSA {

	private List<JSAListener> listeners = new ArrayList<>();
	
	private ServerSocket socket;
	private int port = 0;
	private char[] key;
	
	public JSAServer(int port, char[] key) throws IllegalJSAServerState, IOException, KeyToShortException {
		if(key.length <= 3)
			throw new KeyToShortException(key);
		if(socket != null)
			throw new IllegalJSAServerState("Server already starter on port: "+this.port);
		
		this.port = port;
		this.socket = new ServerSocket(this.port);
		this.key = key;
		
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					while(!socket.isClosed()) {
						try {
							read();
						} catch (IOException e) {
							if(e.getLocalizedMessage() != "Socket closed")
								JSAUtils.error(e, JSAType.SERVER, JSALogType.CRITICAL, socket);
						}
					}
				}
			}
		).start();
	}
	
	public PacketSender write(PreparedPacket ppacket) {
		PacketSender sender = new PacketSender(
			new PacketSenderRunnable() {
				@Override
				public void run(PacketSender sender) throws Exception {
					sender.setState(PacketSenderState.STARTING);
					Socket socket = new Socket(ppacket.getHost(), ppacket.getPort());
					sender.setState(PacketSenderState.ALIVE);
					for(JSAListener l : listeners)
						l.onPacketSended(ppacket.getPacket());
					PrintWriter w = new PrintWriter(socket.getOutputStream());
					w.write(ppacket.crypt(key));
					w.flush();
					sender.setState(PacketSenderState.ENDING);
					socket.close();
					sender.setState(PacketSenderState.STOPPED);
				}
			},
			JSAType.SERVER
		);
		return sender;
	}
	public void read() throws IOException {
		Socket read = socket.accept();
		Scanner s = new Scanner(read.getInputStream()).useDelimiter("\\A");
		String in = s.hasNext() ? s.next() : "";
		Packet p = PreparedPacket.decrypt(new JSONObject(in), key);
		for(JSAListener l : listeners)
			l.onPacketReceived(p);
	}
	
	public void registerListener(JSAListener listener) {if(!listeners.contains(listener)) listeners.add(listener);}
	public void unregisterListener(JSAListener listener) {if(listeners.contains(listener)) listeners.remove(listener);}
	
	
	public void close() throws IOException {socket.close();}
	
}
