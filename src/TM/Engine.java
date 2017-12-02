package TM;

import DM.Variable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Engine {
  private void readFile(String inputFile) throws IOException{
    FileInputStream fis = new FileInputStream(inputFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    String line;
    while ((line = br.readLine()) != null) {
      System.out.println(line);
    }
    br.close();
  }

  public static void main(String[] args) throws IOException {
    Engine taskManager = new Engine();
    taskManager.readFile("C:\\Users\\ztian\\Downloads\\ADB-project\\sample\\test1.txt");
  }
}
