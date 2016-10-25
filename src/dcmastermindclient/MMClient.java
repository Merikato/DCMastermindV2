/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dcmastermindclient;

import dcmastermind.DCMastermind;
import dcmastermind.MMPacket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author 1410926
 */
public class MMClient {
    //ask user to input IP address
   private String IPAddress="192.168.2.232";
   private int port = 50000;
   private  MMPacket mmPacket;
   private byte[] clues ;
   private byte[] serverAnswers;
   private Socket socket;
   private boolean playAgain;
   private byte[] guess;

    //ask user to start game or quit
    public static void main(String[] args) throws IOException
    {
        MMClient c = new MMClient();
        c.createSocket();
        c.continueGame();
    }
    public MMClient(){
    }
    public void continueGame() throws IOException{
        playAgain=playAgainGame();

        while (playAgain == true)   //while loop to keep playing until user enters quit
        {
            startGame(); //sends server the start msg
            //check server msg

            if(mmPacket.readPacket()[0] == 0x0000000A)
            {
                System.out.println("ready to draw board");
                displayGameBoard();
                //playGame();
            }

        playAgain = playAgainGame();
        }
    }
    private void startGame() throws IOException{
        System.out.println("send message to server");
        byte[] startMessage = new byte[]{0x00000011, 0,0,0};
        System.out.println("sending packet: " + Arrays.toString(startMessage));
        mmPacket.writePacket(startMessage);
    }
    public void createSocket() throws IOException{
        // Create socket that is connected to server on specified port
    socket = new Socket(IPAddress, port);
    System.out.println("Connected to server...sending echo string");

    mmPacket = new MMPacket(socket);
    System.out.println("CREATED PACKET");


    }
    public boolean playAgainGame() throws IOException{
        String userResponse = JOptionPane.showInputDialog("Enter P for play and Q for quit:");
        if(userResponse.equalsIgnoreCase("p"))
            playAgain = true;
        else {
             socket.close();
            System.exit(0);
             playAgain =  false;

        }
        return playAgain;
    }



    private void displayGameBoard() throws IOException{
    //diplay board
        Application.launch(DCMastermind.class);
    }


    //it is for one game
    public void playGame() throws IOException{

        byte[] userAnswers = guess;
        if(userAnswers.length == 4)
        {
            for(int i=0; i < 10;i++)
            {
               mmPacket.writePacket(userAnswers); //sends the user ans to server
               System.out.println("sent guess " + Arrays.toString(userAnswers));
               clues = mmPacket.readPacket();

               System.out.println("Clues-client: " + Arrays.toString(clues));
               if(i == 9){
                   System.out.println("end...");

                   byte[] b = mmPacket.readPacket();
                   System.out.println("end?" + b[0]);

                    if(b[0] == 0xFFFFFFFF){
                       endGame();
                        continueGame();

                    }
               }

               //display clues on board
               //if user won, display a msg, and call play again
               // else continuein the loop and get the user array
               //userAnswers = fillInUserAnswerArray();
            }

            serverAnswers = mmPacket.readPacket();
            //display a msg

        }

    }
    public byte[] fillInUserAnswerArray(){
        byte[] userAnswers = new byte[4];
        //gets the colors to insert here, get it from GUI
//        byte[] userAnswers = new byte[]{0x00000002,0x00000002,0x00000003,0x000000005};
        return userAnswers;
    }

    public void setGuess(int[] guess) {
        byte[] bytes = new byte[4];
        for(int i : guess){
            bytes[i] = convertIntToByte(i);
        }
        this.guess = bytes;
    }

    private byte convertIntToByte(int i){
        switch(i){
            case 2:
                return 0x00000002;
            case 3:
                return 0x00000003;
            case 4:
                return 0x00000004;
            case 5:
                return 0x00000005;
            case 6:
                return 0x00000006;
            case 7:
                return 0x00000007;
            case 8:
                return 0x00000008;
            case 9:
                return 0x00000009;
        }
        return 0x1;
    }



    private void endGame()throws IOException {
        System.out.println("End Game");
        byte[] answerSet = mmPacket.readPacket();
        System.out.println("Answer set: " + Arrays.toString(answerSet));
        System.out.println("");
    }
}
