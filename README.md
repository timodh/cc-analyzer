Cyclomatic Complexity Analyzer made for a university project.

- Formats code using Google's google-java-format to ensure code can be predictably analyzed.
- Prevents false positives by ignoring strings, and checks whether characters within a string like ', " or """ are actually the end of the string or not.
- Uses threads to seperate the logic used for if,for,while checks from switch case checks.
