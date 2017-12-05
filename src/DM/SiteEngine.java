package DM;

import TM.ConstantValue;
import TM.Instruction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteEngine {
  private Site[] sites;

  public SiteEngine() {
    sites = SiteFactory.constructSites();
  }

  /**
   * Try to get required write locks. If can, locks are set. Otherwise, the instruction is put into
   * waitlist.
   * @param instruction the instruction to be executed.
   * @return true, if locks can be set. Otherwise, false.
   */
  public boolean getWriteLock(Instruction instruction) {
    if (instruction.variableIndex % 2 != 0) {
      int siteIndex = 1 + (instruction.variableIndex % 10);
      if (sites[siteIndex].variables[instruction.variableIndex].lock == Lock.NONE) {
        setWriteLock(instruction);
        return true;
      } else if (sites[siteIndex].variables[instruction.variableIndex].lock == Lock.WRITE &&
          sites[siteIndex].variables[instruction.variableIndex].lockTable.get(0) ==
          instruction.transactionIndex) {
        return true;
      } else if (sites[siteIndex].variables[instruction.variableIndex].lock == Lock.READ) {
        // There exists a read lock and is shared by other transaction
        if (sites[siteIndex].variables[instruction.variableIndex].lockTable.size() != 1 ||
            sites[siteIndex].variables[instruction.variableIndex].lockTable.get(0) !=
            instruction.transactionIndex) {
          sites[siteIndex].variables[instruction.variableIndex].waitList.add(instruction);
          return false;
        }
        setWriteLock(instruction);
        return true;
      } else {
        sites[siteIndex].variables[instruction.variableIndex].waitList.add(instruction);
        return false;
      }
    } else {
      boolean lock = true;
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        if (sites[i].variables[instruction.variableIndex].lock == Lock.WRITE) {
          // The write lock is not taken by this transaction
          if (sites[i].variables[instruction.variableIndex].lockTable.get(0) !=
              instruction.transactionIndex) {
            sites[i].variables[instruction.variableIndex].waitList.add(instruction);
            lock = false;
          }
        } else if (sites[i].variables[instruction.variableIndex].lock == Lock.READ){
          // There exists a read lock and is shared by other transaction
          if (sites[i].variables[instruction.variableIndex].lockTable.size() != 1 ||
              sites[i].variables[instruction.variableIndex].lockTable.get(0) !=
              instruction.transactionIndex) {
            sites[i].variables[instruction.variableIndex].waitList.add(instruction);
            lock = false;
          }
        }
      }
      if (lock) {
        setWriteLock(instruction);
      }
      return lock;
    }
  }

  private void setWriteLock(Instruction instruction) {
    if (instruction.variableIndex % 2 != 0) {
      int siteIndex = 1 + (instruction.variableIndex % 10);
      sites[siteIndex].variables[instruction.variableIndex].lock = Lock.WRITE;
      sites[siteIndex].variables[instruction.variableIndex].lockTable.
          add(instruction.transactionIndex);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        sites[i].variables[instruction.variableIndex].lock = Lock.WRITE;
        sites[i].variables[instruction.variableIndex].lockTable.add(instruction.transactionIndex);
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

  /**
   * Release locks on variable xi.
   * @param variableIndex the index of the variable
   */
  public void releaseLock(int variableIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      sites[siteIndex].variables[variableIndex].lock = Lock.NONE;
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        sites[i].variables[variableIndex].lock = Lock.NONE;
      }
    }
    dequeWaitList(variableIndex);
  }

  private void dequeWaitList(int variableIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      while (!sites[siteIndex].variables[variableIndex].waitList.isEmpty()) {
        // TODO
      }
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        // TODO
      }
    }
  }

  public List<Integer> getLockTable(int variableIndex) {
    int siteIndex = variableIndex % 2 != 0 ? 1 + (variableIndex % 10) : 1;
    return sites[siteIndex].variables[variableIndex].lockTable;
  }
}
