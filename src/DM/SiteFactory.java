package DM;

import TM.ConstantValue;

public class SiteFactory {

  /**
   * Construct method for sites and variables. Put variables into sites accordingly.
   * @return an array of sites.
   */
  public static Site[] constructSites() {
    Site[] sites = new Site[ConstantValue.SiteNum + 1];
    for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
      sites[i] = new Site();
    }
    for (int i = 1; i <= ConstantValue.VariableNum; ++i) {
      Variable var = new Variable(i);
      if (i % 2 == 0) {
        for (int j = 1; j <= ConstantValue.SiteNum; ++j) {
          sites[j].variableList.add(var);
        }
      } else {
        sites[1 + i % 10].variableList.add(var);
      }
    }
    return sites;
  }
}
