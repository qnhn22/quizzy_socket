package game;

import java.util.ArrayList;

public class Game {
  private ArrayList<Question> questions;
  private Integer curQuestionIdx; // store current question index
  private int curAnswer;

  public Game(ArrayList<Question> questions) {
    this.questions = questions;
    this.curQuestionIdx = 0;
    this.curAnswer = this.questions.get(0).getAnswer();
  }

  public ArrayList<Question> getQuestions() {
    return this.questions;
  }

  public Integer getCurrentQuestion() {
    return this.curQuestionIdx;
  }

  public int getCurAnswer() {
    return this.curAnswer;
  }

  public void setCurAnswer(int newAns) {
    this.curAnswer = newAns;
  }

  public void updateCurrentQuestion() {
    this.curQuestionIdx += 1;
    Question curQ = this.questions.get(this.curQuestionIdx);
    setCurAnswer(curQ.getAnswer());
  }

  public boolean isEnd() {
    return this.curQuestionIdx == this.questions.size();
  }
}
