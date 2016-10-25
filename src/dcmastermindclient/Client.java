/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dcmastermindclient;

import dcmastermind.MMPacket;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 *
 * @author 1432581
 */
public class Client {
    private String server_ip;
    private int port;
    private MMPacket mmPacket;
    private byte[] clues;
    
    public Client(){
        this.server_ip = "localhost";
        this.port = 50000;
    }
    
    public Client(String ip){
        this.server_ip = ip;
        this.port = 50000;
    }
    
    public Client(String ip, int port){
        this.server_ip = ip;
        this.port = port;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String Server_ip) {
        this.server_ip = Server_ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public MMPacket getMmPacket() {
        return mmPacket;
    }

    public void setMmPacket(MMPacket mmPacket) {
        this.mmPacket = mmPacket;
    }

    public byte[] getClues() {
        return clues;
    }

    public void setClues(byte[] clues) {
        this.clues = clues;
    }
    /**
     * plays a turn from the gui
     * recevies the clues from the server after sending the guess.
     * @param guess 
     */
    public void play_turn(int[] guess) throws IOException{
        byte[] packet = new byte[guess.length];
        for(int i = 0; i < packet.length; i++){
            packet[i] = convertIntToByte(guess[i]);
        }
        // send out the guess.
        System.out.println("sent");
        mmPacket.writePacket(packet);
        //read the clues received back
        clues = mmPacket.readPacket();
        System.out.println("received clues...\n" + "Clues: " + Arrays.toString(clues));
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
    /**
     * Creates a socket and implements the MMPacket.
     * @throws IOException 
     */
    public void createSocket() throws IOException{
        Socket soc = new Socket(server_ip, port);
        mmPacket = new MMPacket(soc);
    }
    public void startGame() throws IOException{
        System.out.println("sending start message to server...");
        byte[] startMessage = new byte[]{0x00000011, 0,0,0};
        System.out.println("sending packet: " + Arrays.toString(startMessage));
        mmPacket.writePacket(startMessage);
        System.out.println("packet sent.");
        if(mmPacket.readPacket()[0] == 0x0000000A){
            System.out.println("aOK");
        }
    }
    
  
}
