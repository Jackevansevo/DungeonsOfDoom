package dod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread allows Client to reads in all incoming messages from the server
 * and handle accordingly
 */
public class ClientReadInputThread implements Runnable {

    private final BufferedReader in;
    private final Socket socket;
    private final Client client;
    private char[][] lookReply = new char[7][7];
    private File applauseSound = new File("resources/applause.wav");
    private File tromboneSound = new File("resources/trombone.wav");
    private File coinDropSsound = new File("resources/coindrop.wav");

    /**
     * Default constructor the ClientOutput Thread
     */
    ClientReadInputThread(Socket socket, BufferedReader in, Client client) {
        this.socket = socket;
        this.in = in;
        this.client = client;
    }

    /**
     * Method is ran after the constructor method
     */
    public void run() {
        while (true) {
            try {
                while((in.ready())) {
                    String command = in.readLine();
                    // If the command is a lookReply we need to parse it so we can update the map
                    if (command.startsWith("LOOKREPLY")) {
                        buildLookReply(in);
                        client.updateGameBoard(lookReply);
                    } else if (command.startsWith("WIN")) {
                        // Inform the player that they have won the game and celebrate with applause
                        SoundEffects.playSound(applauseSound, 1);
                        client.showPopUpMessage("YOU WON");
                    } else if (command.startsWith("LOSE")) {
                        // Inform the player that they have lost the game and mock them with a sad trombone sound
                        SoundEffects.playSound(tromboneSound, 1);
                        client.showPopUpMessage("YOU LOST");

                    } else if (command.startsWith("ENDTURN")) {
                        // If their is one player in the game the next immediate command will be STARTURN
                        if(in.readLine().startsWith("STARTTURN")) {
                            // If there is only one player don't bother changing the turn indicator
                            client.updateTurnIndicator(true);
                        } else {
                            // Tell the player their turn is over
                            client.updateTurnIndicator(false);
                        }
                    } else if (command.startsWith("STARTTURN")) {
                        // Set the turn indicator to true
                        client.updateTurnIndicator(true);
                    } else if (command.startsWith("CHANGE")) {
                        // If a change message is received the we need to send a look message so the server
                        client.sendLook();

                    } else if (command.startsWith("FAIL")) {
                        client.showPopUpMessage(command);
                    } else if (command.startsWith("TREASUREMOD")) {
                        // That means that they have just picked up some treasure
                        SoundEffects.playSound(coinDropSsound, 1);

                    }   else {
                        System.out.println(command);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buildLookReply(BufferedReader in) {
        try {
            // Skip over the first line
            String line = in.readLine();
            if (line.length() < 7) {
                // Then they don't have a lantern so we need to pad out the output to fill
                // the 7x7 array
                for (int x = 1; x <= 5; x++) {
                    line = "X" + line + "X";
                    int y = 0;
                    for (char e : line.toCharArray()) {
                        lookReply[y][x] = e;
                        y++;
                    }
                    line = this.in.readLine();
                }
                // Then deal with the missing top and bottom lines
                int z = 0;
                for (char e : "XXXXXXX".toCharArray()) {
                    lookReply[z][6] = e;
                    lookReply[z][0] = e;
                    z++;
                }
            } else {
                // Then they have a lantern so we don't need to worry about padding
                for (int x = 0; x < 7; x++) {
                    int y = 0;
                    for (char e : line.toCharArray()) {
                        lookReply[y][x] = e;
                        y++;
                    }
                    line = this.in.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to build lookReply");
        }
    }
}
