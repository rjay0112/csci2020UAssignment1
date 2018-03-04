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
    private TableView<TestFile> students;
    private TextField acc;
    private TextField prec;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Assignment1");

        DirectoryChooser directoryChooser=new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory=directoryChooser.showDialog(primaryStage);

        BorderPane layout=new BorderPane();

        TableColumn<TestFile, Integer> idCol = new TableColumn<>("File");
        idCol.setPrefWidth(400);
        idCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<TestFile, String> fCol = new TableColumn<>("Actual Class");
        fCol.setPrefWidth(100);
        fCol.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        TableColumn<TestFile, String> lCol = new TableColumn<>("Spam Probability");
        lCol.setPrefWidth(300);
        lCol.setCellValueFactory(new PropertyValueFactory<>("spamProbRounded"));

        students = new TableView<>();
        students.getColumns().add(idCol);
        students.getColumns().add(fCol);
        students.getColumns().add(lCol);

        SpamTester hamTrain=new SpamTester();
        SpamTester spamTrain=new SpamTester();
        hamTrain.processFile(new File(mainDirectory.getPath()+"/train/ham"));
        spamTrain.processFile(new File(mainDirectory.getPath()+"/train/spam"));
        ProbCreator allFileProb=new ProbCreator();
        allFileProb.calcProb(spamTrain,hamTrain);
        GridPane gp= new GridPane();
        gp.setPadding(new Insets(5,5,5,5));
        gp.setHgap(5);
        gp.setVgap(10);
        students.setItems(ProbCreator.getAllFileProb(new File(mainDirectory.getPath()+"/test")));

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

        layout.setCenter(students);
        layout.setBottom(gp);
        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}