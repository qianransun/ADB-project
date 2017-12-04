package DM;

import TM.ConstantValue;

public class SiteEngine {
  private Site[] sites;

  public SiteEngine() {
    sites = SiteFactory.constructSites();
  }

  /**
   * Try to get required write locks. If can, locks are set. Otherwise, the operation is rejected.
   * @param variableIndex the index of the variable
   * @param transactionIndex the index of the transaction
   * @return true, if locks can be set. Otherwise, false.
   */
  public boolean getWriteLock(int variableIndex, int transactionIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      if (sites[siteIndex].variables[variableIndex].lock == Lock.NONE) {
        setWriteLock(variableIndex, transactionIndex);
        return true;
      } else if (sites[siteIndex].variables[variableIndex].lock == Lock.WRITE &&
          sites[siteIndex].variables[variableIndex].lockTable.get(0) == transactionIndex) {
        return true;
      } else if (sites[siteIndex].variables[variableIndex].lock == Lock.READ) {
        // There exists a read lock and is shared by other transaction
        if (sites[siteIndex].variables[variableIndex].lockTable.size() != 1 ||
            sites[siteIndex].variables[variableIndex].lockTable.get(0) != transactionIndex) {
          return false;
        }
        setWriteLock(variableIndex, transactionIndex);
        return true;
      } else {
        return false;
      }
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        if (sites[i].variables[variableIndex].lock == Lock.WRITE) {
          // The write lock is not taken by this transaction
          if (sites[i].variables[variableIndex].lockTable.get(0) != transactionIndex) {
            return false;
          }
        } else if (sites[i].variables[variableIndex].lock == Lock.READ){
          // There exists a read lock and is shared by other transaction
          if (sites[i].variables[variableIndex].lockTable.size() != 1 ||
              sites[i].variables[variableIndex].lockTable.get(0) != transactionIndex) {
            return false;
          }
        }
      }
      setWriteLock(variableIndex, transactionIndex);
      return true;
    }
  }

  private void setWriteLock(int variableIndex, int transactionIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      sites[siteIndex].variables[variableIndex].lock = Lock.WRITE;
      sites[siteIndex].variables[variableIndex].lockTable.add(transactionIndex);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        sites[i].variables[variableIndex].lock = Lock.WRITE;
        sites[i].variables[variableIndex].lockTable.add(transactionIndex);
      }
    }
  }

  /**
   * Try to get required write locks. If can, locks are set. Otherwise, the operation is rejected.
   * @param variableIndex the index of the variable
   * @param transactionIndex the index of the transaction
   * @return true, if locks can be set. Otherwise, false.
   */
  public boolean getReadLock(int variableIndex, int transactionIndex) {
    return false;
  }

  /**
   * Print out the commited values of all copies of all variables.
   */
  public void dump() {

  }

  /**
   * Print the committed values of all copies of variables xi at all sites.
   * @param index the index of the variable
   */
  public void dumpVar(int index) {

  }

  /**
   * Print the committed value of all copies of all variables at site i.
   * @param index the index of the site
   */
  public void dumpSite(int index) {

  }

  /**
   * Set the status of site i to fail.
   * @param index the index of the site.
   */
  public void setSiteFail(int index) {

  }

  /**
   * Set the status of the site i to recover. Note that the recover site still accepts no read
   * operation until a committed write operation.
   * @param index the index of the site.
   */
  public void setSiteRecover(int index) {

  }
}
