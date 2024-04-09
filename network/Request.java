package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
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

  // Constructor
  public Request(Socket socket, Game game, int id) throws Exception {
    this.socket = socket;
    this.game = game;
    is = this.socket.getInputStream();
    os = new DataOutputStream(this.socket.getOutputStream());
    this.id = id;
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
    // Get a reference to the socket's input and output streams.
    InputStream is = socket.getInputStream();
    DataOutputStream os = new DataOutputStream(socket.getOutputStream());

    // Set up input stream filters.
    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    String msg = null;

    // os.writeBytes("Welcome!!!\n");

    Thread.sleep(5000);

    while (true) {

      if (id == 0) {
        Server.sendQuestionToAllPlayers();
        // sendQuestion(game.getQuestions().get(0).getQuestion());
      }

      while (msg == null) {
        msg = br.readLine();
        System.out.println("Player " + id + ": " + msg);
      }
      // os.writeBytes(msg + "\n");

      Thread.sleep(30000);
    }

    // Close streams and socket.
    // os.close();
    // br.close();
    // socket.close();
  }

  public void sendQuestion(String question) throws Exception {
    os.writeBytes(question + "\n");
  }
}