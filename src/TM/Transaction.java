package TM;

import DM.Lock;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
  List<Instruction> instructionList;
  private int index;
  boolean isRO;
  Lock[] lockTable;

  Transaction(int index, boolean isRO) {
    this.index = index;
    instructionList = new ArrayList<>();
    this.isRO = isRO;
    lockTable = new Lock[ConstantValue.VariableNum + 1];
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
