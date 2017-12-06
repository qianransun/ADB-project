package DM;

import TM.Instruction;
import TM.InstructionType;
import org.junit.Test;

public class VariableTest {
  @Test
  public void testVariable() {
    Variable x1 = new Variable(1);
    System.out.println(x1);
  }

  @Test
  public void testRemoveWaitList() {
    Variable var = new Variable(1);
    Instruction a = new Instruction();
    a.type = InstructionType.R;
    a.transactionIndex = 1;
    a.variableIndex = 2;

    Instruction b = new Instruction();
    b.type = InstructionType.R;
    b.transactionIndex = 2;
    b.variableIndex = 2;

    Instruction c = new Instruction();
    c.type = InstructionType.W;
    c.transactionIndex = 3;
    c.variableIndex = 2;

    var.waitList.offer(a);
    var.waitList.offer(b);
    var.waitList.offer(c);

    for (Instruction ins : var.waitList) {
      System.out.println(ins);
    }

    System.out.println();

    var.waitList.remove(b);

    for (Instruction ins : var.waitList) {
      System.out.println(ins);
    }
  }
}
