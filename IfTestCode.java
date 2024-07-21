package production;

public class IfTestCode {

  int a = 0;

  public void method1() {
    if (true) {}

    if (true || true) {}

    for (int i = 0; i < 10; i++) {}

    while (true) {
      break;
    }

    switch (a) {
      case 1:
        break;
      default:
        break;
    }

    do {
      break;
    } while (true);

    do {
      {
      }
      {
      }
      break;
    } while (true);
  }
}
