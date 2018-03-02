import java.io.*;
import java.util.*;

public class SpamTester{
  private Map<String, Float> probabilities;
  private Map<String,Integer> fileWords;
  private int fileCount;

  public SpamTester(){
    probabilities=new TreeMap<>();
  }
  public int getFileCount(){return this.fileCount;}

  public void processFile(File file) throws IOException{
    if (file.isDirectory()){
      File[] contents = file.listFiles();
      for (File current: contents){
        processFile(current);
      }

    }
    else if(file.exists()){
      fileCount++;
      Scanner scanner=new Scanner(file);
      fileWords=new TreeMap<>();
      scanner.useDelimiter("'|\\s|\"|,|:|!|\\?|\\.|-|\'");
      while(scanner.hasNext()){
        String word=scanner.next().toLowerCase();
        if (isWord(word)){
          wordAdd(word);
        }
      }
    }
  }
  //checks if string is a word
  private boolean isWord(String word) {
    String pattern = "^[a-zA-Z]+$";
    if (word.matches(pattern)) {
      return true;
    } else {
      return false;
    }
  }
  //adds the word to the map containing total occurance of word for all file
  //if it has not already been added to the map from the same file
  private void wordAdd(String word){
    if (!fileWords.containsKey(word)){
      fileWords.put(word,1);
      if (probabilities.containsKey(word)) {
        float oldCount = probabilities.get(word);
        probabilities.put(word, oldCount+1);
      } else {
        probabilities.put(word, 1f);
      }
    }
  }

  public static void main(String[] args) throws IOException{
    SpamTester hamTrain=new SpamTester();
    SpamTester spamTrain=new SpamTester();
    File hamDir=new File("data/train/ham");//check with ham2
    File spamDir=new File("data/train/spam");
    hamTrain.processFile(hamDir);
    spamTrain.processFile(spamDir);

  }
}
