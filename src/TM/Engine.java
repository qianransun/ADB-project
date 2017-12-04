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

  public static void main(String[] args) throws IOException {
    Engine taskManager = new Engine();
    taskManager.initial("C:\\Users\\ztian\\Downloads\\ADB-project\\sample\\test13.txt");
    taskManager.run();
  }

  private void run() {
    SiteEngine siteEngine = new SiteEngine();
    for (Instruction instruction : instructionList) {
      switch (instruction.type) {
        case W:
          if (siteEngine.getWriteLock(instruction.variableIndex, instruction.transactionIndex)) {
            transactionList.get(instruction.transactionIndex - 1).
                lockTable[instruction.variableIndex] = Lock.WRITE;

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
          break;
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
  private boolean cycleDetect() {
    return false;
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
