package network;

import java.io.*;
import java.net.*;
import java.util.*;

import game.Game;
import game.Question;

/**
 * Completed by Quan Nguyen, Dzung Dinh
 */
public final class Server {
  public static void main(String argv[]) throws Exception {
    int port = 6789;
    ServerSocket socket = new ServerSocket(port);

    optionsQ1.add("Earth");
    optionsQ1.add("Mars");
    optionsQ1.add("Jupiter");
    optionsQ1.add("Saturn");
    Question q1 = new Question("What is the largest planet in our solar system?", optionsQ1, 2);
    ArrayList<String> optionsQ2 = new ArrayList<>();
    optionsQ2.add("J.K. Rowling");
    optionsQ2.add("Stephen King");
    optionsQ2.add("Harper Lee");
    optionsQ2.add("George Orwell");
    Question q2 = new Question("Who wrote 'To Kill a Mockingbird'?", optionsQ1, 2);
    ArrayList<String> optionsQ3 = new ArrayList<>();
    optionsQ3.add("Paris");
    optionsQ3.add("London");
    optionsQ3.add("Berlin");
    optionsQ3.add("Rome");
    Question q3 = new Question("What is the capital of France?", optionsQ3, 0);
    ArrayList<Question> questions = new ArrayList<>();
    questions.add(q1);
    questions.add(q2);
    questions.add(q3);
    Game game = new Game(questions, "1234", "2234");
    ArrayList<Question> gameQuestions = game.getQuestions();

    while (true) {
      Socket connection = socket.accept();
      // Construct an object to process the HTTP request message.
      Request request = new Request(connection, game);

      // Create a new thread to process the request.
      Thread thread = new Thread(request);

      // Start the thread.
      thread.start();
    }
  }
}