package dod;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Client class creates a graphical user interface along with input
 * and output threads allowing the user to play
 * Gif describing my code: http://i.imgur.com/xU6ufSI.gif
 */
public class Client implements ActionListener, Runnable {

    private final String hostName;
    private final int portNumber;
    private JLabel turnIndicatorText = new JLabel("<html> YOUR TURN: <font color=red>FALSE</font></html>", SwingConstants.CENTER);
    private JFrame gameBoyFrame;
    private JPanel gameBoyContainer;
    private JPanel bottomPanel;
    private JPanel gameBoardPanel;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private BufferedImage armourImg;
    private BufferedImage blankImg;
    private BufferedImage floorImg;
    private BufferedImage goldImg;
    private BufferedImage healthImg;
    private BufferedImage lanternImg;
    private BufferedImage playerImg;
    private BufferedImage portalImg;
    private BufferedImage swordImg;
    private BufferedImage wallImg;
    private File tetrisSound = new File("resources/tetris.wav");
    private File tromboneSound = new File("resources/trombone.wav");

    /**
     * Client can be ran from command line with custom parameters to
     * connect to any given
     */
    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[0]);
        String hostName = args[1];
        new Thread(new Client(portNumber, hostName)).start();
    }

    /**
     * Every networked client needs to be connected to a server
     * @param portNumber the port number to connect to
     * @param hostName the host name to connect to
     */
    public Client(int portNumber,String hostName) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        // Setup the threads
        setUpThreads(this.hostName, this.portNumber);
        // Set the style of buttons to match native operating system theme
        setLookAndFeel();
        // Fetches images
        fetchImages();
        // Install custom fonts required for the game
        fetchFonts();
        // Create the parent frame that every element sits in
        drawGameBoyFrame();
        // Toggle for obnoxious sound effects
        SoundEffects.playSound(tetrisSound, 10);
        // Create a JPanel container to add all the GUI elements to
        drawGameBoyContainer();
        // Add some text showing the current turn status
        drawTurnIndicator();
        // Draw the initial game board
        drawGameBoard();
        // Add some text underneath the gameBoy showing turn indicator and game logo
        drawDungeonsOfDoomLogo();
        // Make a JPanel container for all the buttons underneath the game board
        drawBottomPanel();
        // Add the movement button panel
        drawMovementButtons();
        // Add the command button panel
        drawCommandButtons();
        // Add the gameBoyContainer JPanel to our window
        addGameBoyContainer();
    }

    /**
     * Creates an input and output thread for the client to interact with the server
     * @param hostName the host-name connect to
     * @param portNumber the port number to connect to
     */
    public void setUpThreads(String hostName, int portNumber) {
        Socket socket;
        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(socket.isClosed()) {
                System.err.println("Socket not open");
                System.exit(1);
            }
            Thread sendOutputThread = new Thread(new ClientSendOuputThread(socket, out, this));
            Thread readInputThread = new Thread(new ClientReadInputThread(socket, in, this));

            sendOutputThread.start();
            readInputThread.start();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
            System.exit(1);
        }
    }

    /**
     * Sets the look and feel of the Java Swing application depending
     * on the native operating system currently being used
     */
    public void setLookAndFeel() {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            // Deveopment was done on a Linux machine so used GTKLookAndFeel to make application look native
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                // One every other platform just use the cross platform Java Swing metal theme
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fetches the images when we start the game
     */
    public void fetchImages() {
        try {
            armourImg = ImageIO.read(new File("resources/armour.png"));
            blankImg = ImageIO.read(new File("resources/blank.png"));
            floorImg = ImageIO.read(new File("resources/floor.png"));
            goldImg = ImageIO.read(new File("resources/gold.png"));
            healthImg = ImageIO.read(new File("resources/health.png"));
            lanternImg = ImageIO.read(new File("resources/lantern.png"));
            playerImg = ImageIO.read(new File("resources/player.png"));
            portalImg = ImageIO.read(new File("resources/portal.png"));
            swordImg = ImageIO.read(new File("resources/sword.png"));
            wallImg = ImageIO.read(new File("resources/wall.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempt to set custom fonts for use in the Graphical User interface
     */
    public void fetchFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Early GameBoy.ttf")));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * Draws the game boy frame
     */
    public void drawGameBoyFrame() {
        gameBoyFrame = new JFrame("Dungeons of Doom");
        gameBoyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameBoyFrame.setPreferredSize(new Dimension(450, 720));
        gameBoyFrame.setResizable(false);
    }

    /**
     * Draws the game boy container
     */
    public void drawGameBoyContainer() {
        gameBoyContainer = new JPanel();
        gameBoyContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gameBoyContainer.setBackground(new Color(81, 55, 170));
        gameBoyContainer.setLayout(new BoxLayout(gameBoyContainer, BoxLayout.Y_AXIS));
    }

    /**
     * Draws the turn indicator
     */
    public void drawTurnIndicator() {
        JPanel turnIndicatorTextPanel = new JPanel();
        turnIndicatorTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        turnIndicatorTextPanel.setBackground(Color.black);
        turnIndicatorText.setFont(new Font("Early Gameboy", Font.PLAIN, 11));
        turnIndicatorText.setToolTipText("Indicates if it's your turn or not");
        turnIndicatorTextPanel.add(turnIndicatorText);
        gameBoyContainer.add(turnIndicatorTextPanel);
    }

    /**
     * Draws the game board panel
     */
    public void drawGameBoard() {
        gameBoardPanel = new JPanel(new GridLayout(7, 7));
        gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        gameBoyContainer.add(gameBoardPanel);
        gameBoardPanel.setBackground(Color.black);
        gameBoardPanel.setToolTipText("The game board");
        out.println("LOOK");
    }

    /**
     * Updates the game board
     */
    public void updateGameBoard(char[][] lookReply) {
        gameBoardPanel.removeAll();
        for (int x = 0; x < 7; x++) {
            for(int y = 0; y < 7; y++) {
                switch(lookReply[y][x]) {
                    case 'A':
                        gameBoardPanel.add(new JLabel((new ImageIcon(armourImg))));
                        break;
                    case 'X':
                        gameBoardPanel.add(new JLabel((new ImageIcon(blankImg))));
                        break;
                    case '.':
                        gameBoardPanel.add(new JLabel((new ImageIcon(floorImg))));
                        break;
                    case 'G':
                        gameBoardPanel.add(new JLabel((new ImageIcon(goldImg))));
                        break;
                    case 'H':
                        gameBoardPanel.add(new JLabel((new ImageIcon(healthImg))));
                        break;
                    case 'L':
                        gameBoardPanel.add(new JLabel((new ImageIcon(lanternImg))));
                        break;
                    case 'P':
                        gameBoardPanel.add(new JLabel((new ImageIcon(playerImg))));
                        break;
                    case 'E':
                        gameBoardPanel.add(new JLabel((new ImageIcon(portalImg))));
                        break;
                    case 'S':
                        gameBoardPanel.add(new JLabel((new ImageIcon(swordImg))));
                        break;
                    case '#':
                        gameBoardPanel.add(new JLabel((new ImageIcon(wallImg))));
                        break;
                    default:
                        // Shown a wall image if we can't find the tile
                        gameBoardPanel.add(new JLabel((new ImageIcon(wallImg))));
                        break;
                }
            }
        }
        gameBoardPanel.updateUI();
    }

    /**
     * draw the game logo
     */
    public void drawDungeonsOfDoomLogo() {
        JLabel dungeonsOfDoomText = new JLabel("Dungeons Of Doom", JLabel.CENTER);
        dungeonsOfDoomText.setFont(new Font("Early GameBoy", Font.PLAIN, 14));
        JPanel dungeonsOfDoomTextPanel = new JPanel();
        dungeonsOfDoomTextPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        dungeonsOfDoomTextPanel.setLayout(new GridLayout(0, 1));
        dungeonsOfDoomTextPanel.setBackground(Color.black);
        dungeonsOfDoomTextPanel.add(dungeonsOfDoomText);
        gameBoyContainer.add(dungeonsOfDoomTextPanel);
    }

    /**
     * draw the bottom panel
     */
    public void drawBottomPanel() {
        bottomPanel = new JPanel(new GridLayout(0, 2));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 20, 10));
        bottomPanel.setBackground(new Color(81, 55, 170));
        gameBoyContainer.add(bottomPanel);
    }

    /**
     * draw the movements buttons
     */
    public void drawMovementButtons() {
        JPanel movementButtonPanel = new JPanel(new GridBagLayout());
        movementButtonPanel.setBackground(new Color(81, 55, 170));
        // Set the default button size
        Dimension defaultButtonSize = new Dimension(50, 50);
        GridBagConstraints gbc = new GridBagConstraints();
        // Up button
        gbc.gridx = 1; gbc.gridy = 0;
        JButton upButton = new JButton("↑");
        upButton.setPreferredSize(defaultButtonSize);
        movementButtonPanel.add(upButton, gbc);
        upButton.addActionListener(this);
        upButton.setActionCommand("MOVE N");
        upButton.setToolTipText("Clicking this button during your turn will move player up");
        // Down button
        gbc.gridx = 1; gbc.gridy = 2;
        JButton downButton = new JButton("↓");
        downButton.setPreferredSize(defaultButtonSize);
        movementButtonPanel.add(downButton, gbc);
        downButton.addActionListener(this);
        downButton.setActionCommand("MOVE S");
        downButton.setToolTipText("Clicking this button during your turn will move player down");
        // Left button
        gbc.gridx = 0; gbc.gridy = 1;
        JButton leftButton = new JButton("←");
        leftButton.setPreferredSize(defaultButtonSize);
        movementButtonPanel.add(leftButton, gbc);
        leftButton.addActionListener(this);
        leftButton.setActionCommand("MOVE W");
        leftButton.setToolTipText("Clicking this button during your turn will move player left");
        // Right button
        gbc.gridx = 2; gbc.gridy = 1;
        JButton rightButton = new JButton("→");
        rightButton.setPreferredSize(defaultButtonSize);
        movementButtonPanel.add(rightButton, gbc);
        rightButton.addActionListener(this);
        rightButton.setActionCommand("MOVE E");
        rightButton.setToolTipText("Clicking this button during your turn will move player right");
        bottomPanel.add(movementButtonPanel);
    }

    /**
     * draw the command buttons
     */
    public void drawCommandButtons() {
        JPanel commandButtonPanel = new JPanel(new GridLayout(2, 2));
        commandButtonPanel.setBackground(new Color(81, 55, 170));
        // Look button
        JButton lookButton = new JButton("Look");
        lookButton.addActionListener(this);
        lookButton.setActionCommand("LOOK");
        commandButtonPanel.add(lookButton);
        // Hello button
        JButton chatButton = new JButton("Chat");
        chatButton.addActionListener(this);
        chatButton.setActionCommand("CHAT");
        commandButtonPanel.add(chatButton);
        // Attack button
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(this);
        quitButton.setActionCommand("QUIT");
        commandButtonPanel.add(quitButton);
        // Pickup button
        JButton pickupButton = new JButton("Pickup");
        pickupButton.addActionListener(this);
        pickupButton.setActionCommand("PICKUP");
        commandButtonPanel.add(pickupButton);
        // Add all button elements to the bottom Panel
        bottomPanel.add(commandButtonPanel);
    }

    /**
     * Adds the game boy container to the window frame, packs the elements
     * and makes the window visible
     */
    public void addGameBoyContainer() {
        gameBoyFrame.add(gameBoyContainer);
        gameBoyFrame.pack();
        gameBoyFrame.setVisible(true);
    }

    /**
     * Mutator to modify the turn indicator status above the screen
     * @param Turn boolean is it the users turn?
     */
    public void updateTurnIndicator(boolean Turn) {
        turnIndicatorText.repaint();
        if(Turn) {
            // Change the gui text to true
            turnIndicatorText.setText("<html> YOUR TURN: <font color=white>TRUE</font></html>");
        } else {
            // Change the gui text to false
            turnIndicatorText.setText("<html> YOUR TURN: <font color=red>FALSE</font></html>");
        }
        turnIndicatorText.updateUI();
    }


    /**
     * Processes command when buttons are pressed
     * @param action name of command to be carried out
     */
    public void actionPerformed(ActionEvent action) {
        // Always run a look command after each command
        if(action.getActionCommand().equals("CHAT")) {
            SoundEffects.playSound(tromboneSound, 1);
            showPopUpMessage("This feature hasn't been implemented :(");
        } else if(action.getActionCommand().equals("QUIT")) {
            System.exit(1);
        } else {
            out.println(action.getActionCommand());
            if(!action.getActionCommand().equals("LOOK")) {
                out.println("LOOK");
            }
        }
    }

    /**
     * Method to send a look command after a change is detected by the server
     */
    public void sendLook() {
        out.println("LOOK");
    }

    /**
     * Creates a pop-up dialog with cutom message to be displayed on the screen
     * @param message to be displayed in the dialog
     */
    public void showPopUpMessage(String message) {
        JFrame popUpFrame = new JFrame();
        //custom title, custom icon
        JOptionPane.showMessageDialog(popUpFrame, message, "Dungeons of Doom", JOptionPane.INFORMATION_MESSAGE, null);
    }
}
