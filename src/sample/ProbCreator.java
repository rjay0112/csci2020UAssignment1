package sample;
import java.util.*;
import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProbCreator {
    static private Map<String,Double> wordProb;
    static private int numFiles;
    static private int trueNeg;
    static private int truePos;
    static private int falseNeg;
    static private int falsePos;
    static private double accuracy;
    static private double precision;

    public static double getAccuracy(){return accuracy;}
    public static double getPrecision(){return precision;}


    public static ObservableList<TestFile> getAllFileProb(File file) throws IOException{
        File hamFileDir=new File(file.getPath()+"/ham");
        File spamFileDir=new File(file.getPath()+"/spam");
        File[] contents=hamFileDir.listFiles();
        ObservableList<TestFile> allFiles = FXCollections.observableArrayList();
        for (File current: contents){
            numFiles++;
            double fileSpamChance=0;
            SpamTester eachFile=new SpamTester();
            eachFile.processFile(current);
            Set<String> fileWordKeys=eachFile.getFileWords().keySet();
            Iterator<String>fileWordIter=fileWordKeys.iterator();
            while(fileWordIter.hasNext()){
                String wordKey=fileWordIter.next();
                if(wordProb.containsKey(wordKey)){
                    fileSpamChance+=Math.log(1-wordProb.get(wordKey))-Math.log(wordProb.get(wordKey));
                }
            }
            fileSpamChance=1/(1+Math.pow(Math.E,fileSpamChance));
            if(fileSpamChance<0.5){
                truePos++;
            }else{
                falseNeg++;
            }
            allFiles.add(new TestFile(current.getName(),fileSpamChance,"ham"));
        }
        File[] content=spamFileDir.listFiles();
        for (File current: content){
            numFiles++;
            double fileSpamChance=0;
            SpamTester eachFile=new SpamTester();
            eachFile.processFile(current);
            Set<String> fileWordKeys=eachFile.getFileWords().keySet();
            Iterator<String>fileWordIter=fileWordKeys.iterator();
            while(fileWordIter.hasNext()){
                String wordKey=fileWordIter.next();
                if(wordProb.containsKey(wordKey)){
                    fileSpamChance+=Math.log(1-wordProb.get(wordKey))-Math.log(wordProb.get(wordKey));
                }
            }
            fileSpamChance=1/(1+Math.pow(Math.E,fileSpamChance));
            if(fileSpamChance>0.5){
                trueNeg++;
            }else{
                falsePos++;
            }
            allFiles.add(new TestFile(current.getName(),fileSpamChance,"spam"));
        }
        accuracy=(truePos+trueNeg)/(float)numFiles;
        precision=truePos/(float)(falsePos+truePos);
        return allFiles;
    }

    public void calcProb(SpamTester spam, SpamTester ham){
        wordProb=new TreeMap<>();
        Set<String> spamKeys=spam.getProb().keySet();
        Iterator<String> spamKeyIter=spamKeys.iterator();
        while(spamKeyIter.hasNext()){
            //System.out.println("WORK");
            String key=spamKeyIter.next();
            double occur=spam.getProb().get(key);
            if(ham.getProb().containsKey(key)){
                double probAppearSpam=(occur/spam.getFileCount());
                double probAppearHam=(ham.getProb().get(key)/ham.getFileCount());
                double probSpamWord=probAppearSpam/(probAppearHam+probAppearSpam);
                wordProb.put(key,probSpamWord);
            }
            else{
                wordProb.put(key,0.999999);
            }

        }

        Set<String> hamKeys=ham.getProb().keySet();
        Iterator<String> hamKeyIter=hamKeys.iterator();
        while(hamKeyIter.hasNext()){
            String key=hamKeyIter.next();
            if(!spam.getProb().containsKey(key)){
                wordProb.put(key,0.000001);
            }
        }
    }
}
