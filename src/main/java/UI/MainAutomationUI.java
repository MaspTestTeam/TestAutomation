package UI;

import BTA.Prereqs.BTACreationWithOutlook;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainAutomationUI {
    private final JCheckBox demoCheckBox; // Renamed to "Demo"
    private final JTextField VRNInputField; // Added a label to the input field
    private final JTextArea resultTextArea;

    public MainAutomationUI() {
        //***************************************************************
        //                 INITIAL COMPONENTS
        //***************************************************************
        JFrame frame = new JFrame("Create BTA Account");
        JButton startButton = new JButton("Start Selenium Script");
        demoCheckBox = new JCheckBox("Demo"); // Renamed to "Demo"
        VRNInputField = new JTextField(12); // 12 columns wide
        resultTextArea = new JTextArea(10, 25); // 10 rows and 25 columns
        resultTextArea.setEditable(false);
        // Update window icon
        ImageIcon uiIcon = new ImageIcon("resources/ui_icon.png");
        // Added a label to the input field
        JLabel VRNLabel = new JLabel("VRN: ");
        VRNLabel.setOpaque(true);
        //Add scroll to results text area if needed
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //***************************************************************
        //                         CONTEXT MENUS
        //***************************************************************
        // Add copy
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenuItem.setText("Copy");
        contextMenu.add(copyMenuItem);
        // Add paste
        JMenuItem pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteMenuItem.setText("Paste");
        contextMenu.add(pasteMenuItem);
        // Add right click menu for copy and paste to input text and results field
        VRNInputField.setComponentPopupMenu(contextMenu);

        // Allow only copying from the results
        // Add copy
        JPopupMenu contextMenu2 = new JPopupMenu();
        JMenuItem copyMenuItem2 = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenuItem2.setText("Copy");
        contextMenu2.add(copyMenuItem2);
        resultTextArea.setComponentPopupMenu(contextMenu2);

        //***************************************************************
        //                      MENU BAR
        //***************************************************************
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        // Create a menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Create a menu item to open a new window titled 'OSS creation'
        JMenuItem openOSSCreation = new JMenuItem("Register OSS account");
        openOSSCreation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewOSSWindow();
            }
        });
        fileMenu.add(openOSSCreation);

        // Create a menu item to open a popup that shows VRNS
        JMenuItem openVRNSCreatedMenuItem = new JMenuItem("TODO: View VRNS available");
        openVRNSCreatedMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TODO: View VRNS available");
            }
        });
        fileMenu.add(openVRNSCreatedMenuItem);

        // Create a menu item to open a new window that shows accounts created
        JMenuItem openAccountsCreatedMenuItem = new JMenuItem("TODO: View Accounts Created");
        openAccountsCreatedMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TODO: View Accounts created");
            }
        });
        fileMenu.add(openAccountsCreatedMenuItem);

        // Create a menu item to open a new window that shows screenshots'
        JMenuItem openScreenshotsMenuItem = new JMenuItem("TODO: View Screenshots");
        openScreenshotsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TODO: View screenshots");
            }
        });
        fileMenu.add(openScreenshotsMenuItem);

        //***************************************************************
        //                 UPDATE UI FONT AND COLORS
        //***************************************************************
        Font customFont = new Font("Arial", Font.PLAIN, 14); // Replace with your preferred font
        // Set custom colors
        Color customBackgroundColor = new Color(28, 33, 72); // Replace with your preferred background color
        Color customForegroundColor = new Color(216, 246, 255); // Replace with your preferred foreground color
        Color inputForegroundColor = new Color(12, 12, 12); // Replace with your preferred foreground color
        Color inputBackgroundColor = new Color(173, 173, 180); // Replace with your preferred foreground color
        Color buttonBackgroundColor = new Color(37, 44, 105); // Replace with your preferred foreground color

        //Update Fonts
        startButton.setFont(customFont);
        demoCheckBox.setFont(customFont);
        VRNLabel.setFont(customFont);
        VRNInputField.setFont(customFont);
        resultTextArea.setFont(customFont);

        // Update color schemes
        startButton.setBackground(buttonBackgroundColor);
        startButton.setForeground(customForegroundColor);

        demoCheckBox.setBackground(customBackgroundColor);
        demoCheckBox.setForeground(customForegroundColor);

        VRNLabel.setBackground(customBackgroundColor);
        VRNLabel.setForeground(customForegroundColor);

        VRNInputField.setBackground(inputBackgroundColor);
        VRNInputField.setForeground(inputForegroundColor);

        resultTextArea.setBackground(customBackgroundColor);
        resultTextArea.setForeground(customForegroundColor);

        //***************************************************************
        //                          VRN Input
        //***************************************************************
        // Change background  of VRN input field as mouse hovers over it
        VRNInputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                VRNInputField.setBackground(new Color(212, 212, 217));
                VRNInputField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                VRNInputField.setBackground(new Color(173, 173, 180));
                VRNInputField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        });

        //***************************************************************
        //                 START BUTTON CLICKED
        //***************************************************************
        // Function to run once start begins.
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected state of the Demo check box
                boolean demoSelected = demoCheckBox.isSelected();

                // Get the text from the Gateway ID input field
                String VRNValue = VRNInputField.getText().trim();

                //***************************************************************
                //                  EXECUTE THE SELENIUM SCRIPT
                //***************************************************************
                //Add all variables needed to the function before running here.
                // Validate VRNValue
                if (isValidVRN(VRNValue)) {
                    String result = startSeleniumScript(demoSelected, VRNValue);
                    resultTextArea.setText(result);
                } else {
                    // Throw warning if VRN is not correct structure
                    JOptionPane.showMessageDialog(
                            frame, "Invalid VRN. It must be 9 digits long and contain only numbers.",
                            "Warning! Invalid input",
                            JOptionPane.WARNING_MESSAGE);
                    resultTextArea.setText("Invalid VRN." + "\n" + "It must be 9 digits long and contain only numbers.");
                }
            }
        });

        //***************************************************************
        //                 ADD UI ELEMENTS TO FRAME
        //***************************************************************
        // Set up UI elements
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(demoCheckBox); // Renamed to "Demo"
        panel.add(VRNLabel); // Added the label
        panel.add(VRNInputField);
        panel.add(startButton);
        panel.setBackground(customBackgroundColor);
        // Set up UI layout
        frame.setIconImage(uiIcon.getImage());
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(resultScrollPane, BorderLayout.CENTER);
        // Set up UI basic settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //***************************************************************
    //                 startSeleniumScript FUNCTION
    //***************************************************************
    private String startSeleniumScript(boolean demoSelected, String VRNValue) {

        //run the BTA script
        BTACreationWithOutlook seleniumScript = new BTACreationWithOutlook();

        try {
            // Call the executeSeleniumScript method from the other class
            // Placeholder BP number for now
            String result = seleniumScript.executeSeleniumScript(VRNValue, "123891322", demoSelected);

            return "Selenium script has run! " + "\n" + result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while running the Selenium script.";
        }
    }

    //***************************************************************
    //                 OSS CREATION WINDOW OPENS
    //***************************************************************
    private static void createNewOSSWindow() {
        OSSRegistrationUIFrame newWindow = new OSSRegistrationUIFrame();
        newWindow.setVisible(true);
    }

    //***************************************************************
    //                     isValidVRN FUNCTION
    //***************************************************************
    // Validate VRN is correct structure before running entire script
    private boolean isValidVRN(String gatewayIDValue) {
        return gatewayIDValue.matches("\\d{9}");
    }

    //***************************************************************
    //                     main FUNCTION
    //***************************************************************
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainAutomationUI();
            }
        });
    }
}
