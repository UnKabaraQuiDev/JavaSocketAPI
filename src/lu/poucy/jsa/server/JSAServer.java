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
import lu.poucy.jsa.packets.prepared.PacketCrypter;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.packets.received.PacketChannel;
import lu.poucy.jsa.packets.sender.PacketSender;
import lu.poucy.jsa.packets.sender.PacketSenderRunnable;
import lu.poucy.jsa.packets.sender.PacketSenderState;
import lu.poucy.jsa.utils.JSAListener;

/**
 * @author Poucy113
 * <p>JSA Server class <a>https://github.com/Poucy113/JavaSocketAPI</a></p>
 */
public class JSAServer implements JSA<Thread> {

	private List<JSAListener> listeners = new ArrayList<>();
	private List<PacketSender> createdSenders = new ArrayList<>();
	
	private Thread th;
	
	private ServerSocket socket;
	private int port = 0;
	private int[] key;
	private JSA<Thread> instance;
	
	private PacketCrypter pc = PacketCrypter.DEFAULT;
	
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
		
		th = new Thread(
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
		);
		th.setName(this.getClass().getCanonicalName()+"-"+port);
		th.start();
		instance = this;
	}
	
	public PacketSender write(PreparedPacket ppacket) {
		PacketSender sender = new PacketSender(
			new PacketSenderRunnable() {
				@Override
				public void run(PacketSender sender) throws Exception {
					Socket socket = new Socket(ppacket.getHost(), ppacket.getPort());
					sender.setState(PacketSenderState.ALIVE);
					for(JSAListener l : listeners)
						l.onPacketSended(new PacketChannel(ppacket.getPacket(), socket, key, instance));
					PrintWriter w = new PrintWriter(socket.getOutputStream());
					w.write(pc.Crypt(ppacket.getPacket(), key));
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
		return new ServerSideClientProcessor(socket.accept(), getListeners(), key, instance);
	}
	
	public void registerListener(JSAListener listener) {if(!listeners.contains(listener)) listeners.add(listener);}
	public void unregisterListener(JSAListener listener) {if(listeners.contains(listener)) listeners.remove(listener);}
	
	public InetAddress getHost() {return socket.getInetAddress();}
	public int getPort() {return port;}
	public List<PacketSender> getCreatedSenders() {return createdSenders;}
	public List<JSAListener> getListeners() {return listeners;}
	public Thread getThread() {return th;}
	
	@Override
	public PacketCrypter getPacketCrypter() {return pc;}
	@Override
	public void setPacketCrypter(PacketCrypter pc) {this.pc = pc;}
	@Override
	public void close() throws IOException {socket.close();}
	
}
