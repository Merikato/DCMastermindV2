/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dcmastermind;

import dcmastermindclient.Client;
import dcmastermindclient.MMClient;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/**
 *
 * @author 1430626
 */
public class FXMLDocumentController implements Initializable {

    /*
    TODO:
        
       
        
        -Close app
        -Start new instance of game (for reset game handler)
     */
    //private MMClient client;
    private Client client;

    @FXML
    private Label label;

    @FXML
    private Button btnGuess;
    @FXML
    private MenuItem btnOpenHelp;
    @FXML
    private Button btnCloseHelp;
    @FXML
    private MenuItem btnNewGame;
    @FXML
    private MenuItem btnEndGame;
    @FXML
    private MenuItem btnCloseGame;

    @FXML
    private GridPane gameboard;

    @FXML
    private Circle btnRed;
    @FXML
    private Circle btnYellow;
    @FXML
    private Circle btnGreen;
    @FXML
    private Circle btnBlue;
    @FXML
    private Circle btnPurple;
    @FXML
    private Circle btnPink;
    @FXML
    private Circle btnWhite;
    
    @FXML
    private Circle btnLime;
    @FXML
    private Circle btnBrown;

    private boolean gameOver = false;
    private Circle selected;
    private int rowCount = 10;
    private int currentColor;
    private int[] guessArray = new int[4];
    private Circle[] row;
    private byte[] clues;
    private MMPacket mmp;
    private byte[] answer_set;
    private byte[] start_message;

    public FXMLDocumentController() {}

    
    @FXML
    private void handleColorClick(MouseEvent event) {

        selected.setStroke(Color.BLACK);
        selected.setStrokeWidth(1);
        currentColor = -1;

        selected = (Circle) event.getSource();
        selected.setStroke(Color.ALICEBLUE);
        selected.setStrokeWidth(2);
        currentColor = Integer.parseInt(selected.getId());

    }

    @FXML
    private void handlePositionClick(MouseEvent event) {
        Circle position = (Circle) event.getSource();
        position.setFill(Color.TRANSPARENT);

        position = (Circle) event.getSource();
        position.setFill(selected.getFill());
        //For some reason, if the column index is 0, the get returns null.
        if (GridPane.getColumnIndex(position) == null) {
            guessArray[0] = currentColor;
        } else {
            guessArray[GridPane.getColumnIndex(position)] = currentColor;
        }
    }

    @FXML
    private void handleGuessClick(ActionEvent event) throws IOException {
        System.out.println(Arrays.toString(guessArray));
        // Disable event listener for the current row of colour to avoid user
        // changing them.
        for(Circle c : row){
            c.setOnMousePressed(null);
        }

        client.play_turn(guessArray);
        clues = client.getClues();
        
        if(check_win(clues)){
            System.out.println("You won!");
            rowCount = 0;
            answer_set = mmp.readPacket();
            show_answer_set(answer_set);
            displayEndLabel(true);
            // TODO disable onAction for guess button, don't forget to reenable it
            // Smoke some dank kush
            return;
        }
        display_clues(clues);
        rowCount--;
        
        // Check if it's the last turn.
        System.out.println("turn: " + rowCount);
        if(rowCount == 0){
            System.out.println("last turn");
            byte b[] = mmp.readPacket();
            if(b[0] ==  0xFFFFFFFF){
                //END GAME
                System.out.println("last turn");
                answer_set = mmp.readPacket();
                System.out.println(Arrays.toString(answer_set));
                show_answer_set(answer_set);
                displayEndLabel(false);
            }
            
        }
        
        if(rowCount != 0)
            addCirclesToRow();
        
    }
    
    private boolean check_win(byte[] clues){
        boolean win = true;
        for(byte b : clues){
            if(b != 1)
                win = false;
        }
        return win;
    }

