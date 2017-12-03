package DM;

import TM.ConstantValue;
import java.util.ArrayList;
import java.util.List;

public class Site {
  Variable[] variables;

  Site() {
    variables = new Variable[ConstantValue.VariableNum + 1];
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Variable var : variables) {
      if (var != null) {
        builder.append(var.toString());
        builder.append('\n');
      }
    }
    return builder.toString();
  }

}
