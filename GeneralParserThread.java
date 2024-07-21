package production;

import java.io.BufferedReader;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;

public class GeneralParserThread implements Runnable {

  String line;
  String type;
  Phaser ph;
  int weight;

  public GeneralParserThread(String line, Phaser ph, int weight, String type) {
    this.line = line;
    this.ph = ph;
    this.weight = weight;
    this.type = type;
    ph.register();
    System.out.println("General Thread parser registered");
  }

  @Override
  public void run() {
    // google-java-format will make sure opening bracket is on the same line
    // while body starts when a { is encountered

    // todo, account for other if statements

    System.out.println("Beginning Execution");

    int comparisonsCount = 1;
    boolean end = true;

    int score = 0;

    while (true) {

      if (line.contains("&&") || line.contains("||")) {

        System.out.println("Multiple comparisons found");

        end = false;

        for (int i = 0; i < line.length(); i++) {
          char c = line.charAt(i);

          if (c == 38) {
            // if a comparison exists then i will never overflow
            if (line.charAt(i + 1) == 38) {
              comparisonsCount++;
              i = i + 2;
            }
          }

          if (c == 124) {
            if (line.charAt(i + 1) == 124) {
              comparisonsCount++;
              i = i + 2;
            }
          }

          // if { is encountered, method block starts therefore no more comparisons
          if (c == 123) {
            end = true;
            break;
          }
        }
      } else {
        System.out.println("No multiple comparisons found.");
      }
      // break the loop if the start of the method block is found as moving to the next line is
      // not desired otherwise
      if (end) {
        break;
      }

      // phaser await here
      System.out.println("Arrived and waiting for Advance");
      ph.arriveAndAwaitAdvance();
      System.out.println("Advancing");
      this.line = Main.getLine();
      ph.arriveAndAwaitAdvance();
    }
    score = weight * comparisonsCount;
    Result.addResult(new Result(score, type));
    System.out.println("General Parser unregistering");
    ph.arriveAndDeregister();
  }
}
