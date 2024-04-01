package game;

public class Player extends User {
  private int score;
  private int answer; // answer to the most recent question

  public Player(String username) {
    super(username);
    this.score = 0;
    this.answer = -1;
  }

  public int getScore() {
    return this.score;
  }

  public void updateScore() {
    this.score += 1;
  }

  public int getAnswer() {
    return this.answer;
  }

  public void setAnswer(int newAns) {
    this.answer = newAns;
  }
}
