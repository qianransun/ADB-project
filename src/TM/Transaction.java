package TM;

import DM.Lock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {
  List<Instruction> instructionList;
  private int index;
  boolean isRO;
  Lock[] lockTable;
  Instruction waiting;
  Map<Integer, Integer> changeList;
  TransactionStatus status;

  Transaction(int index, boolean isRO) {
    this.index = index;
    instructionList = new ArrayList<>();
    this.isRO = isRO;
    lockTable = new Lock[ConstantValue.VariableNum + 1];
    waiting = null;
    changeList = new HashMap<>();
    status = TransactionStatus.RUN;
  }

  public void setTransactionAborted() {
    changeList.clear();
    status = TransactionStatus.ABORTED;
    waiting = null;
  }

  public void removeWaiting() {
    switch (waiting.type) {
      case W:
        lockTable[waiting.variableIndex] = Lock.WRITE;
        changeList.put(waiting.variableIndex, waiting.value);
        waiting = null;
        break;
      case R:
        lockTable[waiting.variableIndex] = Lock.READ;
        waiting = null;
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("T");
    builder.append(index);
    builder.append('\n');
    for (Instruction instruction : instructionList) {
      builder.append(instruction);
      builder.append('\n');
    }
    return builder.toString();
  }
}
