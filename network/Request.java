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
 * Completed by Quan Nguyen, Dzung Dinh
 */
public class Request implements Runnable {
  private Socket socket;
  private Game game;
  private InputStream is;
  private DataOutputStream os;
  private Integer id;
  private HashMap<Integer, Integer> scores;

  // Constructor
  public Request(Socket socket, Game game, int id, HashMap<Integer, Integer> scores) throws Exception {
    this.socket = socket;
    this.game = game;
    is = this.socket.getInputStream();
    os = new DataOutputStream(this.socket.getOutputStream());
    this.id = id;
    this.scores = scores;
  }

  // Implement the run() method of the Runnable interface. public void run()
  public void run() {
    try {
      processRequest();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void processRequest() throws Exception {
    // add player
    scores.put(id, 0);

    // Set up input stream filters.
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String msg = null;

    os.writeBytes("wWelcome!!!\n");
    os.writeBytes("wYou are player " + id);
    os.writeBytes("\n");

    Thread.sleep(3000);

    while (true) {

      if (id == 1) {
        Server.sendQuestionToAllPlayers();
      }

      Thread.sleep(10000);

      // read answer from a player
      msg = br.readLine();

      if (game.getCurAnswer() == Integer.parseInt(msg) - 1) {
        Server.updateScore(id, true);
      }
      Thread.sleep(2000);

      sendScore();

      Thread.sleep(5000);

      if (game.getQuestions().size() - 1 == game.getCurrentQuestion()) {
        System.out.println("End game!");
        os.writeBytes("eEnd Game!!!\n");
        os.close();
        br.close();
        socket.close();
      }

      if (id == 1 && game.getCurrentQuestion() < game.getQuestions().size() - 1) {
        Server.updateCurrentQuestion();
      }
    }
  }

  public void sendQuestion(Question question) throws Exception {
    String questionText = question.getQuestion();
    String questionToPlayers = "q" + questionText + ";";

    for (int i = 1; i <= 4; i++) {
      questionToPlayers += i + ". " + question.getAllOpt().get(i - 1) + ";";
    }

    os.writeBytes(questionToPlayers + "\n");
  }

  public void sendScore() throws IOException {
    String msg = "sCorrect answer is " + (game.getCurAnswer() + 1) + " - " + game.getCurAnsText();
    os.writeBytes(msg + ";" + "Your current score is " + scores.get(id) + "\n");
  }
}