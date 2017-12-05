package DM;

import TM.Instruction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Variable {
  private int index;
  int value;
  Lock lock;
  List<Integer> lockTable;
  Queue<Instruction> waitList;

  Variable(int index) {
    this.index = index;
    this.value = 10 * index;
    lock = Lock.NONE;
    lockTable = new ArrayList<>();
    waitList = new ArrayDeque<>();
  }

  @Override
  public String toString() {
    return "x" + index + ":" + value;
  }
}
