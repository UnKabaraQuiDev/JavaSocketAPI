package lu.poucy.jsa.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.IllegalJSAClientState;
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
 * <p>JSA Client class <a>https://github.com/Poucy113/JavaSocketAPI</a></p>
 */
public class JSAClient implements JSA<Void> {

	private List<JSAListener> listeners = new ArrayList<>();
	
	private Thread th;
	private ServerSocket socket;
	private int port = 0;
	private int[] key;
	private JSA<Void> instance;
	
	private PacketCrypter pc = PacketCrypter.DEFAULT;
	
	public JSAClient(int port, int[] key) throws IllegalJSAClientState, IOException, KeyToShortException {
		if(key.length <= 3)
			throw new KeyToShortException(key);
		
		this.port = port;
		this.key = key;
		
		instance = this;
	} 
	
	@Override
	public JSA<Void> open() throws Exception {
		try {
			this.socket = new ServerSocket(this.port);
		}catch(BindException e) {
			throw new IllegalJSAClientState("A client is already started on port: "+this.port);
		}catch(IOException e) {
			throw e;
		}
		
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
		return instance;
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
						l.onPacketSended(new PacketChannel(ppacket.getPacket(), socket, key, instance));
					PrintWriter w = new PrintWriter(socket.getOutputStream());
					w.write(pc.Crypt(ppacket.getPacket(), key));
					w.flush();
					sender.setState(PacketSenderState.ENDING);
					socket.close();
					sender.setState(PacketSenderState.STOPPED);
				}
			},
			JSAType.CLIENT
		);
		return sender;
	}
	@SuppressWarnings("resource")
	public Void read() throws IOException, InvalidKeyException {
		Socket read = socket.accept();
		Scanner s = new Scanner(read.getInputStream()).useDelimiter("\\A");
		String in = s.hasNext() ? s.next() : "";
		PacketChannel p = new PacketChannel(pc.Decrypt(new JSONObject(in), key), read, key, instance);
		for(JSAListener l : listeners)
			l.onPacketReceived(p);
		s.close();
		return null;
	}
	
	public void registerListener(JSAListener listener) {if(!listeners.contains(listener)) listeners.add(listener);}
	public void unregisterListener(JSAListener listener) {if(listeners.contains(listener)) listeners.remove(listener);}
	public Thread getThread() {return th;}
	public InetAddress getHost() {return socket.getInetAddress();}
	public int getPort() {return port;}
	
	@Override
	public void close() throws Exception {
		if(socket != null) {
			socket.close();
			return;
		}
		throw new IllegalJSAClientState("Client isn't started, unable to close");
	}
	@Override
	public PacketCrypter getPacketCrypter() {return pc;}
	@Override
	public void setPacketCrypter(PacketCrypter pc) {this.pc = pc;}
	@Override
	public boolean isAlive() {return (socket != null);}

}
