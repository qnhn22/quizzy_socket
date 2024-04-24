package game;

import java.util.ArrayList;

public class Question {
  private String question;
  private String[] options; // store 4 options for this question with index 0 to 3
  private int answer;

  public Question(String question, String[] options, int answer) {
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

  public String[] getAllOpt() {
    return this.options;
  }

  public int getAnswer() {
    return this.answer;
  }

  public void setAnswer(int newAns) {
    this.answer = newAns;
  }

  public String getAnswerText() {
    return options[answer];
  }
}
