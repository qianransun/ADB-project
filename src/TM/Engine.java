package TM;

import DM.Lock;
import DM.SiteEngine;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Engine {
  private List<Instruction> instructionList = new ArrayList<>();
  private List<Transaction> transactionList = new ArrayList<>();
  private SiteEngine siteEngine = new SiteEngine();

  public static void main(String[] args) throws IOException {
    Engine taskManager = new Engine();
    taskManager.initial("C:\\Users\\ztian\\Downloads\\ADB-project\\sample\\test1.txt");
    taskManager.run();
    taskManager.printOut();
  }

  private void printOut() {
    for (Instruction instruction : instructionList) {
      switch (instruction.type) {
        case R:
          System.out.println("x" + instruction.variableIndex + "'s value is"+ instruction.value);
          break;
        case END:
          if (instruction.value == 1) {
            System.out.println("T" + instruction.transactionIndex + " can commit.");
          } else {
            System.out.println("T" + instruction.transactionIndex + " cannot commit.");
          }
      }
    }
  }

  private void run() {
    for (Instruction instruction : instructionList) {
      switch (instruction.type) {
        case W:
          if (siteEngine.getWriteLock(instruction)) {
            transactionList.get(instruction.transactionIndex - 1).
                lockTable[instruction.variableIndex] = Lock.WRITE;
            transactionList.get(instruction.transactionIndex - 1).changeList.
                put(instruction.variableIndex, instruction.value);
          } else {
            transactionList.get(instruction.transactionIndex - 1).waiting = instruction;
            if (cycleDetect(instruction.transactionIndex)) {
              System.out.println("Cycle!" + instruction);
            }
          }
          break;
        case R:
          break;
        case DUMP:
          break;
        case FAIL:
          break;
        case RECOVER:
          break;
        case END:
          if (transactionList.get(instruction.transactionIndex - 1).waiting == null) {
            // 1 represents can commit in this case.
            instruction.result.add(1);
            releaseLocks(instruction.transactionIndex);
          } else {
            instruction.result.add(0);
          }
          break;
      }
    }
  }

  private void releaseLocks(int transactionIndex) {
    for (int i = 1; i <= ConstantValue.VariableNum; ++i) {

      if (transactionList.get(transactionIndex - 1).lockTable[i] != null) {
        List<Instruction> acquiredLocks = siteEngine.releaseLock(i, transactionIndex);
        for (Instruction instruction : acquiredLocks) {
          transactionList.get(instruction.transactionIndex - 1).removeWaiting();
        }
        transactionList.get(transactionIndex - 1).lockTable[i] = null;
      }
    }
  }


  /**
   * Check if Ti can commit.
   * @param index the index of the transaction
   * @return true, if the transaction can be committed. Otherwise, return false.
   */
  private boolean canCommit(int index) {
    return false;
  }

  /**
   * Check if transactions are deadlocked.
   * @return true, if there exists a cycle. Otherwise, false.
   */
  private boolean cycleDetect(int transactionIndex) {
    boolean[] marked = new boolean[transactionList.size() + 1];
    int[] edgeTo = new int[transactionList.size() + 1];
    List<Integer> cycle = new ArrayList<>();
    boolean[] onStack = new boolean[transactionList.size() + 1];
    DFS(marked, edgeTo, onStack, transactionIndex, cycle);
    if (cycle.size() != 0) {
      abortTransaction(getYoungestTransaction(cycle));
      return true;
    }
    return false;
  }

  private int getYoungestTransaction(List<Integer> cycle) {
    int max = cycle.get(0);
    for (int i = 1; i < cycle.size(); ++i) {
      if (max < cycle.get(i)) {
        max = cycle.get(i);
      }
    }
    return max;
  }

  private void abortTransaction(int transactionIndex) {
    siteEngine.removeWaiting(transactionList.get(transactionIndex - 1).waiting);
    releaseLocks(transactionIndex);
    transactionList.get(transactionIndex - 1).setTransactionAborted();
  }


  /**
   * Use DFS to traverse the graph and detect the cycle. Transactions in the cycle is stored in the
   * list named cycle.
   * @param marked whether the transaction has been visited.
   * @param edgeTo record the father node of the transaction
   * @param onStack whether the transaction is in the cycle
   * @param transactionIndex the index of the transaction
   * @param cycle used to store the cycle of transactions
   */
  private void DFS(boolean[] marked, int[] edgeTo, boolean[] onStack, int transactionIndex,
      List<Integer> cycle) {
    onStack[transactionIndex] = true;
    marked[transactionIndex] = true;
    List<Integer> lockTable = transactionList.get(transactionIndex - 1).
        waiting == null ? new ArrayList<>() : siteEngine.getLockTable(transactionList.
        get(transactionIndex - 1).waiting.variableIndex);
    for (Integer index : lockTable) {
      if (cycle.size() != 0) {
        return;
      }
      if (!marked[index]) {
        edgeTo[index] = transactionIndex;
        DFS(marked, edgeTo, onStack, index, cycle);
      } else if (onStack[index]){
        for (int edge = transactionIndex; edge != index; edge = edgeTo[edge]) {
          cycle.add(edge);
        }
        cycle.add(index);
      }
    }
    onStack[transactionIndex] = false;
  }


  /**
   * Abort transactions according to fail site i.
   * @param index the index of the site.
   */
  private void failSiteAbort(int index) {

  }

  private void initial(String inputFile) throws IOException {
    readFile(inputFile, false);
    parseInstructions(true);
  }

  private void readFile(String inputFile, boolean verbose) throws IOException{
    FileInputStream fis = new FileInputStream(inputFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    String line;
    int[] pos = new int[1];
    while ((line = br.readLine()) != null) {
      pos[0] = 0;
      Instruction instruction = new Instruction();
      switch (line.charAt(pos[0])) {
        case 'b':
          if (line.charAt(pos[0] + 5) == '(') {
            instruction.type = InstructionType.BEGIN;
          } else {
            instruction.type = InstructionType.BEGINRO;
          }
          instruction.transactionIndex = parseIndex(line, pos);
          break;
        case 'W':
          instruction.type = InstructionType.W;
          instruction.transactionIndex = parseIndex(line, pos);
          instruction.variableIndex = parseIndex(line, pos);
          instruction.value = parseIndex(line, pos);
          break;
        case 'e':
          instruction.type = InstructionType.END;
          instruction.transactionIndex = parseIndex(line, pos);
          break;
        case 'd':
          instruction.type = InstructionType.DUMP;
          if (line.charAt(pos[0] + 5) == 'x') {
            instruction.variableIndex = parseIndex(line, pos);
          } else if (isDigit(line.charAt(pos[0] + 5))) {
            instruction.value = parseIndex(line, pos);
          }
          break;
        case 'R':
          instruction.type = InstructionType.R;
          instruction.transactionIndex = parseIndex(line, pos);
          instruction.variableIndex = parseIndex(line, pos);
          break;
        case 'f':
          instruction.type = InstructionType.FAIL;
          instruction.value = parseIndex(line, pos);
          break;
        case 'r':
          instruction.type = InstructionType.RECOVER;
          instruction.value = parseIndex(line, pos);
      }
      instructionList.add(instruction);
    }
    if (verbose) {
      for (Instruction instruction : instructionList) {
        System.out.println(instruction);
      }
    }
    br.close();
  }

  private void parseInstructions(boolean verbose) {
    for (Instruction instruction : instructionList) {
      switch (instruction.type) {
        case BEGIN:
          transactionList.add(new Transaction(instruction.transactionIndex, false));
          break;
        case BEGINRO:
          transactionList.add(new Transaction(instruction.transactionIndex, true));
          break;
        case END: case R: case W:
          transactionList.get(instruction.transactionIndex - 1).instructionList.add(instruction);
          break;
      }
    }
    if (verbose) {
      for (Transaction transaction : transactionList) {
        System.out.println(transaction);
      }
    }
  }

  private void nextDigit(String line, int[] pos) {
    while (pos[0] < line.length() && !isDigit(line.charAt(pos[0]))) {
      pos[0]++;
    }
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private int parseIndex(String line, int[] pos) {
    nextDigit(line, pos);
    int index = 0;
    while (isDigit(line.charAt(pos[0]))) {
      index = index * 10 + line.charAt(pos[0]) - '0';
      pos[0]++;
    }
    return index;
  }
}
