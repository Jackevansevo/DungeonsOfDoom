package dod;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

/**
 * Graphical user interface allows user to select the map, number of players and the port to
 * be run on, purely for demonstration purposes
 */
public class GameOptionsDialog extends JFrame  {

    private final JTextField hostNameField;
    private String[] mapFileNames;
    private JSpinner numberOfPlayersSpinner;
    private JComboBox<String> mapsDropDownBox;
    private JTextField portNumberField;

    /**
     * Temporary test method for this class
     */
    public static void main(String[] args) {
        new GameOptionsDialog();
    }

    /**
     * Default constructor for GameOptionsDialog
     */
    public GameOptionsDialog() {
        setLookAndFeel();

        JPanel optionsDialogPanel = new JPanel(new GridLayout(4, 2));

        // Add player number field
        optionsDialogPanel.add(new JLabel("Select number of players:"));
        optionsDialogPanel.add(numberOfPlayersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5,1 )));

        // Add map name field
        optionsDialogPanel.add(new JLabel("Select map:"));
        mapFileNames = new File("maps/").list();
        optionsDialogPanel.add(mapsDropDownBox = new JComboBox(mapFileNames));
        mapsDropDownBox.addItem("Random Map");
        mapsDropDownBox.setSelectedItem("Random Map");

        // Add port number field
        optionsDialogPanel.add(new JLabel("Enter port number:"));
        optionsDialogPanel.add(portNumberField = new JTextField());
        portNumberField.setText("4444");

        // Add hostname field
        optionsDialogPanel.add(new JLabel("Enter hostname:"));
        optionsDialogPanel.add(hostNameField = new JTextField());
        hostNameField.setText("127.0.0.1");

        JOptionPane.showConfirmDialog(null, optionsDialogPanel, "Dungeons Of Doom Options Panel", JOptionPane.OK_CANCEL_OPTION);

    }

    /**
     * sets the look and feel of the Java Swing application depending
     * on the native operating system currently being used
     */
    public void setLookAndFeel() {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *  Accessor for mapChoice
     */
    public String getMapChoice() {
        if(mapsDropDownBox.getSelectedItem() == "Random Map") {
            int randomIndex = new Random().nextInt(mapFileNames.length);
            return mapFileNames[randomIndex];
        } return (String) mapsDropDownBox.getSelectedItem();
    }

    /**
     * Accessor for numberOfPlayers
     */
    public int getNumberOfPlayers() {
        return (int) numberOfPlayersSpinner.getValue();
    }

    /**
     * Accessor for portNumber
     */
    public int getPortNumber() {
        try {
            return Integer.parseInt(portNumberField.getText());
        } catch (NumberFormatException e) {
            return 4444;
        }
    }

    /**
     * Accessor for hostName
     */
    public String getHostName() {
        return hostNameField.getText();
    }
}
