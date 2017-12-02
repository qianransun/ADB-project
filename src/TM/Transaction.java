package TM;

import DM.Variable;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
  List<Instruction> instructionList;
  private int index;

  Transaction(int index) {
    this.index = index;
    instructionList = new ArrayList<>();
  }

}
