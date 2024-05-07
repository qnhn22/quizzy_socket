package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Host {

  public static void main(String argv[]) throws Exception {

    String answer;
    String msg;

    // Connect to the server
    Socket clientSocket = new Socket("127.0.0.1", 6789);

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    while (true) {
      msg = inFromServer.readLine(); // Read message from server

      // Handle welcome message
      if (msg.startsWith("w")) {
        System.out.println(msg.substring(1));
        System.out.println();
      }
      // Handle question message
      else if (msg.startsWith("t")) {
        String[] topics = msg.substring(1).split(";");
        String question = topics[0];
        System.out.println();
        System.out.println(question);
        for (int i = 1; i <= 6; i++) {
          System.out.println(i + ": " + topics[i]);
        }
        System.out.print("Select numbers corresponding with interested topics separated by a comma. Ex: 1,2,3: ");

        String selectedTopics = inFromUser.readLine();

        outToServer.writeBytes(selectedTopics + "\n"); // Send topics to server

        System.out.println();
      }
      // Handle number of question
      else if (msg.startsWith("n")) {
        System.out.print(msg.substring(1) + " ");
        String noQues = inFromUser.readLine();

        outToServer.writeBytes(noQues + "\n");

        System.out.println();
      }
      // Handle question duration
      else if (msg.startsWith("d")) {
        System.out.print(msg.substring(1) + " ");
        String dur = inFromUser.readLine();

        outToServer.writeBytes(dur + "\n");

        System.out.println();

        System.out.println("Thank you! Good bye.");

        inFromServer.close();
        break;
      }
    }
  }
}
