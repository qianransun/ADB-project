package DM;

import TM.ConstantValue;
import TM.Instruction;

import java.util.*;

public class SiteEngine {
  private Site[] sites;
  private List<List<Instruction>> waitList;

  public SiteEngine() {
    sites = SiteFactory.constructSites();
    initalWaitList();
  }

  private void initalWaitList() {
    waitList = new ArrayList<>();
    for (int i = 0; i <= ConstantValue.VariableNum; ++i) {
      waitList.add(new ArrayList<>());
    }
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
        addWaitList(instruction);
        return false;
      }
    } else {
      boolean canLock = true;
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        if (sites[i].variables[instruction.variableIndex].status != SiteStatus.FAIL
            && !getWriteLockHelper(instruction, i)) {
          canLock = false;
          break;
        }
      }
      if (canLock) {
        setWriteLock(instruction);
      } else {
        addWaitList(instruction);
      }
      return canLock;
    }
  }

  private void addWaitList(Instruction instruction) {
    waitList.get(instruction.variableIndex).add(instruction);
  }

  private boolean getWriteLockHelper(Instruction instruction, int siteIndex) {
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    if (var.status == SiteStatus.FAIL) {
      return false;
    }
    switch (var.lock) {
      case NONE:
        return true;
      default:
        return var.lockTable.size() == 1 && var.lockTable.contains(instruction.transactionIndex);
    }
  }

  private void setWriteLock(Instruction instruction) {
    if (instruction.variableIndex % 2 != 0) {
      int siteIndex = 1 + (instruction.variableIndex % 10);
      setWriteLockHelper(instruction.variableIndex, instruction.transactionIndex, siteIndex);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        if (sites[i].variables[instruction.variableIndex].status != SiteStatus.FAIL) {
          setWriteLockHelper(instruction.variableIndex, instruction.transactionIndex, i);
        }
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
    int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10) :
            findFirstUpSite(instruction.variableIndex);
    if (getReadLockHelper(instruction, siteIndex)) {
      setReadLock(instruction);
      return true;
    } else {
      addWaitList(instruction);
      return false;
    }
  }

  private int findFirstUpSite(int variableIndex) {
    for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
      if (sites[i].variables[variableIndex].status == SiteStatus.UP) {
        return i;
      }
    }
    return 1;
  }

  private boolean getReadLockHelper(Instruction instruction, int siteIndex) {
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    if (sites[siteIndex].variables[instruction.variableIndex].status != SiteStatus.UP) {
      return false;
    }
    switch (var.lock) {
      case WRITE:
        return var.lockTable.size() == 1 && var.lockTable.contains(instruction.transactionIndex);
      default:
        return waitList.get(instruction.variableIndex).isEmpty();
    }
  }

  private void setReadLock(Instruction instruction) {
    int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10) :
        findFirstUpSite(instruction.variableIndex);
    Variable var = sites[siteIndex].variables[instruction.variableIndex];
    if (var.lock == Lock.NONE) {
      var.lock = Lock.READ;
    }
    var.lockTable.add(instruction.transactionIndex);
  }

  /**
   * Print out the committed values of all copies of all variables.
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
        System.out.println(sites[i].variables[index]);
      }
    } else {
      System.out.println("Site " + (1 + index % 10));
      System.out.println(sites[1 + index % 10].variables[index]);
    }
  }

  /**
   * Set the status of site i to fail. Actually, we are setting the status of each variable to fail.
   * Accumulate all transactions that have acquired lock in that site.
   * Erase the lock table in that site.
   * @param siteIndex the index of the site.
   * @return list of transaction index.
   */
  public Set<Integer> setSiteFail(int siteIndex) {
    Set<Integer> transactionToAbort = new HashSet<>();
    for (int i = 1; i <= ConstantValue.VariableNum; ++i) {
      Variable var = sites[siteIndex].variables[i];
      if (var != null) {
        var.status = SiteStatus.FAIL;
        if (var.lock != Lock.NONE) {
          transactionToAbort.addAll(var.lockTable);
          var.lockTable.clear();
          var.lock = Lock.NONE;
        }
      }
    }
    return transactionToAbort;
  }

  /**
   * Set the status of the site i to recover. Note that the recover site still accepts no read
   * operation until a committed write operation.
   * @param siteIndex the index of the site.
   */
  public List<Instruction> setSiteRecover(int siteIndex) {
    List<Instruction> acquiredLocks = new ArrayList<>();
    for (int i = 1; i <= ConstantValue.VariableNum; ++i) {
      Variable var = sites[siteIndex].variables[i];
      if (var != null) {
        if (i % 2 != 0) {
          var.status = SiteStatus.UP;
        } else {
          var.status = SiteStatus.RECOVER;
        }
        acquiredLocks.addAll(dequeWaitList(i));
      }
    }
    return acquiredLocks;
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
        sites[siteIndex].variables[variableIndex].lockTable.remove(transactionIndex);
        if (sites[siteIndex].variables[variableIndex].lockTable.size() == 0) {
          sites[siteIndex].variables[variableIndex].lock = Lock.NONE;
        }
        break;
    }
  }

  /**
   * For each released variable, we try to deque its wait list if there exists one.
   * @param variableIndex the index of the variable
   * @return the list of instructions that have acquired their lock
   */
  private List<Instruction> dequeWaitList(int variableIndex) {
    List<Instruction> acquiredLocks = new ArrayList<>();
    List<Instruction> wait = waitList.get(variableIndex);
    for (int i = 0; i < wait.size(); ++i) {
      Instruction instruction = wait.get(i);
      switch (instruction.type) {
        case W:
          if (variableIndex % 2 != 0) {
            int siteIndex = 1 + (variableIndex % 10);
            if (getWriteLockHelper(instruction, siteIndex)) {
              setWriteLock(instruction);
              acquiredLocks.add(instruction);
              wait.remove(instruction);
              i--;
            }
          } else {
            boolean canLock = true;
            for (int j = 1; j <= ConstantValue.SiteNum; ++j) {
              if (sites[j].variables[instruction.variableIndex].status != SiteStatus.FAIL
                  && !getWriteLockHelper(instruction, j)) {
                canLock = false;
                break;
              }
            }
            if (canLock) {
              setWriteLock(instruction);
              acquiredLocks.add(instruction);
              wait.remove(instruction);
            }
          }
          break;
        case R:
          int siteIndex = instruction.variableIndex % 2 != 0 ? 1 + (instruction.variableIndex % 10)
              : findFirstUpSite(instruction.variableIndex);
          if (sites[siteIndex].variables[variableIndex].status == SiteStatus.UP && (i == 0 &&
              sites[siteIndex].variables[variableIndex].lock != Lock.WRITE ||
              getReadLockHelper(instruction, siteIndex))) {
            setReadLock(instruction);
            acquiredLocks.add(instruction);
            wait.remove(instruction);
            i--;
          }
          break;
      }
    }
    return acquiredLocks;
  }

  public void removeWaiting(Instruction instruction) {
    waitList.get(instruction.variableIndex).remove(instruction);
  }

  public Set<Integer> getLockTable(int variableIndex) {
    Set<Integer> lockTable = new HashSet<>();
    if (variableIndex % 2 != 0) {
      lockTable.addAll(sites[1 + (variableIndex % 10)].variables[variableIndex].lockTable);
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        lockTable.addAll(sites[i].variables[variableIndex].lockTable);
      }
    }
    return lockTable;
  }

  public void writeVariable(int variableIndex, int value, int transactionIndex) {
    if (variableIndex % 2 != 0) {
      int siteIndex = 1 + (variableIndex % 10);
      sites[siteIndex].variables[variableIndex].value = value;
    } else {
      for (int i = 1; i <= ConstantValue.SiteNum; ++i) {
        if (sites[i].variables[variableIndex].lockTable.contains(transactionIndex)) {
          sites[i].variables[variableIndex].value = value;
          if (sites[i].variables[variableIndex].status == SiteStatus.RECOVER) {
            sites[i].variables[variableIndex].status = SiteStatus.UP;
          }
        }
      }
    }
  }

  public int getVariableValue(int variableIndex) {
    int siteIndex = variableIndex % 2 != 0 ? 1 + (variableIndex % 10) :
      findFirstUpSite(variableIndex);
    return sites[siteIndex].variables[variableIndex].value;
  }
}
