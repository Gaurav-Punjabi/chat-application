/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author gauravpunjabi
 * This is the client side that is responsible for sending the messages to the 
 * server in the proper format.
 */
public class ClientSide {
    /***************************************************************************
     *                          CONSTRUCTOR
     * This constructor just basically initializes all the variables makes 
     * connection to the server and sends the clients name to the server.
     * Also it starts the thread for reading the responses from the server.
     * @param eventHandling : reference object of the eventHandling class.
     ***************************************************************************
     */
    public ClientSide(final EventHandling eventHandling) {
        try {
            this.name = eventHandling.getName();
            
            this.inetAddress = InetAddress.getLocalHost();
            
            //MAKING CONNECTION TO THE SERVER
            this.socket = new Socket(inetAddress,65534);
            
            this.printWriter = new PrintWriter(socket.getOutputStream(),true);
            //SENDING THE NAME
            sendName();
            
            //CREATING AND STARTING THE THREAD TO LISTEN TO RESPONSES FROM THE
            //SERVER.
            new ClientReadOperation(socket.getInputStream(),eventHandling).start();			
        }
        catch(Exception ioe) {
            System.out.println("Something went wrong with Client Side : " + ioe);
        }
    }
    
    /***************************************************************************
     *                          REQUESTING METHODS
     * This method is used to send the request to the sever.
     * @param message : The message that needs to be sent.
     */
    public void write(String message) {
        printWriter.println(message);
    }
    /***********************END_OF_REQUESTING_METHODS**************************/
    
    /***************************************************************************
     *                            SERVICE METHODS
     * This method just send the name of the client to the server for reference 
     * purpose in the proper format.
     * @throws IOException 
     */
    private void sendName()throws IOException {
        write("name:" + this.name);
    }
    /**************************END_OF_SERVICE_METHODS**************************/
    
    /**************************VARIABLE_DECLARATION****************************/
    private Socket socket;
    private int port;
    private InetAddress inetAddress;
    private PrintWriter printWriter;
    private String name;
    /**********************END_OF_VARIABLE_DECLARATION*************************/
}
