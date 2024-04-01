package game;

import java.util.ArrayList;

public class Game {
  private ArrayList<Question> questions;
  private String code; // the room code created by a host
  private String pin; // the pin to enter the room
  private boolean on; // indicate whether the game is still on or done
  private Host host;
  private ArrayList<Player> players;

  public Game(ArrayList<Question> questions, String code, String pin, boolean on) {
    this.questions = questions;
    this.code = code;
    this.pin = pin;
    this.on = false;
    this.players = new ArrayList<Player>();
  }

  public void updateScores(Question q) {
    for (Player p : this.players) {
      if (p.getAnswer() == q.getAnswer()) {
        p.updateScore();
      }
    }
  }

  public void displayQuestion(Question q, int order) {
    String qText = q.getQuestion();
    ArrayList<String> options = q.getAllOpt();
    int answer = q.getAnswer();
    System.out.println("Question " + (order + 1) + ": " + qText);
    System.out.println("Options:");
    System.out.println("1. " + options.get(0));
    System.out.println("2. " + options.get(1));
    System.out.println("3. " + options.get(2));
    System.out.println("4. " + options.get(3));
    System.out.print("Choose 1 - 4: ");
  }

  public void play() {
    System.out.println("Welcome to Quizzy!!!");
    System.out.println("Please answer 1 to 4 correspoding to the order of options for each question");
    for (int i = 0; i < this.questions.size(); i++) {
      Question q = this.questions.get(i);
      displayQuestion(q, i);
    }
  }
}
