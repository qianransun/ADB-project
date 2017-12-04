package DM;

import TM.ConstantValue;

public class SiteEngine {
  private Site[] sites;

  public SiteEngine() {
    sites = SiteFactory.constructSites();
  }

  /**
   * Try to get required locks. If can, locks are set. Otherwise, the operation is rejected.
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
}
