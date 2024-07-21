package production;

import java.io.BufferedReader;
import java.util.concurrent.Phaser;

public class SwitchParserThread implements Runnable {

  String line;
  String type;
  Phaser ph;
  int weight;

  public SwitchParserThread(String line, Phaser ph, int weight, String type) {
    this.line = line;
    this.ph = ph;
    this.weight = weight;
    this.type = type;
    ph.register();
    System.out.println("Switch Parser registering");
  }

  @Override
  public void run() {

    System.out.println("Switch parser beginning execution");

    int score = 0;
    int comparisonCount = 0; // switch case cannot exist without at least 1 comparison surely
int commaCount =0;

    // phaser arrive and await

    System.out.println("Switch Parser arrving and waiting for advance");

    ph.arriveAndAwaitAdvance();

    ph.arriveAndAwaitAdvance();
    this.line = Main.getLine();
    System.out.println("Switch case line is : " + line);

    System.out.println("Switch Parser advancing");

    while (true) {

      System.out.println("Switch case line is : " + line);

      if (line.contains("case")) { // add support for multiple inline cases
        System.out.println("Case found");

        for(int i=0; i< line.length(); i++){
          char c = line.charAt(i);
          //we need to check for multiple inline comparisons, which are seperated by commas
          if (c == 44){ //ascii for a comma
            commaCount++;
          }
        }

        comparisonCount = comparisonCount + commaCount + 1; //there will always be n+1 comparisons for every comma

        System.out.println("Comparison count is now : " + comparisonCount);
      }
      if (line.contains("default")) {
        System.out.println("Default found");
        break;
      }

      System.out.println("Switch Parser arriving and waiting for advance");
      ph.arriveAndAwaitAdvance();

      ph.arriveAndAwaitAdvance();
      this.line = Main.getLine();

      System.out.println("Switch Parser advancing");
    }

    if (comparisonCount == 0) {
      comparisonCount = 1; // just in case switch case exists without comparisons
    }

    score = weight * comparisonCount;

    Result.addResult(new Result(score, type));

    System.out.println("Switch parser unregistering");
    ph.arriveAndDeregister();
  }
}
