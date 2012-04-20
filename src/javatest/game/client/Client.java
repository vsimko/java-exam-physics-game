package javatest.game.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javatest.game.Server;

/**
 * Example implementation of a network client.
 * 
 * @author Viliam Simko
 */
public class Client {

	private static void printUsage() {
		System.out.println("Usage: java -jar client.jar [address] [port]");
		System.exit(1);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {

		if (args.length != 2)
			printUsage();

		String address = args[0];
		int port = 0;

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printUsage();
		}

		System.out.printf("Trying to connect to the server %s:%d ...\n",
				address, port);

		Socket sock;
		try {
			sock = new Socket(address, port);
			if (!sock.isConnected())
				throw new ConnectException();

			System.out.println("Connected.");

			// input/output for communication with the server
			final DataOutputStream out = new DataOutputStream(sock
					.getOutputStream());
			final DataInputStream in = new DataInputStream(sock
					.getInputStream());

			/**
			 * Holds information about goals and game status.
			 * @author Viliam Simko
			 */
			class MyContext {
				private int currentGoal;

				Hashtable<Integer, Byte> objIdToType = new Hashtable<Integer, Byte>();

				synchronized public void addEntityInfo(byte objType, int objId) {
					objIdToType.put(objId, objType);
				}

				synchronized void setCurrentGoal(int currentGoal) {
					this.currentGoal = currentGoal;
				}

				synchronized public int getCurrentGoal() {
					return currentGoal;
				}

				synchronized public boolean isInDanger() {
					return proximityCounter > 0;
				}
				
				private int proximityObjId = -1;
				private int proximityCounter = 0;
				synchronized public void addProximityWarning(int objId) {
					proximityObjId = objId;
					proximityCounter = 10;
				}

				synchronized public int getEvadeObjectId() {
					return proximityObjId;
				}

				synchronized public int getFollowObjectId() {
					return getCurrentGoal();
				}

				synchronized public void updateProximity() {
					proximityCounter--;
				}
			};

			final MyContext context = new MyContext();

			// handle messages FROM THE SERVER in a separate thread
			Thread readerThread = new Thread() {
				@Override
				public void run() {
					try {
						while (true) {
							byte msgtype = in.readByte();
							switch (msgtype) {
							case Server.MSG_SERVER_ENTITY_INFO:
								context.addEntityInfo(in.readByte(), in
										.readInt());
								break;
							case Server.MSG_SERVER_CURRENT_GOAL:
								context.setCurrentGoal(in.readInt());
								break;
							case Server.MSG_SERVER_PROXIMITY_WARNING:
								context.addProximityWarning(in.readInt());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println(e);
					}
				}
			};
			readerThread.start();

			// send messages TO THE SERVER in the main thread 
			while (true) {
				if (context.isInDanger()) {
					out.writeByte(Server.MSG_CLIENT_EVADE_ENTITY);
					out.writeInt(context.getEvadeObjectId());
				} else {
					out.writeByte(Server.MSG_CLIENT_FOLLOW_ENTITY);
					out.writeInt(context.getFollowObjectId());
				}
				context.updateProximity();
				Thread.sleep(20);
			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("client done.");
	}
}