package dod;

import dod.game.GameLogic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.ParseException;

/**
 *  Class to run the dungeons of Doom Server
 */
public class DodServer implements Runnable {

	public static int numberOfPlayers = 0;
	public static boolean listening = true;
	private static int portNumber;
	private static String mapChoice;

	/**
	 *  Main method ran when the user runs DodServer from command line
	 * allows them to specify where the server is run by changing the Port and Hostname
	 */
	public static void main(String[] args) {
		portNumber = Integer.parseInt(args[0]);
		mapChoice = args[1];
		new Thread(new DodServer(portNumber, mapChoice)).start();
	}

	/**
	 * Default constructor for Dungeons of Doom Server
	 * @param portNumber
	 * @param mapChoice
	 */
	public DodServer(int portNumber, String mapChoice) {
		this.portNumber = portNumber;
		this.mapChoice = mapChoice;
	}

	public void run() {
		// Create an instance of GameLogic, which effectively starts the game
		GameLogic game = null;
		try {
			game = new GameLogic("maps/" + mapChoice);
		} catch (FileNotFoundException | ParseException e) {
			System.out.println("Map: " + mapChoice + " not found");
			System.exit(1);
		}
		// Start listening on a port and wait for connections
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			System.out.println("Server running, details:" + "\nPort Number: " + portNumber + "\nMap: " + mapChoice);
			System.out.println("Waiting for connections...");
			while (listening) {
				// For each client that connects make a separate thread
				new Thread(new DodServerThread(game, this, serverSocket.accept())).start();
				// Print some helpful information to the server
				System.out.println("Made a connection");
				playerJoinedMessage();
				// Increment the number of players by one
				incrementNumberOfPlayers(1);
			}
		} catch (IOException e) {
			// Catch any errors where client was unable to connect
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

	/**
	 * Mutator to alter the numberOfPlayers variable
	 */
	public void incrementNumberOfPlayers(int x) {
		numberOfPlayers = numberOfPlayers + x;
		System.out.println("No.Players: " + numberOfPlayers);
	}

	/**
	 *  Method to print that a player has left the game
	 */
	public void playerLeftMessage() {
		System.out.println("Player has left");
	}

	/**
	 * Method to print that a player has joined the game
	 */
	public void playerJoinedMessage() {
		System.out.println("Player has joined");
	}
}
