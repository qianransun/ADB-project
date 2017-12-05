package DM;

import TM.ConstantValue;
import TM.Instruction;
import java.util.ArrayList;
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
      if (getWriteLockHelper(instruction, siteIndex)) {
        setWriteLock(instruction);
        return true;
      } else {
        addWaitList(instruction, siteIndex);
        return false;
      }
    } else {
      boolean lock = true;
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        lock &= getWriteLockHelper(instruction, i);
      }
      if (lock) {
        setWriteLock(instruction);
      } else {
        for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
          addWaitList(instruction, i);
        }
      }
      return lock;
    }
  }

  private void addWaitList(Instruction instruction, int siteIndex) {
    sites[siteIndex].variables[instruction.variableIndex].waitList.add(instruction);
  }

  private boolean getWriteLockHelper(Instruction instruction, int siteIndex) {
    switch (sites[siteIndex].variables[instruction.variableIndex].lock) {
      case WRITE:
        return sites[siteIndex].variables[instruction.variableIndex].lockTable.get(0) ==
            instruction.transactionIndex;
      case READ:
        return sites[siteIndex].variables[instruction.variableIndex].lockTable.size() != 1 ||
            sites[siteIndex].variables[instruction.variableIndex].lockTable.get(0) !=
                instruction.transactionIndex;
      default:
        return true;
    }
  }

  private void setWriteLock(Instruction instruction) {
    if (instruction.variableIndex % 2 != 0) {
      int siteIndex = 1 + (instruction.variableIndex % 10);
      setWriteLockHelper(instruction.variableIndex, instruction.transactionIndex, siteIndex);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        setWriteLockHelper(instruction.variableIndex, instruction.transactionIndex, i);
      }
    }
  }

  private void setWriteLockHelper(int variableIndex, int transactionIndex, int siteIndex) {
    sites[siteIndex].variables[variableIndex].lock = Lock.WRITE;
    sites[siteIndex].variables[variableIndex].lockTable.add(transactionIndex);
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
   * Release all locks on variable xi obtained by transaction Ti.
   * @param variableIndex the index of the variable
   * @param transactionIndex the index of the transaction
   */
  public List<Instruction> releaseLock(int variableIndex, int transactionIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      lockReleaseHelper(variableIndex, transactionIndex, siteIndex);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        lockReleaseHelper(variableIndex, transactionIndex, i);
      }
    }
    return dequeWaitList(variableIndex);
  }

  private void lockReleaseHelper(int variableIndex, int transactionIndex, int siteIndex) {
    switch (sites[siteIndex].variables[variableIndex].lock) {
      case WRITE:
        sites[siteIndex].variables[variableIndex].lock = Lock.NONE;
        sites[siteIndex].variables[variableIndex].lockTable.clear();
        break;
      case READ:
        sites[siteIndex].variables[variableIndex].lockTable.remove(new Integer(transactionIndex));
        if (sites[siteIndex].variables[variableIndex].lockTable.size() == 0) {
          sites[siteIndex].variables[variableIndex].lock = Lock.NONE;
        }
        break;
    }
  }

  /**
   * For each released variable, we try to deque its waitlist if there exists one.
   * @param variableIndex the index of the variable
   * @return the list of instructions that have acquired their lock
   */
  private List<Instruction> dequeWaitList(int variableIndex) {
    List<Instruction> acquiredLocks = new ArrayList<>();
    int siteIndex = 1;

    return acquiredLocks;
  }
  private void dequeWaitListHelper(int variableIndex, int siteIndex,
      List<Instruction> acquiredLocks) {
    for (Instruction instruction : sites[siteIndex].variables[variableIndex].waitList) {
      switch (instruction.type) {
        case W:
          if (getWriteLock(instruction)) {
            acquiredLocks.add(instruction);
            return;
          }
          break;
      }
    }
  }

  public void removeWaiting(Instruction instruction) {
    if (instruction.variableIndex % 2 != 0) {
      removeWaitingHelper(instruction, 1 + (instruction.variableIndex % 10));
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        removeWaitingHelper(instruction, i);
      }
    }
  }

  private void removeWaitingHelper(Instruction instruction, int siteIndex) {
    sites[siteIndex].variables[instruction.variableIndex].waitList.remove(instruction);
  }

  public List<Integer> getLockTable(int variableIndex) {
    int siteIndex = variableIndex % 2 != 0 ? 1 + (variableIndex % 10) : 1;
    return sites[siteIndex].variables[variableIndex].lockTable;
  }
}
