package App;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author jcvsa
 */
public class App {

    public static void main(String[] args) {

        Client client = new Client();

        SwingUtilities.invokeLater(() -> new App(client));
    }

    private final Client client;
    private JFrame frame;
    private JTextPane chatArea;
    private DefaultListModel<String> allUsers;

    /**
     * Constants
     */
    private final String FONT = "";

    /**
     * 
     */
    public final static SimpleAttributeSet ATTR
            = new SimpleAttributeSet();

    /**
     * 
     * @param client 
     */
    public App(Client client) {

        loadFrame();

        this.client = client;
        this.client.connectInterface(this);
    }

    /**
     * 
     */
    private void loadFrame() {
        frame = new JFrame("DSM Assignment 2 - ChatRoom");

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(loadRightPanel(), BorderLayout.CENTER);
        container.add(loadLeftPanel(), BorderLayout.WEST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * 
     * @return 
     */
    private JPanel loadLeftPanel() {
        GridBagConstraints userConstraints = new GridBagConstraints();
        userConstraints.weightx = 1;
        userConstraints.weighty = 1;
        userConstraints.gridx = 0;
        userConstraints.gridy = 1;
        userConstraints.fill = GridBagConstraints.BOTH;

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.weightx = 1;
        buttonConstraints.weighty = 0;
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 2;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.anchor = GridBagConstraints.PAGE_END;

        JPanel left = new JPanel(new GridBagLayout());
        left.add(loadUserPanel(), userConstraints);
        left.add(loadButtonPanel(), buttonConstraints);

        return left;
    }

    /**
     * 
     * @return 
     */
    private JPanel loadUserPanel() {
        // Title.
        JLabel label = new JLabel("Current Users", JLabel.CENTER);
        label.setFont(new Font(FONT, Font.BOLD, 12));
        JPanel panelMargin = new JPanel(); 
        panelMargin.setBorder(new EmptyBorder(0, 0, 20, 0));
        panelMargin.add(label);

        // User names.
        this.allUsers = new DefaultListModel<>();
        JList<String> list = new JList<>(this.allUsers);
        list.setBorder(new EmptyBorder(40, 40, 40, 20));
        list.setFont(new Font(FONT, Font.PLAIN, 12));
        list.setVisibleRowCount(8);

        JScrollPane scrollPane = new JScrollPane(list);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(40, 40, 20, 20));
        panel.add(panelMargin, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 
     * @return 
     */
    private JPanel loadButtonPanel() {
        JButton connect = new JButton("Connect");
        JButton disconnect = new JButton("Disconnect");

        // Connect button.
        connect.setFont(new Font(FONT, Font.BOLD, 12));
        connect.addActionListener(onConnect(connect, disconnect));
        connect.setEnabled(true);
        // Disconnect button.
        disconnect.setFont(new Font(FONT, Font.BOLD, 12));
        disconnect.addActionListener(onDisconnection(connect, disconnect));
        disconnect.setEnabled(false);
        // Connect button.
        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.weightx = 0.5;
        constraints1.weighty = 0;
        constraints1.gridx = 0;
        constraints1.gridy = 0;
        constraints1.fill = GridBagConstraints.HORIZONTAL;
        // Disconnect button.
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.weightx = 0.5;
        constraints2.weighty = 0;
        constraints2.gridx = 1;
        constraints2.gridy = 0;
        constraints2.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 40, 40, 20));
        panel.add(connect, constraints1);
        panel.add(disconnect, constraints2);

        return panel;
    }

    /**
     * 
     * @return 
     */
    private JPanel loadRightPanel() {

        GridBagConstraints chatConstraints = new GridBagConstraints();
        chatConstraints.weightx = 1;
        chatConstraints.weighty = 1;
        chatConstraints.gridx = 0;
        chatConstraints.gridy = 0;
        chatConstraints.fill = GridBagConstraints.BOTH;

        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.weightx = 1;
        inputConstraints.weighty = 0;
        inputConstraints.gridx = 0;
        inputConstraints.gridy = 1;
        inputConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputConstraints.anchor = GridBagConstraints.PAGE_END;

        JPanel right = new JPanel(new GridBagLayout());
        right.add(loadChatPanel(), chatConstraints);
        right.add(loadInputPanel(), inputConstraints);

        return right;

    }

    /**
     * 
     * @return 
     */
    private JPanel loadChatPanel() {
        // Message list.
        chatArea = new JTextPane();
        chatArea.setMargin(new Insets(20, 20, 20, 20));
        chatArea.setFont(new Font(FONT, Font.PLAIN, 12));
        chatArea.setEditable(false);
        postMessage("DSM Assignment 2 - Chatroom.\n"
                + "log in by clicking on connect on the bottom left.\n\n");

        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(40, 20, 40, 40));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 
     * @return 
     */
    private JPanel loadInputPanel() {
        // User input.
        JTextField textField = new JTextField();
        textField.setMargin(new Insets(20, 20, 20, 20));
        textField.setFont(new Font(FONT, Font.PLAIN, 12));
        textField.addActionListener(onSend(textField));
        // Send button.
        JButton button = new JButton("Send");
        button.setFont(new Font(FONT, Font.BOLD, 12));
        button.addActionListener(onSend(textField));

        // User input
        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.weightx = 0.9;
        constraints1.weighty = 0;
        constraints1.gridx = 0;
        constraints1.gridy = 0;
        constraints1.fill = GridBagConstraints.HORIZONTAL;
        // Send button. 
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.weightx = 0.1;
        constraints2.weighty = 0;
        constraints2.gridx = 1;
        constraints2.gridy = 0;
        constraints2.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 20, 40, 40));
        panel.add(textField, constraints1);
        panel.add(button, constraints2);

