package DM;

import TM.Instruction;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;

public class Variable {
  private int index;
  int value;
  Lock lock;
  Set<Integer> lockTable;
  Queue<Instruction> waitList;

  Variable(int index) {
    this.index = index;
    this.value = 10 * index;
    lock = Lock.NONE;
    lockTable = new HashSet<>();
    waitList = new ArrayDeque<>();
  }

  @Override
  public String toString() {
    return "x" + index + ":" + value;
  }
}