    @FXML
    private void handleHelpClick(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        if (event.getSource().equals(btnOpenHelp)) {
            stage = new Stage();
            Parent parent = FXMLLoader.load(getClass().getResource("AboutPage.fxml"));
            scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else if (event.getSource().equals(btnCloseHelp)) {
            stage = (Stage) btnCloseHelp.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleNewGameClick(ActionEvent event) {
        resetGame();
        try {
            client.startGame();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetGame() {
        ObservableList<Node> boardContent = gameboard.getChildren();

        for (Node content : boardContent) {
            if (content instanceof Circle) {
                Circle circle = (Circle) content;
                circle.setFill(Color.TRANSPARENT);
                circle.setStrokeWidth(1);
                circle.setVisible(false);
                circle.setDisable(false);
                if (GridPane.getRowIndex(content) == 10) {
                    circle.setVisible(true);
                }
            } else if (content instanceof HBox) {
                HBox hbox = (HBox) content;
                hbox.setVisible(false);
            }
            
        }

        gameboard.setDisable(false);
        btnGuess.setDisable(false);
        guessArray = new int[]{0,0,0,0};
        label.setVisible(false);
        rowCount = 10;
        gameOver = false;
    }
    /**
     * private helper method to show the answer set
     * @param answer_set 
     */
    private void show_answer_set(byte[] answer_set){
        row = new Circle[]{new Circle(13.5), new Circle(13.5), new Circle(13.5), new Circle(13.5)};
        int col = 0;
        for(int i = 0; i < answer_set.length; i++){
            row[i].setFill(int_to_color(answer_set[i]));
            row[i].setStrokeType(StrokeType.OUTSIDE);
            row[i].setStroke(Color.BLACK);
            row[i].setStrokeWidth(1);
            gameboard.add(row[i], col, 0);
            col++;               
        }
        
    }
    /**
     * private helper method to turn ints into their color code value 
     * for displaying the answer set's circles.
     * @param i
     * @return 
     */
    private Color int_to_color(int i){
        switch(i){
            case 2: return Color.RED;
            case 3: return Color.YELLOW;
            case 4: return Color.GREEN;
            case 5: return Color.BLUE;
            case 6: return Color.web("#551a8b");
            case 7: return Color.PINK;
            case 8: return Color.web("#00fc00");
            case 9: return Color.BROWN;
        }
        return Color.TRANSPARENT;
    }
    
    /**
     * Adds the circles to the current row
     */
    private void addCirclesToRow() {
        row = new Circle[]{new Circle(13.5), new Circle(13.5), new Circle(13.5), new Circle(13.5)};
        int col = 0;
        for (Circle c : row) {
            c.setOnMousePressed(this::handlePositionClick);
            c.setFill(Color.TRANSPARENT);
            c.setStrokeType(StrokeType.OUTSIDE);
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(1);
            gameboard.add(c, col, rowCount);
            col++;
        }
    }

    private void displayEndLabel(boolean result) {
        label.setVisible(true);

        if (result) {
            label.setText("You Win!");
            label.setTextFill(Color.GREEN);
        } else {
            label.setText("You Lose!");
            label.setTextFill(Color.RED);
        }
    }

    private void display_clues(byte[] clues) {
        if (clues == null) {
            clues = new byte[4];
        }
        HBox hints = new HBox();
        for (Node n : gameboard.getChildren()) {
            if (n instanceof HBox && rowCount == GridPane.getRowIndex(n)) {
                hints = (HBox) n;
                hints.setVisible(true);
                break;
            }
        }

        ObservableList<Node> childs = hints.getChildren();
        System.out.println(childs.size());
        boolean is_placed;
        for (int i = 0; i < clues.length; i++) {
            is_placed = false;
            if (clues[i] >= 0 && clues[i] < 3) {
                for (int j = 0; j < 4 && !is_placed; j++) {

                    Ellipse ellipse = (Ellipse) childs.get(j);

                    if (clues[i] == 1 && !ellipse.isVisible()) {
                        //one is white
                        ellipse.setFill(Color.BLACK);
                        is_placed = true;
                        ellipse.setVisible(true);
                    } else if (clues[i] == 0 && !ellipse.isVisible()) {
                        ellipse.setFill(Color.WHITE);
                        ellipse.setVisible(true);
                        is_placed = true;
                    }

                }
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        client = new MMClient();
//        try {
//            client.playGame();
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        client = new Client();
        try {
            client.createSocket();
            client.startGame();
            this.mmp = client.getMmPacket();
            System.out.println("Game Started");
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        selected = btnRed;
        currentColor = 2;
        addCirclesToRow();

    }
}
