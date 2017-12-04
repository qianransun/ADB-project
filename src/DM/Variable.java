package DM;

import java.util.ArrayList;
import java.util.List;

public class Variable {
  private int index;
  int value;
  Lock lock;
  List<Integer> lockTable;
  List<Integer> waitList;

  Variable(int index) {
    this.index = index;
    this.value = 10 * index;
    lock = Lock.NONE;
    lockTable = new ArrayList<>();
    waitList = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "x" + index + ":" + value;
  }
}
