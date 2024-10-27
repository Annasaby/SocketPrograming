import java.io.*;
import java.net.*;

public class TicTacToeServer {
    private static final int PORT = 12345;
    private static ServerSocket serverSocket;
    private static Socket player1, player2;
    private static PrintWriter out1, out2;
    private static BufferedReader in1, in2;
    private static char[][] board = {
        {' ', ' ', ' '},
        {' ', ' ', ' '},
        {' ', ' ', ' '}
    };
    private static char currentPlayer = 'X';

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("TicTacToe Server is running... Waiting for two players.");

        player1 = serverSocket.accept();
        System.out.println("Player 1 connected.");
        out1 = new PrintWriter(player1.getOutputStream(), true);
        in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        out1.println("You are Player X");

        player2 = serverSocket.accept();
        System.out.println("Player 2 connected.");
        out2 = new PrintWriter(player2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        out2.println("You are Player O");

        new Thread(new PlayerHandler(player1, player2, 'X', out1, in1, out2)).start();
        new Thread(new PlayerHandler(player2, player1, 'O', out2, in2, out1)).start();
    }

    static class PlayerHandler implements Runnable {
        private Socket playerSocket, opponentSocket;
        private PrintWriter out, opponentOut;
        private BufferedReader in;
        private char playerMark;

        public PlayerHandler(Socket playerSocket, Socket opponentSocket, char mark, PrintWriter out, BufferedReader in, PrintWriter opponentOut) {
            this.playerSocket = playerSocket;
            this.opponentSocket = opponentSocket;
            this.playerMark = mark;
            this.out = out;
            this.in = in;
            this.opponentOut = opponentOut;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (playerMark == currentPlayer) {
                        out.println("Your turn");
                        String move = in.readLine(); // Membaca input dari pemain

                        if (move != null && move.length() == 2) {
                            int row = Character.getNumericValue(move.charAt(0));
                            int col = Character.getNumericValue(move.charAt(1));

                            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ') {
                                board[row][col] = playerMark;
                                opponentOut.println("Opponent moved: " + row + col);
                                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

                                if (checkWin(playerMark)) {
                                    out.println("WIN");
                                    opponentOut.println("LOSE");
                                    break;
                                } else if (checkDraw()) {
                                    out.println("DRAW");
                                    opponentOut.println("DRAW");
                                    break;
                                }
                            }
                        }
                    } else {
                        out.println("Wait");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean checkWin(char mark) {
            for (int i = 0; i < 3; i++) {
                if (board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) return true;
                if (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark) return true;
            }
            return board[0][0] == mark && board[1][1] == mark && board[2][2] == mark ||
                   board[0][2] == mark && board[1][1] == mark && board[2][0] == mark;
        }

        private boolean checkDraw() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') return false;
                }
            }
            return true;
        }
    }
}
