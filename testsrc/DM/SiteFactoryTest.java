package DM;

import TM.ConstantValue;
import org.junit.Test;

public class SiteFactoryTest {
  @Test
  public void testSiteFactory() {
    Site[] sites = SiteFactory.constructSites();
    for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
      System.out.println("Site " + i);
      System.out.println(sites[i]);
    }
  }
}
