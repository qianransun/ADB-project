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
}
