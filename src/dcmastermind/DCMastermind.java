/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dcmastermind;

import dcmastermindclient.Client;
import dcmastermindclient.MMClient;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author 1430626
 */
public class DCMastermind extends Application {
    
    private Stage stage;
    private Client client;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Scene game = createGame();
        
        stage.setTitle("MASTERMIND");
        stage.setScene(game);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
    
    private Scene createGame() throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("FXMLDocument.fxml"));
        Parent root = (AnchorPane)loader.load();
        FXMLDocumentController controller = loader.getController();
//        client = new Client();
//        controller.setClient(client);
        Scene scene = new Scene(root);
        return scene;
        
    }
    
    
}
