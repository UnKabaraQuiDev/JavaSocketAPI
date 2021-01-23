import java.io.IOException;
import java.util.Arrays;

import lu.poucy.jsa.client.JSAClient;
import lu.poucy.jsa.exceptions.IllegalJSAClientState;
import lu.poucy.jsa.exceptions.IllegalJSAServerState;
import lu.poucy.jsa.exceptions.KeyToShortException;
import lu.poucy.jsa.packets.Packet;
import lu.poucy.jsa.packets.prepared.PreparedPacket;
import lu.poucy.jsa.server.JSAServer;
import lu.poucy.jsa.utils.JSAListener;
import lu.poucy.jsa.utils.Pair;

public class JSATestMain {

	public static void main(String[] args) throws IllegalJSAServerState, IOException, KeyToShortException, IllegalJSAClientState {
		
		JSAServer server = new JSAServer(3000, new int[] {1,2,3,4,5,6,7,8,9,0});
		JSAClient client = new JSAClient(3001, new int[] {1,9,8,7,6,5,4,3,2,0});
		
		Packet p = new Packet(Arrays.asList(new Pair<String, Object>("test", "test2")));
		PreparedPacket pp = new PreparedPacket(p, server.getHost(), server.getPort());
		
		System.out.println(p);
		System.out.println(pp.crypt(new int[] {1,2,3,4,5,6,7,8,9,0}));
		
		client.registerListener(new JSAListener() {
			@Override
			public void onPacketSended(Packet packet) {
				System.out.println("client sended: "+packet);
			}
			@Override
			public void onPacketReceived(Packet packet) {
				System.out.println("client received: "+packet);
			}
		});
		server.registerListener(new JSAListener() {
			@Override
			public void onPacketSended(Packet packet) {
				System.out.println("server sended: "+packet);
			}
			@Override
			public void onPacketReceived(Packet packet) {
				System.out.println("server received: "+packet);
			}
		});
		
		pp.send(client);
		
		server.close();
		client.close();
		
	}

}
