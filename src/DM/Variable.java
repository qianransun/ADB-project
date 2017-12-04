package DM;

public class Variable {
  private int index;
  private int value;
  private Lock lock;

  Variable(int index) {
    this.index = index;
    this.value = 10 * index;
    lock = Lock.NONE;
  }

  @Override
  public String toString() {
    return "x" + index + ":" + value + "; Lock:" + lock;
  }
}
