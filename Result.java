package production;

import java.util.LinkedList;

public class Result {

  static LinkedList<Result> results = new LinkedList<>(); //concurrentlinkedlist
  int score;
String type;
  public Result(int score, String type) {
    this.score = score;
    this.type = type;
  }

  public int getScore() {
    return score;
  }

  public static void addResult(Result result) {
    results.add(result);
  }

  @Override
  public String toString() {
    return "Type is : " + type + " score is : " + score;
  }
}
