package network;

import java.io.*;
import java.net.*;

class Client {

  public static void main(String argv[]) throws Exception {

    String answer;
    String question;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket clientSocket = new Socket("127.0.0.1", 6789);

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
        clientSocket.getInputStream()));

    int questionNo = 0;

    while (true) {
      question = inFromServer.readLine();

      if (question.length() != 0) {
        System.out.println("Question " + questionNo + ": " + question);
        System.out.println("Please select your answer (1 to 4):");
        questionNo += 1;
      }

      answer = inFromUser.readLine();

      if (answer.length() != 0) {
        System.out.println(answer);
        outToServer.writeBytes(answer + "\n");
      }
    }
  }
}