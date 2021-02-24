package lu.poucy.jsa.packets.received;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;

import lu.poucy.jsa.JSA;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.Packet;

public class PacketChannel {

	private Packet packet;
	private Socket socket;
	private int[] key;
	private Scanner input;
	private JSA<?> in;
	
	public PacketChannel(Packet decrypt, Socket socket, int[] key, JSA<?> in) throws IOException {
		this.packet = decrypt;
		this.socket = socket;
		this.in = in;
		this.key = key;
		this.input = new Scanner(socket.getInputStream());
	}
	
	public void answer(JSONObject obj) throws KeyToShortException, IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.write(in.getPacketCrypter().Crypt(new Packet(obj), key));
	}
	
	public Scanner getInput() {return input;}
	public Packet getPacket() {return packet;}
	public Socket getSocket() {return socket;}
	public JSA<?> getJSA() {return in;}

}
