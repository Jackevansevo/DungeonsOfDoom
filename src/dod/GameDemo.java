package dod;

/**
 * Class purely for demonstration purposes, allows users to run a server and set up
 * multiple clients from a pretty graphical user interface
 */
public class GameDemo {

    private static String mapChoice;
    private static int portNumber;
    private static int players;
    private static String hostName;

    /**
     * Main method creates a new instance of Game Demo
     * Allows Game Demo to be called from command line
     * @param args
     */
    public static void main(String[] args) {
        new GameDemo();
    }

    /**
     * Default constructor for Game Demonstration
     */
    public GameDemo() {
        // Create a new Game Dialog and get parameters from the user
        GameOptionsDialog gameOptionsDialog = new GameOptionsDialog();
        mapChoice = gameOptionsDialog.getMapChoice();
        players = gameOptionsDialog.getNumberOfPlayers();
        portNumber = gameOptionsDialog.getPortNumber();
        hostName = gameOptionsDialog.getHostName();
        // Don't bother making a game with no players
        if(players >=  1) {
            // Run the Dungeons of Doom server in a separate thread with user
            // specified port choice & map number
            new Thread(new DodServer(portNumber, mapChoice)).start();
            for(int i = 0; i < players; i++) {
                try {
                    // Pause between adding each client because we don't want to fry the CPU
                    Thread.sleep(2000);
                    System.out.println("Adding client " + i);
                    // Run each client in a new thread with user specified port number and hostname
                    // so they can connect to the newly created server instance
                    new Thread(new Client(portNumber, hostName)).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

