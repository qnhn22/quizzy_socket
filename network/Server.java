package network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import game.Game;
import game.Question;

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
    boolean hostConnected = false;
    ArrayList<String> topics = new ArrayList<>();
    int noQues = 0;
    int dur = 0;

    while (!hostConnected) {
      // Hash map for topics
      Map<String, String> topicMap = new HashMap<>();

      // Add items to the HashMap
      topicMap.put("1", "Science");
      topicMap.put("2", "Technology");
      topicMap.put("3", "Sports");
      topicMap.put("4", "Geography");
      topicMap.put("5", "History");
      topicMap.put("6", "Literature");

      // Host set up game
      String[] settings = setUpGame(socket);

      String topicString = settings[0]; // quesiton topics
      String[] topicIds = topicString.split(",");
      for (String id : topicIds) {
        topics.add(topicMap.get(id));
      }

      noQues = Integer.parseInt(settings[1]); // number of question in a game
      dur = Integer.parseInt(settings[2]); // time allowed to answer each question

      hostConnected = true;
    }

    // Read questions from file and select random questions for the game
    ArrayList<Question> questions = selectRandomQuestions(noQues, topics);

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
   * @param int noOfQuestions is the number of questions to select.
   * @return An ArrayList of randomly selected questions.
   * @throws IOException If an error occurs while reading questions from the file.
   */
  private static ArrayList<Question> selectRandomQuestions(int noOfQuestions, ArrayList<String> topics)
      throws IOException {
    ArrayList<String> rawQuestions = readQuestionsFromFile();

    // Filter questions in selected topics
    List<String> questionsInTopics = rawQuestions.stream()
        .filter(question -> topics.stream().anyMatch(question::endsWith))
        .collect(Collectors.toList());

    Collections.shuffle(questionsInTopics);

    ArrayList<Question> questions = new ArrayList<>();
    int i = 0;
    while (noOfQuestions > 0 && i < questionsInTopics.size()) {
      String rawQuestion = questionsInTopics.get(i);
      if (rawQuestion != null && rawQuestion.length() > 0) {
        Question question = createQuestion(rawQuestion);
        questions.add(question);
        noOfQuestions -= 1;
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
        System.out.println("Player " + id + " joined");
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
    System.out.println(text);
    String[] arr = text.split(";");
    String question = arr[0];
    int answer = Integer.parseInt(arr[5]);
    String[] options = Arrays.copyOfRange(arr, 1, 5);
    String topic = arr[6];
    return new Question(question, options, answer, topic); // Create and return a new Question object
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

  public static String[] setUpGame(ServerSocket socket) throws Exception {
    // Get game setting from the host client
    Socket connection = socket.accept(); // Accept a connection from the host client
    InputStream is = connection.getInputStream();
    // Input stream to receive data from the client
    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
    // Output stream to send data to the client
    BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Set up input stream filters

    // Send welcome message to the player
    os.writeBytes("wWelcome to Quizzy!!!\n");
    os.writeBytes("wYou are the host. Let choose game settings!" + "\n");

    Thread.sleep(1000);

    // Topics settings
    os.writeBytes(
        "t" + "What topics you want to cover?;Science;Technology;Sports;Geography;History;Literature" + "\n");

    String topics = br.readLine();
    String[] topicList = topics.split(";");
    System.out.println("Topics: " + topicList);

    // Number of questions
    os.writeBytes("n" + "How many questions you want?" + "\n");
    String noQues = br.readLine();
    System.out.println(noQues);

    // Answering time for each question
    os.writeBytes("d" + "How long you want for answering time (in seconds)?" + "\n");
    String quesDuration = Integer.parseInt(br.readLine()) * 1000 + "";
    System.out.println(quesDuration);

    br.close();
    is.close();
    os.close();
    connection.close();

    String[] arr = { topics, noQues, quesDuration };

    return arr;
  }
}