        return panel;
    }

    /**
     * 
     * @param textField
     * @return 
     */
    private ActionListener onSend(JTextField textField) {
        return e -> {
            if (!client.isConnected()) {
                postMessage("[Server]: Please log in to "
                        + "send messages.\n");
                textField.setText("");
                return;
            }

            String input = textField.getText();

            if (input != null && !input.isEmpty()) {
                if (!client.isConnected()) {
                    client.connectUser(input);
                } else {
                    client.sendMessage(input);
                }

                textField.setText("");
            }
        };
    }

    /**
     * 
     * @param connectButton
     * @param disconnectButton
     * @return 
     */
    private ActionListener onConnect(JButton connectButton, JButton disconnectButton) {
        return e -> {
            String name = JOptionPane.showInputDialog(frame,
                    "Enter your name:", "Connect",
                    JOptionPane.PLAIN_MESSAGE);

            if (name != null) {
                if (!name.isEmpty()) {
                    if (client.connectUser(name)) {
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                    }
                } else {
                    postMessage("[Server]: Error on connection. "
                            + "Fields were empty.\n");
                }
            }
        };
    }

    /**
     * 
     * @param connectButton
     * @param disconnectButton
     * @return 
     */
    private ActionListener onDisconnection(JButton connectButton,
            JButton disconnectButton) {
        return e -> {
            int input = JOptionPane.showConfirmDialog(frame,
                    "Click ok to log out.", "Disconnect",
                    JOptionPane.DEFAULT_OPTION);

            if (input == 0) {
                client.disconnectUser();
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
            }
        };
    }

    /**
     * 
     * @param message
     * @param attributes 
     */
    public void postMessage(String message) {
        Document doc = chatArea.getDocument();
        try {
            doc.insertString(doc.getLength(), message, ATTR);
        } catch (Exception e) {
        }

        chatArea.setCaretPosition(doc.getLength());
    }

    /**
     * 
     * @param name 
     */
    public void addToAllUsers(String name) {
        this.allUsers.addElement(name);
    }

    /**
     * 
     * @param name 
     */
    public void removeFromAllUsers(String name) {
        this.allUsers.removeElement(name);
    }

    /**
     * 
     */
    public void clearAllUsers() {
        this.allUsers.clear();
    }

}
