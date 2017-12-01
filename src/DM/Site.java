package DM;

import java.util.ArrayList;
import java.util.List;

public class Site {
  List<Variable> variableList;

  Site() {
    variableList = new ArrayList<>();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Variable var : variableList) {
      builder.append(var.toString());
      builder.append('\n');
    }
    return builder.toString();
  }

}
