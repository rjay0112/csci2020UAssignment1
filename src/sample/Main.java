//Ryan Jay
package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.util.*;
import java.io.*;

public class Main extends Application {
    private TableView<TestFile> fileSpam;
    private TextField acc;
    private TextField prec;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Assignment1");
        //directory prompt
        DirectoryChooser directoryChooser=new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory=directoryChooser.showDialog(primaryStage);

        BorderPane layout=new BorderPane();

        TableColumn<TestFile, Integer> fileCol = new TableColumn<>("File");
        fileCol.setPrefWidth(400);
        fileCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<TestFile, String> classCol = new TableColumn<>("Actual Class");
        classCol.setPrefWidth(100);
        classCol.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        TableColumn<TestFile, String> spamCol = new TableColumn<>("Spam Probability");
        spamCol.setPrefWidth(300);
        spamCol.setCellValueFactory(new PropertyValueFactory<>("spamProbRounded"));

        fileSpam = new TableView<>();
        fileSpam.getColumns().add(fileCol);
        fileSpam.getColumns().add(classCol);
        fileSpam.getColumns().add(spamCol);


        SpamTester hamTrain=new SpamTester();
        SpamTester spamTrain=new SpamTester();
        //processes words in both training spam and ham folders
        hamTrain.processFile(new File(mainDirectory.getPath()+"/train/ham"));
        spamTrain.processFile(new File(mainDirectory.getPath()+"/train/spam"));
        ProbCreator allFileProb=new ProbCreator();
        //calculates word probabilities of all words found in training
        allFileProb.calcProb(spamTrain,hamTrain);
        GridPane gp= new GridPane();
        gp.setPadding(new Insets(5,5,5,5));
        gp.setHgap(5);
        gp.setVgap(10);
        //adds all files in test to the list to be shown in the columns
        fileSpam.setItems(ProbCreator.getAllFileProb(new File(mainDirectory.getPath()+"/test")));

        Label accLabel=new Label("Accuracy: ");
        acc=new TextField();
        acc.setText(Double.toString(ProbCreator.getAccuracy()));
        gp.add(accLabel,0,0);
        gp.add(acc,1,0);

        Label precLabel=new Label("Precision: ");
        prec=new TextField();
        prec.setText(Double.toString(ProbCreator.getPrecision()));
        gp.add(precLabel,0,1);
        gp.add(prec,1,1);

        layout.setCenter(fileSpam);
        layout.setBottom(gp);
        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}