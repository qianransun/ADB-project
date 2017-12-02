package DM;

public class Variable {
  private int index;
  int value;

  Variable(int index) {
    this.index = index;
    this.value = 10 * index;
  }

  @Override
  public String toString() {
    return "x" + index + ":" + value;
  }
}
