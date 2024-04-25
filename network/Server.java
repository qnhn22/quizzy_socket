package network;

import java.io.*;
import java.net.*;
import java.util.*;
import game.Game;
import game.Question;
import network.Request;

/**
 * Represents the server for the multiplayer quiz game.
 */
public final class Server {
  private static Game game; // The current game being played
  private static ArrayList<Request> playerConnections; // List of player connections
  private static HashMap<Integer, Integer> scores; // Map of player scores

  /**
   * The main method to start the server and handle player connections.
   *
   * @param argv Command line arguments (not used).
   * @throws Exception If an error occurs while starting the server.
   */
  public static void main(String argv[]) throws Exception {
    int port = 6789; // Port number for the server
    ServerSocket socket = new ServerSocket(port); // Server socket for accepting connections

    // Read questions from file and select random questions for the game
    ArrayList<Question> questions = selectRandomQuestions();

    // Initialize game, player connections, and scores
    initializeGame(questions);

    // Accept player connections within a specified duration
    acceptPlayerConnections(socket);

    // Start the game
    startGame();
  }

  /**
   * Selects random questions from a list of questions read from a file.
   *
   * @return An ArrayList of randomly selected questions.
   * @throws IOException If an error occurs while reading questions from the file.
   */
  private static ArrayList<Question> selectRandomQuestions() throws IOException {
    ArrayList<String> rawQuestions = readQuestionsFromFile();
    Collections.shuffle(rawQuestions);
    ArrayList<Question> questions = new ArrayList<>();
    int noQuestions = 5; // Number of questions to select
    int i = 0;
    while (noQuestions > 0 && i < rawQuestions.size()) {
      String rawQuestion = rawQuestions.get(i);
      if (rawQuestion != null && rawQuestion.length() > 0) {
        Question question = createQuestion(rawQuestion);
        questions.add(question);
        noQuestions -= 1;
      }
      i += 1;
    }
    return questions;
  }

  /**
   * Initializes the game with the given list of questions.
   *
   * @param questions The list of questions for the game.
   */
  private static void initializeGame(ArrayList<Question> questions) {
    game = new Game(questions); // Create a new game instance
    playerConnections = new ArrayList<>(); // Initialize player connections list
    scores = new HashMap<>(); // Initialize player scores map
  }

  /**
   * Accepts player connections within a specified duration and adds them to the
   * player connections list.
   *
   * @param socket The server socket for accepting connections.
   * @throws IOException If an error occurs while accepting connections.
   */
  private static void acceptPlayerConnections(ServerSocket socket) throws Exception {
    int id = 1; // Initial player ID
    long startTime = System.currentTimeMillis();
    long duration = 5000; // 10 seconds

    // Accept player connections within the specified duration
    while (System.currentTimeMillis() - startTime <= duration) {
      Socket connection = socket.accept(); // Accept a new connection
      Request request = new Request(connection, game, id, scores); // Create a request handler for the connection

      // Add the request handler to the player connections list if the connection is
      // successful
      if (connection.isConnected()) {
        playerConnections.add(request); // Add the request handler to the list
        game.updateNoPlayers(); // Increment the number of players
        id += 1; // Increment the player ID for the next connection
      }
    }
  }

  /**
   * Starts the game by creating a thread for each player connection.
   */
  public static synchronized void startGame() {
    for (Request player : playerConnections) {
      Thread thread = new Thread(player); // Create a new thread for the player connection
      thread.start(); // Start the thread
    }
  }

  /**
   * Sends the current question to all players.
   */
  public static synchronized void sendQuestionToAllPlayers() {
    int curIndex = game.getCurrentQuestion(); // Get the index of the current question
    if (curIndex >= 0 && curIndex < game.getQuestions().size()) {
      for (Request player : playerConnections) {
        Question question = game.getQuestions().get(curIndex); // Get the current question
        try {
          player.sendQuestion(question); // Send the question to the player
        } catch (Exception e) {
          System.out.println(e.getMessage()); // Handle any exceptions
        }
      }
    }
  }

  /**
   * Updates the current question in the game.
   */
  public static synchronized void updateCurrentQuestion() {
    game.updateCurrentQuestion(); // Update the current question in the game
  }

  /**
   * Updates the score for a player based on whether their answer is correct.
   *
   * @param id        The ID of the player.
   * @param isCorrect True if the player's answer is correct, false otherwise.
   */
  public static synchronized void updateScore(int id, boolean isCorrect) {
    int curScore = scores.get(id); // Get the current score for the player
    if (isCorrect) {
      scores.put(id, curScore + 1); // Increment the score if the answer is correct
    } else {
      scores.put(id, curScore); // Otherwise, keep the score unchanged
    }
  }

  /**
   * Sorts player scores and sends the game results to all players.
   */
  public static synchronized void sortAndSendGameResult() {
    ArrayList<int[]> result = new ArrayList<>();
    for (int i = 1; i <= game.getNoPlayers(); i++) {
      result.add(new int[] { scores.get(i), i }); // Add player ID and score to the result list
    }

    // Sort the result list in descending order of scores
    Collections.sort(result, Comparator.comparingInt(a -> -a[0]));

    // Construct the game result message
    StringBuilder resultToClient = new StringBuilder("r");
    for (int[] playerScore : result) {
      resultToClient.append("Player ").append(playerScore[1]).append(": ").append(playerScore[0]).append(";");
    }
    resultToClient.append("\n");

    // Send the game result message to all players
    for (Request player : playerConnections) {
      try {
        player.sendResult(resultToClient.toString()); // Send the game result to the player
      } catch (Exception e) {
        System.out.println(e.getMessage()); // Handle any exceptions
      }
    }
  }

  /**
   * Creates a Question object from a raw question string.
   *
   * @param text The raw question string.
   * @return The Question object created from the raw string.
   */
  public static Question createQuestion(String text) {
    String[] arr = text.split(";");
    String question = arr[0];
    int answer = Integer.parseInt(arr[5]);
    String[] options = Arrays.copyOfRange(arr, 1, 5);
    return new Question(question, options, answer); // Create and return a new Question object
  }

  /**
   * Reads questions from a file and returns them as a list of strings.
   *
   * @return The list of raw question strings read from the file.
   * @throws IOException If an error occurs while reading the file.
   */
  public static ArrayList<String> readQuestionsFromFile() throws IOException {
    ArrayList<String> questions = new ArrayList<>();
    String filePath = "questions.txt"; // Path to the file containing questions
    BufferedReader reader = new BufferedReader(new FileReader(filePath)); // Create a file reader
    String line = reader.readLine();
    while ((line = reader.readLine()) != null) {
      questions.add(line); // Add each line (question) to the list
    }
    reader.close(); // Close the file reader
    return questions; // Return the list of questions read from the file
  }
}
