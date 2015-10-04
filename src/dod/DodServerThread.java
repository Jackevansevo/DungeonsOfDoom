package dod;

import dod.game.GameLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *  Class to create a new thread each time there is a new connection to the server
 */
public class DodServerThread extends CommandLineUser {

	private Socket socket = null;
	private PrintWriter out;
	private BufferedReader in;
	private DodServer dodServer;

	/**
	 * Default constructor for the Server Thread
	 */
	DodServerThread(GameLogic game, DodServer dodServer, Socket socket) {
		super(game);
		this.socket = socket;
		this.dodServer = dodServer;
		setUpIOStreams();
		addPlayer();
	}

	/**
	 * Simple method to set up input and output streams
	 */
	private void setUpIOStreams() {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method is ran after the constructor method
	 */
	public void run() {
		boolean listening = true;
		// Listen for input from the client
		while (listening) {
			try {
				String command = in.readLine();
				// Handle case where client quits the game
				if (command == null) {
                    processCommand("ENDTURN");
					this.removePlayer();
					dodServer.playerLeftMessage();
					dodServer.incrementNumberOfPlayers(-1);
					listening = false;
					continue;
				} processCommand(command);
			} catch (IOException e) {
				// Catch any errors
				e.printStackTrace();
			}
		}
	}

	/**
	 * Prints the output of the game to the terminal
	 */
	protected void doOutputMessage(String message) {
		out.println(message);
	}
}
