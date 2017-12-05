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
   * wait list.
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
        if (!getWriteLockHelper(instruction, i)) {
          lock = false;
          break;
        }
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
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    switch (var.lock) {
      case WRITE:
        return var.lockTable.get(0) == instruction.transactionIndex;
      case READ:
        return var.lockTable.size() == 1 && var.lockTable.get(0) == instruction.transactionIndex;
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
   * Try to get required read locks. If can, locks are set. Otherwise, the instruction is put into
   * wait list.
   * @param instruction the instruction to be executed.
   * @return true, if locks can be set. Otherwise, false.
   */
  public boolean getReadLock(Instruction instruction) {
    int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10) : 1;
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    switch (var.lock) {
      case READ:
        if (!var.waitList.isEmpty()) {
          var.waitList.add(instruction);
          return false;
        }
        setReadLock(instruction);
        return true;
      case NONE:
        setReadLock(instruction);
        return true;
      default:
        var.waitList.add(instruction);
        return false;
    }
  }

  private void setReadLock(Instruction instruction) {
    int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10) : 1;
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    var.lock = Lock.READ;
    var.lockTable.add(instruction.transactionIndex);
  }

  /**
   * Print out the commited values of all copies of all variables.
   */
  public void dump() {
    for (int i = 1; i <= ConstantValue.SiteNum; i++) {
      System.out.println("Site " + i);
      System.out.println(sites[i]);
    }
  }

  /**
   * Print the committed value of all copies of all variables at site i.
   * @param index the index of the site
   */
  public void dumpSite(int index) {
    System.out.println("Site " + index);
    System.out.println(sites[index]);
  }

  /**
   * Print the committed values of all copies of variables xi at all sites.
   * @param index the index of the variable
   */
  public void dumpVar(int index) {
    if (index % 2 == 0) {
      for (int i = 1; i <= ConstantValue.SiteNum; i++) {
        System.out.println("Site " + i);
        System.out.println(sites[i]);
      }
    } else {
      System.out.println("Site " + 1 + index % 10);
      System.out.println(sites[1 + index % 10]);
    }
  }

  /**
   * Set the status of site i to fail.
   * @param index the index of the site.
   */
  public void setSiteFail(int index) {
    sites[index].status = SiteStatus.FAIL;
  }

  /**
   * Set the status of the site i to recover. Note that the recover site still accepts no read
   * operation until a committed write operation.
   * @param index the index of the site.
   */
  public void setSiteRecover(int index) {
    sites[index].status = SiteStatus.RECOVER;
  }

  /**
   * Release all locks of variable xi held by transaction Ti
   * @param variableIndex the index of the variable
   * @param transactionIndex the index of the transaction
   * @return list of instructions that have acquired locks due to the release of locks.
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
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      boolean flag = true;
      while (flag && !sites[siteIndex].variables[variableIndex].waitList.isEmpty()) {
        Instruction instruction = sites[siteIndex].variables[variableIndex].waitList.peek();
        switch (instruction.type) {
          case W:
            if (getWriteLockHelper(instruction, siteIndex)) {
              acquiredLocks.add(sites[siteIndex].variables[variableIndex].waitList.poll());
            }
            flag = false;
            break;
          case R:
            dequeReadInstruction(instruction, acquiredLocks);
            break;
        }
      }
    } else {
      boolean flag = true;
      while (flag && !sites[1].variables[variableIndex].waitList.isEmpty()) {
        Instruction instruction = sites[1].variables[variableIndex].waitList.peek();
        switch (instruction.type) {
          case W:
            boolean lock = true;
            for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
              if (!getWriteLockHelper(instruction, i)) {
                lock = false;
                break;
              }
            }
            if (lock) {
              acquiredLocks.add(sites[1].variables[variableIndex].waitList.poll());
              for (int i = 2; i <= ConstantValue.SiteNum; ++i) {
                sites[i].variables[variableIndex].waitList.poll();
              }
            }
            flag = false;
            break;
          case R:
            dequeReadInstruction(instruction, acquiredLocks);
            break;
        }
      }
    }
    return acquiredLocks;
  }

  private boolean dequeReadInstruction(Instruction instruction, List<Instruction> acquiredLocks) {
    int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10) : 1;
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    if (var.lock == Lock.NONE || var.lock == Lock.READ) {
      setReadLock(instruction);
      acquiredLocks.add(sites[siteIndex].variables[instruction.variableIndex].waitList.poll());
      return true;
    } else {
      return false;
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

  public void writeVariable(int variableIndex, int value) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      sites[siteIndex].variables[variableIndex].value = value;
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        sites[i].variables[variableIndex].value = value;
      }
    }
  }

  public int getVariableValue(int variableIndex) {
    int siteIndex = variableIndex % 2 != 0 ? 1 + (variableIndex % 10) : 1;
    return sites[siteIndex].variables[variableIndex].value;
  }
}
