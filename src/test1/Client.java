/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test1;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String playerName = JOptionPane.showInputDialog(null, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
            if (playerName == null || playerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty. Exiting game.");
                return;
            }
            out.println(playerName);

            JFrame frame = new JFrame("Game Client - " + playerName);
            JTextArea textArea = new JTextArea();
            JPanel optionsPanel = new JPanel();
            JButton pairButton = new JButton("Pair");
            JButton[] optionButtons = new JButton[4];

            textArea.setEditable(false);
            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
            frame.add(optionsPanel, BorderLayout.SOUTH);
            frame.add(pairButton, BorderLayout.NORTH);

            optionsPanel.setLayout(new GridLayout(2, 2));
            for (int i = 0; i < 4; i++) {
                optionButtons[i] = new JButton();
                optionButtons[i].setEnabled(false);
                optionsPanel.add(optionButtons[i]);

                final int index = i;
                optionButtons[i].addActionListener(e -> {
                    String selectedAnswer = optionButtons[index].getText().substring(0, 1);
                    out.println(selectedAnswer);
                    disableOptions(optionButtons);
                });
            }

            pairButton.addActionListener(e -> out.println("Pair"));

            frame.setSize(500, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("Question:")) {
                    textArea.append("\n" + serverMessage.substring(9) + "\n");
                } else if (serverMessage.startsWith("A.") || serverMessage.startsWith("B.") ||
                           serverMessage.startsWith("C.") || serverMessage.startsWith("D.")) {
                    int index = serverMessage.charAt(0) - 'A';
                    optionButtons[index].setText(serverMessage);
                    optionButtons[index].setEnabled(true);
                } else {
                    textArea.append(serverMessage + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void disableOptions(JButton[] optionButtons) {
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
    }
}
