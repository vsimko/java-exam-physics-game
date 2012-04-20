package javatest.game;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Creates a TCP server waiting for connections from network clients. After a
 * client is connected the communication is handles in a separate thread using
 * the ClientHandler class.
 * 
 * @see ClientHandler
 * 
 * @author Viliam Simko
 */
public class Server {

	static World world;

	static final public byte MSG_SERVER_ENTITY_INFO = 0;
	static final public byte MSG_SERVER_CURRENT_GOAL = 1;
	static final public byte MSG_SERVER_PROXIMITY_WARNING = 2;
	static final public byte MSG_CLIENT_FOLLOW_ENTITY = 3;
	static final public byte MSG_CLIENT_EVADE_ENTITY = 4;

	static final public byte OBJTYPE_UNKNOWN = 0;
	static final public byte OBJTYPE_MINE = 1;
	static final public byte OBJTYPE_GOAL = 2;
	static final public byte OBJTYPE_OTHER = 3;

	static final private int DEFAULT_PORT = 20000;

	private static void printUsage() {
		System.out.println("Usage: java -jar server.jar [port]");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			World world = new World();
			System.out.println("World created.");

			ServerSocket server;
			if (args.length == 1) {
				int port = Integer.parseInt(args[0]);
				server = new ServerSocket(port);
			} else {
				printUsage();
				server = new ServerSocket(DEFAULT_PORT);
			}

			while (true) {
				System.out.printf("Waiting for clients on port %d ...\n",
						server.getLocalPort());
				Socket clientSock = server.accept();
				ClientHandler ch = new ClientHandler(clientSock, world);

				// handle the connection in a separate thread
				// the main thread will continue accepting connections from
				// other clients
				ch.start();
			}

		} catch (Exception e) {
			System.err.println("Server error: " + e);
			e.printStackTrace();
			System.exit(2);
		}
	}

}
