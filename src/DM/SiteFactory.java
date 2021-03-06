package DM;

import TM.ConstantValue;

/**
 * @Author: Tian Zhao, Qianran Sun
 * @Date: 12/7/2017
 * @General Description: The SiteFactory is used to build sites and variables.
 */
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
      if (i % 2 == 0) {
        for (int j = 1; j <= ConstantValue.SiteNum; ++j) {
          Variable var = new Variable(i);
          sites[j].variables[i] = var;
        }
      } else {
        Variable var = new Variable(i);
        sites[1 + i % 10].variables[i] = var;
      }
    }
    return sites;
  }
}
