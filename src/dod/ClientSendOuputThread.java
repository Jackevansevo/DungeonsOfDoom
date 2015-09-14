package dod;

import java.io.*;
import java.net.Socket;

public class ClientSendOuputThread implements Runnable {
    private final PrintWriter out;
    private final Client client;
    private Socket socket = null;
    BufferedReader stdIn = null;

    /**
     * Default constructor for the ClientOutput Thread
     */
    ClientSendOuputThread(Socket socket, PrintWriter out, Client client) {
        this.socket = socket;
        this.out = out;
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        this.client = client;
    }

    /**
     *  Creates a graphical user interface and allowing the client to play the game in the gui
     *  or command line
     */
    public void run() {
        while(true) {
            try {
                while ((stdIn.ready())) {
                    String command = stdIn.readLine();
                    out.println(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
