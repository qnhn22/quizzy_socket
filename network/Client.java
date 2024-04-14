package network;

import java.io.*;
import java.net.*;

class Client {

  public static void main(String argv[]) throws Exception {

    String answer;
    String msg;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket clientSocket = new Socket("127.0.0.1", 6789);

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
        clientSocket.getInputStream()));

    int questionNo = 1;

    while (true) {
      msg = inFromServer.readLine();
      System.out.println(msg);

      if (msg.startsWith("w")) {
        System.out.println(msg.substring(1));
      } else if (msg.startsWith("q")) {
        String[] questionAndOptions = msg.substring(1).split(";");
        String question = questionAndOptions[0];
        System.out.println("Question " + questionNo + ": " + question);
        for (int i = 1; i <= 4; i++) {
          System.out.println(questionAndOptions[i]);
        }
        System.out.println("Please select your answer (1 to 4):");
        questionNo += 1;

        answer = inFromUser.readLine();

        if (answer.length() != 0) {
          System.out.println(answer);
          outToServer.writeBytes(answer + "\n");
        }
      } else if (msg.startsWith("s") || msg.startsWith("o")) {
        System.out.println(msg.substring(1));
      }
    }
  }
}