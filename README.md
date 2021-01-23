# JSA (Java Socket API)

JSA is a packet-based communication system in Java

## Summary

1. [Introduction](#introduction)
2. [Create a server](#create-a-server)
3. [Create a client](#create-a-client)
4. [Create a packet](#create-a-packet)
5. [Prepare a packet](#prepare-a-packet)
6. [Send a packet](#send-a-packet)
7. [Listeners](#listeners)
8. [Create a custom packet](#create-a-custom-packet)

## Introduction

This package includes a client/server solution to transfer series of key/values.
Encryption is based on a symetric key for variable offset. 

Each server/client has its own port which has been passed as an argument in the constructor, each port can only be used by one server/client.
It's possible to add listeners to servers/clients, listeners contain methods (`onPacketSended`, `onPacketReceived`) which are called when a packet is sented or received by the server on which the listener is registered.

## Create a server

Server side instantiation:
```java
JSAServer server = new lu.poucy.jsa.server.JSAServer(3000, new int[] {1,2,3,4,5,6,7,8,9,0});
```
Arguments of the server's constructor are:
1. The TCP-port on which the server has to listen,
2. The key to encrypt communications (array of minimum 4 numeric values)

## Create a client

Client side instantiation:
```java
JSAClient client = new lu.poucy.jsa.client.JSAClient(3001, new int[] {1,2,3,4,5,6,7,8,9,0});
```
Arguments of the client's constructor are the same as the server. ([Here...](#create-a-server))

## Create a packet

Packet instantiation:
```java
Packet packet = new lu.poucy.jsa.packets.Packet(java.utils.Arrays.asList(
	new lu.poucy.jsa.utils.Pair<String, Object>("string", "value"),
	new lu.poucy.jsa.utils.Pair<String, Object>("int", 1),
	new lu.poucy.jsa.utils.Pair<String, Object>("double", 1.5),
	new lu.poucy.jsa.utils.Pair<String, Object>("object", new java.lang.Object())
));
```

**or**
```java
java.utils.List<lu.poucy.jsa.utils.Pair<String, Object>> args = new java.utils.ArrayList<>();
args.add(new lu.poucy.jsa.utils.Pair<String, Object>("string", "value"));
args.add(new lu.poucy.jsa.utils.Pair<String, Object>("int", 1));
args.add(new lu.poucy.jsa.utils.Pair<String, Object>("double", 1.5));
args.add(new lu.poucy.jsa.utils.Pair<String, Object>("object", new java.lang.Object()));

Packet packet = new lu.poucy.jsa.packets.Packet(args);
```
A packet consists of a list of pairs of a text key and any kind of value.
The value can be text, numeric and event an object. 

## Prepare a packet

Packet preparation:
```java
PreparedPacket ppacket = new PreparedPacket(new Packet(new ArrayList()), InetAddress.getHost("localhost"), 3000);
```
A PreparedPacket is an intermediate step between creating the packet, and sending it.
Arguments are:
1. The packet to be sent
2. The target ip-address
3. The target TCP-port

## Send a packet

Packet transmission:
```java
JSAServer server = new lu.poucy.jsa.server.JSAServer(3000, new char[] {1,2,3,4,5,6,7,8,9,0});
JSAClient client = new lu.poucy.jsa.client.JSAClient(3001, new char[] {1,2,3,4,5,6,7,8,9,0});

PreparedPacket ppacket = new PreparedPacket(new Packet(new ArrayList()), server.getHost(), server.getPort());

PacketSender sender = ppacket.send(client);
```
To send a packet, the PreparedPacket class contains a `send` function, the argument of which is the server/client with which it will be sent to the address that is passed as an argument in the constructor.
This function returns a PacketSender which allows to start sending the packet, with the function `start`, the argument of this function is a functional interface, which will be called if an exception occurs.
The `send` function in PreparedPacket automatically starts sending the packet.
Each PacketSender can only be used once.

**or**
```java
JSAServer server = new lu.poucy.jsa.server.JSAServer(3000, new char[] {1,2,3,4,5,6,7,8,9,0});
JSAClient client = new lu.poucy.jsa.client.JSAClient(3001, new char[] {1,2,3,4,5,6,7,8,9,0});

PreparedPacket ppacket = new PreparedPacket(new Packet(new ArrayList()), server.getHost(), server.getPort());

PacketSender sender = client.write(ppacket);
sender.start((exception) -> exception.printStackTrace());
```
The JSAServer or JSAClient classes contain a write function, the first and only argument is the PreparedPacket that will be sent. 
This function returns a PacketSender which allows to start sending the packet, with the function `start`, the argument of this function is a functional interface, which will be called if an exception occurs.
Each PacketSender can only be used once.

## Listeners

JSAListeners:
```java
public class CustomListener implements lu.poucy.jsa.utils.JSAListener {
	public void onPacketReceived(Packet packet) {
		System.out.println("packet received: "+packet);
	}

	public void onPacketSended(Packet packet) {
		System.out.println("packet sended: "+packet);
	}
}

JSAListener serverListener = new CustomListener();
JSAListener clientListener = new CustomListener();

JSAServer server = new lu.poucy.jsa.server.JSAServer(3000, new char[] {1,2,3,4,5,6,7,8,9,0});
JSAClient client = new lu.poucy.jsa.client.JSAClient(3001, new char[] {1,2,3,4,5,6,7,8,9,0});


server.registerListener(serverListener);
client.registerListener(clientListener);

new PreparedPacket(new Packet(new ArrayList()), server.getHost(), server.getPort()).send(client);

server.unregisterListener(serverListener);
client.unregisterListener(clientListener);
```
It is possible to add listeners to the servers/clients, the functions `registerListener` and `unregisterListener` are used to register or unregister JSAListeners.

## Create a custom packet

Create a custom packet:

**Exemple**
```java
public class CarPacket extends lu.poucy.jsa.packets.Packet {
	public CarPacket(Car car) {
		super(new ArrayList<Pair<String, Object>>());
		addArg(new Pair<String, Object>("name", car.getName()));
		addArg(new Pair<String, Object>("wheelCount", car.getWheelCount()));
	}
	public static Car {
		private String name;
		private int wheelCount;
		public Car(String name, int wheelCount) {
			this.name = name;
			this.weelCount = weelCount;
		}
		public String getName() {return this.name;}
		public String getWheelCount() {return this.wheelCount;}
	}
}

public class CustomListener implements lu.poucy.jsa.utils.JSAListener {
	public void onPacketReceived(Packet packet) {
		System.out.println("packet received: "+packet);
	}

	public void onPacketSended(Packet packet) {
		System.out.println("packet sended: "+packet);
	}
}

JSAListener serverListener = new CustomListener();
JSAListener clientListener = new CustomListener();

JSAServer server = new lu.poucy.jsa.server.JSAServer(3000, new char[] {1,2,3,4,5,6,7,8,9,0});
JSAClient client = new lu.poucy.jsa.client.JSAClient(3001, new char[] {1,2,3,4,5,6,7,8,9,0});

server.registerListener(serverListener);
client.registerListener(clientListener);

new PreparedPacket(new CarPacket(new Car("Poucy's car", 4)), server.getHost(), server.getPort()).send(client);
```
