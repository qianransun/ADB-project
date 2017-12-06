package DM;


import TM.Instruction;
import TM.InstructionType;
import org.junit.Test;

public class SiteEngineTest {
  @Test
  public void testGetReadLock() {
    SiteEngine engine = new SiteEngine();
    Instruction a = new Instruction();
    a.type = InstructionType.R;
    a.transactionIndex = 1;
    a.variableIndex = 1;
    Instruction b = new Instruction();
    b.type = InstructionType.R;
    b.transactionIndex = 2;
    b.variableIndex = 1;
    engine.getReadLock(a);

    Instruction c = new Instruction();
    c.type = InstructionType.W;
    c.transactionIndex = 2;
    c.variableIndex = 1;

    System.out.println(engine.getWriteLock(c));
    System.out.println(engine.getReadLock(b));
  }

  @Test
  public void testWriteThenRead() {
    SiteEngine engine = new SiteEngine();

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
    c.transactionIndex = 2;
    c.variableIndex = 2;

    engine.getReadLock(a);
    System.out.println(engine.getReadLock(b));
    System.out.println(engine.getWriteLock(c));
  }
}
