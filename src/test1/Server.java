/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static final List<String[]> questions = new ArrayList<>();
    private static final int POINTS_PER_CORRECT_ANSWER = 10;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final Queue<ClientHandler> waitingQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {
        loadQuestionsFromFile("D:/THmangmaytinh/test2/questions.txt");
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected!");
            ClientHandler clientHandler = new ClientHandler(socket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    private static void loadQuestionsFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 6); // Tách câu hỏi và đáp án
                if (parts.length == 6) {
                    questions.add(parts);
                }
            }
            System.out.println("Questions loaded: " + questions.size());
        } catch (IOException e) {
            System.out.println("Error reading questions file: " + e.getMessage());
        }
    }
    synchronized static void pairClients(ClientHandler client) {
        waitingQueue.add(client);
        if (waitingQueue.size() >= 2) {
            ClientHandler player1 = waitingQueue.poll();
            ClientHandler player2 = waitingQueue.poll();
            if (player1 != null && player2 != null) {
                player1.setPartner(player2);
                player2.setPartner(player1);
                player1.sendMessage("Paired with: " + player2.getPlayerName());
                player2.sendMessage("Paired with: " + player1.getPlayerName());
                startGame(player1, player2);
            }
        }
    }

    private static void startGame(ClientHandler player1, ClientHandler player2) {
        new Thread(() -> {
            try {
                int round = 1;
                int maxRounds = 4; // Số vòng chơi cố định
                int questionsPerRound = 5; // Số câu hỏi mỗi vòng
                List<String[]> availableQuestions = new ArrayList<>(questions); // Sao chép danh sách câu hỏi

                Collections.shuffle(availableQuestions); // Xáo trộn danh sách câu hỏi

                while (round <= maxRounds && !availableQuestions.isEmpty()) {
                    player1.sendMessage("=== ROUND " + round + " ===");
                    player2.sendMessage("=== ROUND " + round + " ===");

                    // Lấy tối đa 5 câu hỏi từ danh sách đã xáo trộn
                    List<String[]> roundQuestions = availableQuestions.subList(0, Math.min(questionsPerRound, availableQuestions.size()));

                    for (String[] questionData : roundQuestions) {
                        sendQuestion(player1, questionData);
                        sendQuestion(player2, questionData);

                        String answer1 = player1.readAnswer();
                        String answer2 = player2.readAnswer();

                        evaluateAnswer(player1, answer1, questionData[5]);
                        evaluateAnswer(player2, answer2, questionData[5]);
                    }

                    // Xóa các câu hỏi đã sử dụng khỏi danh sách
                    availableQuestions.removeAll(roundQuestions);

                    player1.sendMessage("End of Round " + round + ". Your score: " + player1.getScore());
                    player2.sendMessage("End of Round " + round + ". Your score: " + player2.getScore());

                    round++;

                    if (round <= maxRounds && !availableQuestions.isEmpty()) {
                        player1.sendMessage("Prepare for the next round...");
                        player2.sendMessage("Prepare for the next round...");
                        Thread.sleep(3000); // Nghỉ giữa các vòng
                    }
                }

                player1.sendMessage("Game over! Final score: " + player1.getScore());
                player2.sendMessage("Game over! Final score: " + player2.getScore());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static void sendQuestion(ClientHandler client, String[] questionData) throws IOException {
        client.sendMessage("Question:" + questionData[0]);
        client.sendMessage(questionData[1]);
        client.sendMessage(questionData[2]);
        client.sendMessage(questionData[3]);
        client.sendMessage(questionData[4]);
    }

    private static void evaluateAnswer(ClientHandler client, String answer, String correctAnswer) {
        if (answer != null && answer.equalsIgnoreCase(correctAnswer)) {
            client.addScore(POINTS_PER_CORRECT_ANSWER);
            client.sendMessage("Correct!");
        } else {
            client.sendMessage("Wrong! The correct answer is " + correctAnswer + ".");
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;
        private int score = 0;
        private ClientHandler partner;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                out.println("Enter your name:");
                this.playerName = in.readLine();
                out.println("Hello, " + this.playerName + "!");
                //out.println("Press 'Pair' to find an opponent.");

                while (true) {
                    String command = in.readLine();
                    if ("Pair".equalsIgnoreCase(command)) {
                        pairClients(this);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
        public String getPlayerName() {
            return this.playerName;
        }

        public void setPartner(ClientHandler partner) {
                this.partner = partner;
            }
        public ClientHandler getPartner() {
            return this.partner;
        }


        public String readAnswer() throws IOException {
            return in.readLine();
        }
        public int getScore() {
            return this.score;
        }

        public void addScore(int points) {
            this.score += points;
        }
    }
}
