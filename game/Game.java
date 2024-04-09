package game;

import java.util.ArrayList;

public class Game {
  private ArrayList<Question> questions;
  private ArrayList<Player> players;
  private Integer count; // handle concurrency
  private Integer curQuestionIdx; // store current question index

  public Game(ArrayList<Question> questions) {
    this.questions = questions;
    this.players = new ArrayList<Player>();
    this.count = this.players.size();
    this.curQuestionIdx = 0;
  }

  public ArrayList<Question> getQuestions() {
    return this.questions;
  }

  public void updatePlayerScore(Question q, int playerIndex) {
    Player p = this.players.get(playerIndex);
    if (p.getAnswer() - 1 == q.getAnswer()) {
      p.updateScore();
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

  public void play(int questionIdx) {
    // System.out.println("Welcome to Quizzy!!!");
    // System.out.println("Please answer 1 to 4 correspoding to the order of options
    // for each question");

    // for (int i = 0; i < this.questions.size(); i++) {
    // Question q = this.questions.get(i);
    // displayQuestion(q, i);
    // }

    Question q = this.questions.get(questionIdx);
    displayQuestion(q, questionIdx);
  }

  public Integer getCount() {
    return this.count;
  }

  public void setCount(int newCount) {
    this.count -= 1;
  }

  public void resetCount() {
    this.count = this.players.size();
  }

  public Integer getCurrentQuestion() { // index
    return this.curQuestionIdx;
  }

  public void updateCurrentQuestion() { // index
    this.curQuestionIdx += 1;
  }

  public ArrayList<Player> getPlayers() {
    return this.players;
  }

  public boolean isEnd() {
    return this.curQuestionIdx == this.questions.size();
  }
}
