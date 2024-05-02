package network;

import java.io.*;
import java.net.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

  public static void main(String argv[]) throws Exception {

    String answer;
    String msg;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    // Connect to the server
    Socket clientSocket = new Socket("127.0.0.1", 6789);

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    int questionNo = 1;

    while (true) {
      msg = inFromServer.readLine(); // Read message from server

      // Handle welcome message
      if (msg.startsWith("w")) {
        System.out.println(msg.substring(1));
      }
      // Handle question message
      else if (msg.startsWith("q")) {
        String[] questionAndOptions = msg.substring(1).split(";");
        String question = questionAndOptions[0];
        System.out.println();
        System.out.println("Question " + questionNo + ": " + question);
        for (int i = 1; i <= 4; i++) {
          System.out.println(questionAndOptions[i]);
        }
        System.out.println("You have 30 seconds to answer.");
        System.out.print("Please select your answer (1 to 4): ");
        questionNo += 1;

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        answer = "-1";

        while (elapsedTime < 7000) {
          if (System.in.available() > 0) {
            answer = inFromServer.readLine(); // Read user's answer
            break;
          }
          elapsedTime = System.currentTimeMillis() - startTime;
        }

        if (answer.isEmpty()) {
          answer = "none";
          System.out.println("You run out of time!");
        }

        if (answer.length() != 0) {
          outToServer.writeBytes(answer + "\n"); // Send answer to server
        }
      }
      // Handle information message
      else if (msg.startsWith("o")) {
        System.out.println(msg.substring(1));
      }
      // Handle score message
      else if (msg.startsWith("s")) {
        String[] result = msg.split(";");
        System.out.println(result[0].substring(1));
        System.out.println(result[1]);
      }
      // Handle game result message
      else if (msg.startsWith("r")) {
        String[] result = msg.substring(1).split(";");
        System.out.println();
        System.out.println("Game Over! Here are the results:");
        System.out.println("---------------------------------");
        for (String score : result) {
          System.out.println(score);
        }
        System.out.println("---------------------------------");
        break;
      }
    }
  }
}
