package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import game.Game;
import game.Question;

/**
 * Represents a request handler for processing client connections in the
 * multiplayer quiz game.
 */
public class Request implements Runnable {
  private Socket socket; // The socket representing the client connection
  private Game game; // The current game being played
  private InputStream is; // Input stream to receive data from the client
  private DataOutputStream os; // Output stream to send data to the client
  private Integer id; // ID of the player
  private HashMap<Integer, Integer> scores; // Map of player scores

  /**
   * Constructor to initialize the request handler.
   *
   * @param socket The socket representing the client connection.
   * @param game   The current game being played.
   * @param id     The ID of the player.
   * @param scores Map of player scores.
   * @throws Exception If an error occurs while initializing the request handler.
   */
  public Request(Socket socket, Game game, int id, HashMap<Integer, Integer> scores) throws Exception {
    this.socket = socket;
    this.game = game;
    is = this.socket.getInputStream(); // Initialize input stream
    os = new DataOutputStream(this.socket.getOutputStream()); // Initialize output stream
    this.id = id;
    this.scores = scores;
  }

  /**
   * Overrides the run method of the Runnable interface to handle client requests.
   */
  @Override
  public void run() {
    try {
      processRequest(); // Process client request
    } catch (Exception e) {
      System.out.println(e); // Print any exceptions that occur
    }
  }

  /**
   * Method to process the client request.
   *
   * @throws Exception If an error occurs while processing the request.
   */
  private void processRequest() throws Exception {
    // Add player to the game and initialize their score
    scores.put(id, 0);

    // Set up input stream filters
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    // Send welcome message to the player
    os.writeBytes("wWelcome!!!\n");
    os.writeBytes("wYou are player " + id + "\n");

    // Wait for a few seconds before sending the first question
    Thread.sleep(3000);

    while (true) {
      // Send the current question to all players
      if (id == 1) {
        Server.sendQuestionToAllPlayers();
      }

      // Allow players to answer in approximately 30 seconds
      Thread.sleep(8500);

      // Read the player's answer from the input stream
      String msg = br.readLine();

      // Check if the player's answer is correct and update their score accordingly
      if (game.getCurAnswer() == Integer.parseInt(msg) - 1) {
        Server.updateScore(id, true);
      }

      // Send the player's score to them
      sendScore();

      // Wait for a few seconds before proceeding to the next question
      Thread.sleep(4000);

      // Check if the game has ended
      if (game.getQuestions().size() - 1 == game.getCurrentQuestion()) {
        if (id == game.getNoPlayers()) {
          // If all players have answered the final question, sort player scores and send
          // the game result
          Thread.sleep(2000);
          Server.sortAndSendGameResult();
        }
        // End the game and close the connection
        Thread.sleep(3000);
        System.out.println("End game!");
        break;
      }

      // Update the current question if there are more questions
      if (id == 1 && game.getCurrentQuestion() < game.getQuestions().size() - 1) {
        Server.updateCurrentQuestion();
      }
    }

    // Close input stream, output stream, and socket connection
    os.close();
    br.close();
    socket.close();
  }

  /**
   * Method to send the current question to the player.
   *
   * @param question The current question to be sent.
   * @throws Exception If an error occurs while sending the question.
   */
  public void sendQuestion(Question question) throws Exception {
    String questionText = question.getQuestion();
    String questionToPlayers = "q" + questionText + ";";

    for (int i = 1; i <= 4; i++) {
      questionToPlayers += i + ". " + question.getAllOpt()[i - 1] + ";";
    }

    os.writeBytes(questionToPlayers + "\n");
  }

  /**
   * Method to send the player's score to them.
   *
   * @throws IOException If an error occurs while sending the score.
   */
  public void sendScore() throws IOException {
    String msg = "sCorrect answer is " + (game.getCurAnswer() + 1) + " - " + game.getCurAnsText();
    os.writeBytes(msg + ";" + "Your current score is " + scores.get(id) + "\n");
  }

  /**
   * Method to send the game result to the player.
   *
   * @param result The game result to be sent.
   * @throws IOException If an error occurs while sending the result.
   */
  public void sendResult(String result) throws IOException {
    os.writeBytes(result); // Send the game result to the player
  }
}
