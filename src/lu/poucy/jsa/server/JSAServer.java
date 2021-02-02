package lu.poucy.jsa.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.IllegalJSAServerState;
import lu.poucy.jsa.exceptions.InvalidKeyException;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.sender.PacketSender;
import lu.poucy.jsa.packets.sender.PacketSenderRunnable;
import lu.poucy.jsa.packets.sender.PacketSenderState;
import lu.poucy.jsa.utils.JSAListener;

public class JSAServer implements JSA<Thread> {

	private List<JSAListener> listeners = new ArrayList<>();
	private List<PacketSender> createdSenders = new ArrayList<>();
	
	private ServerSocket socket;
	private int port = 0;
	private int[] key;
	
	public JSAServer(int port, int[] key) throws IllegalJSAServerState, IOException, KeyToShortException {
		if(key.length <= 3)
			throw new KeyToShortException(key);
		
		this.port = port;
		try {
			this.socket = new ServerSocket(this.port);
		}catch(BindException e) {
			throw new IllegalJSAServerState("A server is already started on port: "+this.port);
		}
		this.key = key;
		
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					while(!socket.isClosed()) {
						try {
							read();
						} catch (IOException | InvalidKeyException e) {
							if(e.getLocalizedMessage() != "Socket closed")
								JSA.error(e, JSAType.SERVER, JSALogType.CRITICAL, socket);
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
		createdSenders.add(sender);
		return sender;
	}
	public Thread read() throws IOException, InvalidKeyException {
		return new ServerSideClientProcessor(socket.accept(), getListeners(), key);
	}
	
	public void registerListener(JSAListener listener) {if(!listeners.contains(listener)) listeners.add(listener);}
	public void unregisterListener(JSAListener listener) {if(listeners.contains(listener)) listeners.remove(listener);}
	
	public InetAddress getHost() {return socket.getInetAddress();}
	public int getPort() {return port;}
	public List<PacketSender> getCreatedSenders() {return createdSenders;}
	public List<JSAListener> getListeners() {return listeners;}
	
	@Override
	public void close() throws IOException {socket.close();}
	
}
