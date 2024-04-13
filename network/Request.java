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
import game.Player;
import game.Question;

/**
 * Completed by Quan Nguyen, Dzung Dinh
 */
public class Request implements Runnable {
  private Socket socket;
  private Game game;
  private InputStream is;
  private DataOutputStream os;
  private Integer id;
  private HashMap<Integer, Integer> scores;
  private int lastAnswer = 1;

  // Constructor
  public Request(Socket socket, Game game, int id, HashMap<Integer, Integer> scores) throws Exception {
    this.socket = socket;
    this.game = game;
    is = this.socket.getInputStream();
    os = new DataOutputStream(this.socket.getOutputStream());
    this.id = id;
    this.scores = scores;
    this.lastAnswer = -1;
  }

  // Implement the run() method of the Runnable interface. public void run()
  public void run() {
    try {
      processRequest();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public Integer getId() {
    return id;
  }

  public Integer getLastAnswer() {
    return lastAnswer;
  }

  private void processRequest() throws Exception {
    // add player
    scores.put(id, 0);

    // Get a reference to the socket's input and output streams.
    InputStream is = socket.getInputStream();
    DataOutputStream os = new DataOutputStream(socket.getOutputStream());

    // Set up input stream filters.
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String msg = null;

    // os.writeBytes("Welcome!!!\n");

    Thread.sleep(3000);

    while (true) {

      if (id == 0) {
        Server.sendQuestionToAllPlayers();
      }

      Thread.sleep(10000);

      msg = br.readLine();

      System.out.println("Player " + id + ": " + msg);
      // System.out.println(Integer.parseInt(msg));
      System.out.println(game.getCurrentQuestion());
      System.out.println(game.getCurAnswer());
      System.out.println(game.getCurAnswer() == Integer.parseInt(msg) - 1);

      // System.out.println("*** Before check score, Question:" +
      // game.getCurrentQuestion());

      // Thread.sleep(3000);
      // lastAnswer = Integer.parseInt(msg);
      // Server.checkScoreToAllPlayers(game.getCurrentQuestion(), scores);

      if (game.getCurAnswer() == Integer.parseInt(msg) - 1) {
        // scores.put(id, scores.get(id) + 1);
        Server.updateScore(id);
      }
      Thread.sleep(2000);
      // lastAnswer = Integer.parseInt(msg);

      // System.out.println("*** After check score");
      // System.out.println("Question " + game.getCurrentQuestion() + " answer: "
      // + game.getQuestions().get(game.getCurrentQuestion()).getAnswer());
      // System.out.println("Player " + id + " anwser: " + lastAnswer + " score: " +
      // scores.get(id));

      sendScore();

      Thread.sleep(5000);

      if (id == 0) {
        Server.updateCurrentQuestion();
      }
    }

    // Close streams and socket.
    // os.close();
    // br.close();
    // socket.close();
  }

  public void sendQuestion(String question) throws Exception {
    os.writeBytes("q" + question + "\n");
  }

  public void sendScore() throws IOException {
    os.writeBytes("sPlayer " + id + " score: " + scores.get(id) + "\n");
  }
}