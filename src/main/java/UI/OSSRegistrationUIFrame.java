package UI;

import OSS.OSSRegistrationScript;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSSRegistrationUIFrame extends JFrame{
    public OSSRegistrationUIFrame(){
        // Create a new window
        //***************************************************************
        //                 INITIAL COMPONENTS FOR OSS
        //***************************************************************
        setTitle("OSS Account creation");
        JButton startButton = new JButton("Start Selenium - OSS creation");
        JCheckBox ossDemoCheckBox = new JCheckBox("Demo"); // Renamed to "Demo"
        JTextField GGIDInputField = new JTextField(12); // 12 columns wide
        JTextArea ossResultTextArea = new JTextArea(10, 25); // 10 rows and 25 columns
        ossResultTextArea.setEditable(false);
        // Update window icon
        ImageIcon uiIcon = new ImageIcon("resources/ui_icon.png");
        // Added a label to the input field
        JLabel VRNLabel = new JLabel("Gov Gateway ID: ");
        VRNLabel.setOpaque(true);
        //Add scroll to results text area if needed
        JScrollPane resultScrollPane = new JScrollPane(ossResultTextArea);
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
        GGIDInputField.setComponentPopupMenu(contextMenu);

        // Allow only copying from the results
        // Add copy
        JPopupMenu contextMenu2 = new JPopupMenu();
        JMenuItem copyMenuItem2 = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenuItem2.setText("Copy");
        contextMenu2.add(copyMenuItem2);
        ossResultTextArea.setComponentPopupMenu(contextMenu2);

        //***************************************************************
        //                 UPDATE UI FONT AND COLORS FOR OSS
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
        ossDemoCheckBox.setFont(customFont);
        VRNLabel.setFont(customFont);
        GGIDInputField.setFont(customFont);
        ossResultTextArea.setFont(new Font("Arial", Font.PLAIN, 17));

        // Update color schemes
        startButton.setBackground(buttonBackgroundColor);
        startButton.setForeground(customForegroundColor);

        ossDemoCheckBox.setBackground(customBackgroundColor);
        ossDemoCheckBox.setForeground(customForegroundColor);

        VRNLabel.setBackground(customBackgroundColor);
        VRNLabel.setForeground(customForegroundColor);

        GGIDInputField.setBackground(inputBackgroundColor);
        GGIDInputField.setForeground(inputForegroundColor);

        ossResultTextArea.setBackground(customBackgroundColor);
        ossResultTextArea.setForeground(customForegroundColor);
        ossResultTextArea.setBorder(BorderFactory.createLineBorder(new Color(173, 173, 180), 1));

        //***************************************************************
        //                          GGID Input
        //***************************************************************
        // Change background  of VRN input field as mouse hovers over it
        GGIDInputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                GGIDInputField.setBackground(new Color(212, 212, 217));
                GGIDInputField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                GGIDInputField.setBackground(new Color(173, 173, 180));
                GGIDInputField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
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
                boolean demoSelected = ossDemoCheckBox.isSelected();

                // Get the text from the Gateway ID input field
                String GGIDValue = GGIDInputField.getText().trim();

                //***************************************************************
                //                  EXECUTE THE SELENIUM SCRIPT
                //***************************************************************
                //Add all variables needed to the function before running here.
                // Validate GGIDValue
                if (isValidGGID(GGIDValue)) {
                    System.out.println("VALID GGID:" + GGIDValue);
                    String result = startSeleniumScript(demoSelected, GGIDValue);
                    ossResultTextArea.setText(result);
                } else {
                    System.out.println("Your GGID input: " + GGIDValue);
                    ossResultTextArea.setText("Invalid GGID." + "\n" + "It must be in the form 96 37 47 63 32 15");
                }
            }
        });
        //***************************************************************
        //                 ADD UI ELEMENTS TO OSS FRAME
        //***************************************************************
        // Set up UI elements
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(ossDemoCheckBox); // Renamed to "Demo"
        panel.add(VRNLabel); // Added the label
        panel.add(GGIDInputField);
        panel.add(startButton);
        panel.setBackground(customBackgroundColor);
        // Set up UI layout
        setIconImage(uiIcon.getImage());
        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    //***************************************************************
    //                 startSeleniumScript FUNCTION
    //                 RUN THE OSS CREATION SCRIPT
    //***************************************************************
    private String startSeleniumScript(boolean demoSelected, String VRNValue) {
        // Create an instance of the TestSeleniumScript class
        //TestSeleniumScript seleniumScript = new TestSeleniumScript();

        // Create an OSS creation selenium script
        OSSRegistrationScript seleniumScript = new OSSRegistrationScript();

        try {
            // Call the executeSeleniumScript method from the other class
            String result = seleniumScript.executeSeleniumScript(demoSelected, VRNValue, "GGPlaceholder", "BPPlaceholder");
            //return result that will update the text area
            return "OSSCreationSeleniumScript" +" script has run! " + "\n" + result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while running the Selenium script.";
        }
    }
    //***************************************************************
    //                     isValidGGID FUNCTION
    //***************************************************************
    public boolean isValidGGID(String input) {
        // Define a regular expression pattern for the desired format
        String regex = "\\d{2} \\d{2} \\d{2} \\d{2} \\d{2} \\d{2}";

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Use a Matcher to check if the input matches the pattern
        Matcher matcher = pattern.matcher(input);

        // Return true if it matches, false otherwise
        return matcher.matches();
    }
}
