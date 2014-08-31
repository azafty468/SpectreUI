package spectre.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;

/**
 * Created by andrewzafft on 6/6/14.
 */
public class CommandOutJPanel extends JPanel {
    private JTextArea myCommandWindow;
    private String currentLog;
    private static final String newline = "\n";

    public CommandOutJPanel(int newLeft, int newTop, int newWidth, int newHeight) {
        setLayout(new BorderLayout());
        setBounds(newLeft, newTop, newWidth, newHeight);

        myCommandWindow = new JTextArea(500, 60);
        ((AbstractDocument) myCommandWindow.getDocument()).setDocumentFilter(new CommandOutFilter(myCommandWindow, 55));

        myCommandWindow.setLineWrap(true);
        myCommandWindow.setWrapStyleWord(true);
        myCommandWindow.setEditable(false);
        myCommandWindow.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(myCommandWindow);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new java.awt.Dimension(newWidth, newHeight));
        add(BorderLayout.CENTER, scrollPane);

        currentLog = "Initialized Log";
        myCommandWindow.setText(currentLog);
    }

    public void displayMessage(String message) {
        currentLog += newline + message;
        myCommandWindow.append(newline + message);
        myCommandWindow.setCaretPosition(myCommandWindow.getDocument().getLength());
    }
}