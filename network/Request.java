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

  // Constructor
  public Request(Socket socket, Game game) throws Exception {
    this.socket = socket;
    this.game = game;
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

    String msg;

    while (true) {
      os.writeBytes("\n");
      msg = br.readLine();
      System.out.println(msg);
      os.writeBytes(msg + "\n");
    }

    // Close streams and socket.
    // os.close();
    // br.close();
    // socket.close();
  }
}

// while (true) {
// // Get the incoming message from the client (read from socket)
// String msg = br.readLine();
// //Print message received from client
// System.out.println("Received from client: "); System.out.println(msg);
// //convert message to upper case
// String outputMsg = msg.toUpperCase();
// //Send modified msg back to client (write to socket)
// os.writeBytes(outputMsg); os.writeBytes("\r\n"); System.out.println("Sent to
// client: ");
// }
