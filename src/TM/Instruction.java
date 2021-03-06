package TM;

public class Instruction {
  public InstructionType type;
  public int variableIndex;
  public int transactionIndex;
  // Used as site index in dump(value) and target value in W(T2, x1, value);
  int value;

  public Instruction() {
    type = InstructionType.DEFAULT;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(type);
    switch (type) {
      case BEGIN: case BEGINRO: case END:
        builder.append(" T");
        builder.append(transactionIndex);
        break;
      case W:
        builder.append(" T");
        builder.append(transactionIndex);
        builder.append(" x");
        builder.append(variableIndex);
        builder.append(" ");
        builder.append(value);
        break;
      case R:
        builder.append(" T");
        builder.append(transactionIndex);
        builder.append(" x");
        builder.append(variableIndex);
        break;
      case DUMP:
        if (variableIndex != 0) {
          builder.append(" x");
          builder.append(variableIndex);
        } else if (value != 0) {
          builder.append(" ");
          builder.append(value);
        }
        break;
      case FAIL: case RECOVER:
        builder.append(" ");
        builder.append(value);
        break;
    }
    return builder.toString();
  }
}
