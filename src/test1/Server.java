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
    
    private static void updateWaitingRoom() {
        StringBuilder waitingRoomStatus = new StringBuilder("Waiting Room:\n");
        for (ClientHandler client : waitingQueue) {
            waitingRoomStatus.append(client.getPlayerName()).append("\n");
        }
        broadcastWaitingRoom(waitingRoomStatus.toString());
    }

    private static void broadcastWaitingRoom(String message) {
        for (ClientHandler client : waitingQueue) {
            client.sendMessage(message);
        }
    }
    synchronized static void pairClients(ClientHandler client) {
        waitingQueue.add(client);
        updateWaitingRoom();

        if (waitingQueue.size() >= 4) { // Chờ đủ 4 người chơi
            new Thread(() -> {
                try {
                    // Đếm ngược 10 giây
                    for (int i = 10; i > 0; i--) {
                        broadcastWaitingRoom("Game starts in " + i + " seconds...");
                        Thread.sleep(1000);
                    }

                    // Lấy 4 người chơi từ phòng chờ
                    List<ClientHandler> players = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        players.add(waitingQueue.poll());
                    }

                    // Xác nhận tất cả người chơi đã ghép cặp
                    for (ClientHandler player : players) {
                        player.sendMessage("Game starting! Players in this game:");
                        for (ClientHandler teammate : players) {
                            if (player != teammate) {
                                player.sendMessage("- " + teammate.getPlayerName());
                            }
                        }
                    }

                    // Bắt đầu trò chơi với 4 người chơi
                    startGame(players);
                    updateWaitingRoom(); // Cập nhật phòng chờ sau khi ghép cặp
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
//    synchronized static void pairClients(ClientHandler client) {
//        waitingQueue.add(client);
//        updateWaitingRoom();
//
//        if (waitingQueue.size() >= 4) { // Chờ đủ 4 người chơi
//            new Thread(() -> {
//                try {
//                    // Đếm ngược 10 giây
//                    for (int i = 10; i > 0; i--) {
//                        broadcastWaitingRoom("Game starts in " + i + " seconds...");
//                        Thread.sleep(1000);
//                    }
//
//                    // Lấy 4 người chơi từ phòng chờ
//                    List<ClientHandler> players = new ArrayList<>();
//                    for (int i = 0; i < 4; i++) {
//                        players.add(waitingQueue.poll());
//                    }
//
//                    // Xác nhận tất cả người chơi đã ghép cặp
//                    for (ClientHandler player : players) {
//                        player.sendMessage("Game starting! Players in this game:");
//                        for (ClientHandler teammate : players) {
//                            if (player != teammate) {
//                                player.sendMessage("- " + teammate.getPlayerName());
//                            }
//                        }
//                    }
//
//                    // Bắt đầu trò chơi với 4 người chơi
//                    startGame(players);
//                    updateWaitingRoom(); // Cập nhật phòng chờ sau khi ghép cặp
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//    }
    private static void startGame(List<ClientHandler> players) {
    new Thread(() -> {
        // Thông báo bắt đầu trò chơi
        for (ClientHandler player : players) {
            player.sendMessage("Game is starting now! Players in this game:");
            for (ClientHandler teammate : players) {
                if (player != teammate) {
                    player.sendMessage("- " + teammate.getPlayerName());
                }
            }
        }
        // Trò chơi bắt đầu
        // Ví dụ ở đây có thể chỉ đơn giản là thông báo rằng trò chơi đã bắt đầu mà không cần câu hỏi
        for (ClientHandler player : players) {
            player.sendMessage("Game started! Good luck!");
        }
    }).start();
}

//    private static void startGame(List<ClientHandler> players) {
//    new Thread(() -> {
//        try {
//            int round = 1;
//            int maxRounds = 4; // Số vòng chơi cố định
//            int questionsPerRound = 5; // Số câu hỏi mỗi vòng
//            List<String[]> availableQuestions = new ArrayList<>(questions); // Sao chép danh sách câu hỏi
//
//            Collections.shuffle(availableQuestions); // Xáo trộn danh sách câu hỏi
//
//            while (round <= maxRounds && !availableQuestions.isEmpty()) {
//                // Thông báo bắt đầu vòng chơi
//                for (ClientHandler player : players) {
//                    player.sendMessage("=== ROUND " + round + " ===");
//                }
//
//                // Lấy tối đa 5 câu hỏi từ danh sách đã xáo trộn
//                List<String[]> roundQuestions = availableQuestions.subList(0, Math.min(questionsPerRound, availableQuestions.size()));
//
//                for (String[] questionData : roundQuestions) {
//                    for (ClientHandler player : players) {
//                        sendQuestion(player, questionData);
//                    }
//
//                    // Đọc câu trả lời từ tất cả người chơi
//                    Map<ClientHandler, String> answers = new HashMap<>();
//                    for (ClientHandler player : players) {
//                        answers.put(player, player.readAnswer());
//                    }
//
//                    // Đánh giá câu trả lời
//                    for (ClientHandler player : players) {
//                        evaluateAnswer(player, answers.get(player), questionData[5]);
//                    }
//                }
//
//                // Xóa các câu hỏi đã sử dụng khỏi danh sách
//                availableQuestions.removeAll(roundQuestions);
//
//                for (ClientHandler player : players) {
//                    player.sendMessage("End of Round " + round + ". Your score: " + player.getScore());
//                }
//
//                round++;
//
//                if (round <= maxRounds && !availableQuestions.isEmpty()) {
//                    for (ClientHandler player : players) {
//                        player.sendMessage("Prepare for the next round...");
//                    }
//                    Thread.sleep(3000); // Nghỉ giữa các vòng
//                }
//            }
//
//            // Kết thúc trò chơi
//            for (ClientHandler player : players) {
//                player.sendMessage("Game over! Final score: " + player.getScore());
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }).start();
//    }
//    private static void sendQuestion(ClientHandler client, String[] questionData) throws IOException {
//        client.sendMessage("Question:" + questionData[0]);
//        client.sendMessage(questionData[1]);
//        client.sendMessage(questionData[2]);
//        client.sendMessage(questionData[3]);
//        client.sendMessage(questionData[4]);
//    }

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
                //out.println("Enter your name:");
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
