package game;

import java.util.ArrayList;

public class Question {
  private String question;
  private ArrayList<String> options; // store 4 options for this question with index 0 to 3
  private int answer;

  public Question(String question, ArrayList<String> options, int answer) {
    this.question = question;
    this.options = options;
    this.answer = answer;
  }

  public String getQuestion() {
    return this.question;
  }

  public void setQuestion(String newQues) {
    this.question = newQues;
  }

  public ArrayList<String> getAllOpt() {
    return this.options;
  }

  public String getOpt(int index) {
    return this.options.get(index);
  }

  public void setOpt(int index, String newOpt) {
    this.options.set(index, newOpt);
  }

  public int getAnswer() {
    return this.answer;
  }

  public String getAnsText() {
    return this.options.get(this.answer);
  }

  public void setAnswer(int newAns) {
    this.answer = newAns;
  }
}
