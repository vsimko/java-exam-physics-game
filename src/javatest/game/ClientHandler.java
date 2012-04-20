package javatest.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javatest.game.entities.BigRepulsor;
import javatest.game.entities.Entity;
import javatest.game.entities.GoalEntity;
import javatest.game.entities.PlayerEntity;
import javatest.game.entities.ProximityMine;

/**
 * Implementation of the communication with network clients
 * using a simple binary protocol.
 * 
 * @see Server
 * @author Viliam Simko
 */
public class ClientHandler extends Thread {

	private Socket clientSock;
	private World world;

	PlayerEntity playerEntity;

	public ClientHandler(Socket clientSock, World world) {
		this.clientSock = clientSock;
		this.world = world;
		playerEntity = this.world.playerStart.createNewPlayer();
	}

	// collecting interesting entities
	LinkedList<Entity> importantEntities = new LinkedList<Entity>();

	public void run() {
		System.out.println(String.format(
				"Communicating with the client: addr=%s, port=%s", clientSock
						.getInetAddress(), clientSock.getPort()));

		try {
			// prepare the input/output channels
			final DataOutputStream out = new DataOutputStream(clientSock
					.getOutputStream());
			final DataInputStream in = new DataInputStream(clientSock
					.getInputStream());

			for (Entity entity : world.getEntities()) {

				byte objType;
				if (entity instanceof GoalEntity)
					objType = Server.OBJTYPE_GOAL;
				else if (entity instanceof ProximityMine)
					objType = Server.OBJTYPE_MINE;
				else if (entity instanceof BigRepulsor)
					objType = Server.OBJTYPE_OTHER;
				else
					objType = Server.OBJTYPE_UNKNOWN;

				if (objType != Server.OBJTYPE_UNKNOWN) {
					importantEntities.add(entity);

					// sending to client
					out.writeByte(Server.MSG_SERVER_ENTITY_INFO);
					out.writeByte(objType);
					out.writeInt(importantEntities.indexOf(entity));
				}
			}

			Timer readingTimer = new Timer();
			readingTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						byte msgtype = in.readByte();
						int objId = in.readInt();

						switch (msgtype) {
						case Server.MSG_CLIENT_EVADE_ENTITY:
							playerEntity.evadeEntity(getObjFromId(objId));
							break;

						case Server.MSG_CLIENT_FOLLOW_ENTITY:
							playerEntity.followEntity(getObjFromId(objId));
							break;
						}
					} catch (Exception e1) {
						cancel();
					}
				}
			}, 0, 20);

			GoalEntity myGoal = null;

			while (!playerEntity.isDestroyed()) {
				try {
					// report the current goal if it has changed
					if (myGoal != playerEntity.getCurrentGoal()) {
						myGoal = playerEntity.getCurrentGoal();
						if (myGoal != null) {
							out.writeByte(Server.MSG_SERVER_CURRENT_GOAL);
							out.writeInt(getIdFromObj(myGoal));
						}
					}

					// report proximity warning
					Entity proximityWarning = playerEntity
							.getProximityWarning();
					if (proximityWarning != null) {
						out.writeByte(Server.MSG_SERVER_PROXIMITY_WARNING);
						out.writeInt(getIdFromObj(proximityWarning));
					}

					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		} finally {
			world.deleteEntity(playerEntity);
			try {
				clientSock.close();
			} catch (IOException e) {
			}
			System.out.println("Connection with client terminated.");
		}
	}

	synchronized private int getIdFromObj(Entity entity) {
		return importantEntities.indexOf(entity);
	}

	synchronized private Entity getObjFromId(int objId) {
		try {
			return importantEntities.get(objId);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

}