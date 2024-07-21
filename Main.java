package production;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class Main {

  static String sharedLine;

  public static void main(String[] args) {

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    String[] controlStructures = {"for", "while", "if", "switch"};
    Phaser ph = new Phaser(1);

    try (BufferedReader br =
        new BufferedReader(new FileReader("C:\\Users\\test\\IdeaProjects\\test\\src\\Main2.java")))
    // new BufferedReader(new FileReader("C:\\Users\\test\\Desktop\\streamtest.txt")))
    {
      int weight = 0;
      String line;
      line = br.readLine();
      sharedLine = line; // if no comments are found, sharedLine will be the same as line
      while (line != null) {

        // Check for single line comments

        if (line.contains("//") || line.contains("/*") || line.contains("\"")) {

          System.out.println("Comment detected in line : " + line);

          /*
           *False positive checking. will remove any characters within quotation marks in case there is a "//" inside quotes.
           *Additionally, will remove the single line comment to prevent false positives during processing
           */

          StringBuilder stringBuilder =
              new StringBuilder(); // doesn't matter if string builder is initialized in the while
          // loop
          for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            /*
             *If "//" is found before quotation marks, then loop is broken as comment is found.
             *This is also to prevent false positives from singular quotation marks that may be in comments.
             *Example : // this is a single quotation "
             */

            if (c == 47) { // ascii for /
              System.out.println("/ Detected");
              // Checking immediate next character for a "//" pair
              if (line.charAt(i + 1) == 47) { // ascii for /
                System.out.println("// Comment Detected.");
                break;
              }

              /*
               * Check for a multiline comment
               */

              if (line.charAt(i + 1) == 42) { // ascii for *
                System.out.println("/* Multiline starting comment detected.");
                while (true) {
                  // adjust counters
                  if (line.contains("*/")) {
                    System.out.println("*/ Multiline ending comment detected. New line : " + line);
                    // in case line terminates at */, +1 will prevent a String Index Out Of Range
                    // Exception
                    i = line.indexOf("*/") + 1;
                    c = line.charAt(i);
                    break;
                  }
                  line = br.readLine();
                }
              }
            }

            // add support for multi line quotations
            if (c == 34) { // ascii for "
              System.out.println("Quotation Detected");
              // If " is found, skips over characters until the ending " is found.
              while (true) {
                i++;
                if (i < line.length()) {
                  c = line.charAt(i);
                } else {
                  System.out.println("End Quotation not detected. Moving to next line.");
                  line = br.readLine();
                  System.out.println("New Line: " + line);
                  i = 0;
                }
                // account for \"
                if (c == 92) { // ascii for \
                  if (line.charAt(i + 1) == 34) {
                    System.out.println(" \" Detected. Skipping.");
                    i = i + 2;
                    c = line.charAt(i);
                  }
                }

                if (c == 34) { // ascii for "
                  i++;
                  System.out.println("End Quotation found. Breaking quotation loop.");
                  // Ending quote found, breaking out of sub loop.
                  break;
                }
              }
            } else {
              stringBuilder.append(c);
            }
          }
          /*
           * Now that validation has ended, we have removed any comments or quotations that may interfere
           * with
           * checking for control structures.
           * Sending the current line, minus comments, off for processing
           */
          System.out.println("Comment checking completed.");
          System.out.println("Line with comments : " + line);
          line = stringBuilder.toString();
          sharedLine = line;
          System.out.println("Line without comments : " + line);
        } else {
          sharedLine = line;
        }

        ph.arriveAndAwaitAdvance();

        for (String structure : controlStructures) {

          if (line.contains(structure)) {

            switch (structure) {
              case "if":
                weight = 2;
                executorService.submit(new GeneralParserThread(line, ph, weight, structure));
                break;
              case "for",
                  "while": // multiple comparisons inline. doesn't matter because variable contains
                // the actual structure
                weight = 3;
                executorService.submit(new GeneralParserThread(line, ph, weight, structure));
                break;
              case "switch":
                weight = 2;
                executorService.submit(new SwitchParserThread(line, ph, weight, structure));
                break;
              default:
                weight = 0;
            }
            System.out.println("Structure is : " + structure);
            System.out.println("Weight is " + weight);
          }
        }

        // Two phaser advances here to make sure that the same line is delivered to all threads in
        // case of desynchronization
        // Might not be required. Don't know if the class that instantiates phaser is always
        // executed first
        line = br.readLine();
        ph.arriveAndAwaitAdvance();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    System.out.println("End of file reached.");
    System.out.println("Main process unregistering from parser");
    ph.arriveAndDeregister();
    System.out.println("Executor Service Shutting down");
    executorService.shutdown();

    // End of program, time to go through results

    Result.results.forEach(System.out::println);
  }

  public static String getLine() {
    return sharedLine;
  }
}
