package network;

import java.io.*;
import java.net.*;
import java.util.*;
import game.Game;
import game.Question;
import network.Request;

/**
 * Completed by Quan Nguyen, Dzung Dinh
 */
public final class Server {
  private static Game game;
  private static ArrayList<Request> playerConnections;
  private static HashMap<Integer, Integer> scores;

  public static void main(String argv[]) throws Exception {
    int port = 6789;
    ServerSocket socket = new ServerSocket(port);

    ArrayList<Question> questions = new ArrayList<>();
    // read all questions from questions.txt file
    ArrayList<String> rawQuestions = Server.readQuestion();
    Collections.shuffle(rawQuestions);
    // get 5 random questions for the game
    int noQuestions = 5;
    int i = 0;
    while (noQuestions > 0) {
      String rawQuestion = rawQuestions.get(i);
      System.out.println(rawQuestion);
      if (rawQuestion != null && rawQuestion.length() > 0) {
        Question question = Server.createQuestion(rawQuestions.get(i));
        questions.add(question);
        noQuestions -= 1;
      }
      i += 1;
    }

    game = new Game(questions);
    playerConnections = new ArrayList<>();
    int id = 1;
    scores = new HashMap<>();

    long startTime = System.currentTimeMillis();
    long duration = 5000; // 5 seconds

    // accept any player connection in 5 seconds
    while (System.currentTimeMillis() - startTime <= duration) {
      Socket connection = socket.accept();

      // Construct an object to process the HTTP request message.
      Request request = new Request(connection, game, id, scores);

      if (connection.isConnected()) {
        playerConnections.add(request);
        game.updateNoPlayers();
        id += 1;
      }
    }

    Server.startGame();
  }

  public static synchronized void startGame() {
    for (Request player : playerConnections) {
      // Create a new thread to process the request.
      Thread thread = new Thread(player);
      thread.start();
    }
  }

  public static synchronized void sendQuestionToAllPlayers() {
    int curIndex = game.getCurrentQuestion(); // the index of current question in the question list

    if (curIndex >= 0 && curIndex < game.getQuestions().size()) {
      for (Request player : playerConnections) {
        Question question = game.getQuestions().get(curIndex);
        try {
          player.sendQuestion(question);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }

  public static synchronized void updateCurrentQuestion() {
    game.updateCurrentQuestion();
  }

  public static synchronized void updateScore(int id, boolean isCorrect) {
    int curScore = scores.get(id);
    if (isCorrect == true) {
      scores.put(id, curScore + 1);
    } else {
      scores.put(id, curScore);
    }
  }

  public static synchronized void sortAndSendGameResult() {
    ArrayList<int[]> result = new ArrayList<>();
    for (int i = 1; i <= game.getNoPlayers(); i++) {
      result.add(new int[] { scores.get(i), i });
    }

    String resultToClient = "r";
    Collections.sort(result, Comparator.comparingInt(a -> -a[0]));
    for (int[] playerScore : result) {
      resultToClient += "Player " + playerScore[1] + ": " + playerScore[0] + ";";
    }
    resultToClient += "\n";

    for (Request player : playerConnections) {
      try {
        player.sendResult(resultToClient);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public static Question createQuestion(String text) {
    String[] arr = text.split(";");
    String question = arr[0];
    int answer = Integer.parseInt(arr[5]);
    String[] options = Arrays.copyOfRange(arr, 1, 5);
    Question questionObj = new Question(question, options, answer);
    return questionObj;
  }

  public static ArrayList<String> readQuestion() throws IOException {
    ArrayList<String> questions = new ArrayList<>();
    String filePath = "questions.txt";
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String line = reader.readLine();
    while ((line = reader.readLine()) != null) {
      questions.add(line);
    }
    reader.close();
    return questions;
  }
}