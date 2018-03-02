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
  public Map<String, Integer> getFileWords(){return this.fileWords;}


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
      scanner.useDelimiter("'|\\s|,|:|!|\\?|\\.|-|\'");
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
    //create spam word probability map
    Map<String,Float> spamProb=new TreeMap<>();
    Set<String> spamKeys = spamTrain.probabilities.keySet();
    Iterator<String> spamKeyIterator = spamKeys.iterator();
    //runs through each word detected in spam files
    while (spamKeyIterator.hasNext()) {
      String key = spamKeyIterator.next();
      float occur = spamTrain.probabilities.get(key);
      //if spam word also appears in ham files
      if(hamTrain.probabilities.containsKey(key)){
        float probAppearSpam=(occur/spamTrain.getFileCount());
        float probAppearHam=(hamTrain.probabilities.get(key)/hamTrain.getFileCount());
        float probSpamWord=probAppearSpam/(probAppearHam+probAppearSpam);
        System.out.println("Word: "+ key+ " chance "+probSpamWord);
        spamProb.put(key,probSpamWord);
      }
      //if spam word only appears in spam files
      else{
        float probSpamWord=0.999f;
        System.out.println("Word: "+ key+ " chance "+probSpamWord);
        spamProb.put(key,probSpamWord);
      }
    }

    Set<String> hamKeys = hamTrain.probabilities.keySet();
    Iterator<String> hamKeyIterator = hamKeys.iterator();
    //adds remaining probabilities for words only appearing in ham
    while(hamKeyIterator.hasNext()){
      String key=hamKeyIterator.next();
      if(!spamTrain.probabilities.containsKey(key)){
        float probSpamWord=0.001f;
        System.out.println("Word: "+ key+ " chance "+probSpamWord);
        spamProb.put(key,probSpamWord);
      }
    }

    //running through testing files
    SpamTester eachFile=new SpamTester();
    File testDir=new File("data/test/spam");
    File[] contents=testDir.listFiles();
    for (File current: contents){
      float fileSpamChance=0f;
      int count=0;
      eachFile.processFile(current);
      Set<String> fileWordKeys=eachFile.getFileWords().keySet();
      Iterator<String> fileWordIterator=fileWordKeys.iterator();
      while(fileWordIterator.hasNext()){
        String wordKey=fileWordIterator.next();
        if (spamProb.containsKey(wordKey)){
          fileSpamChance+=Math.log(1-spamProb.get(wordKey))-Math.log(spamProb.get(wordKey));
          count++;
        }
      }
      System.out.println(count+" + "+1/(1+Math.pow(Math.E,fileSpamChance)));
    }


  }
}
