/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Server;

import BackEnd.JSONMessageHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gauravpunjabi
 * This is the class which handles all the Server Side Operations with the help 
 * of 2 or more classes.
 * This is basically the side that accepts the user sends Meta-data to the 
 * client such as the active users or broadcasts some messages.
 * Most importantly it is used to connect all the server modules and accept the
 * Client connection.
 */
public class ServerSide {
    
    /***************************************************************************
     *                          VARIABLE DECLARATION
     */
    private InetAddress inetAddress;
    private ServerSocket serverSocket;
    private HashMap <String,PrintWriter> clients;
    private JSONMessageHandler jsonMessageHandler;
    /***************************************************************************/
    
    /***************************************************************************
     *                              CONSTRUCTOR
     * This constructor just initializes the basic variables and enters an 
     * infinite loop to accept the client connection.
     ***************************************************************************
     */
    public ServerSide() {
        try {
            initialize();
            acceptClients();
        }
        catch(IOException ioe) {
            System.out.println("Server Exception " + ioe.getMessage());
        }
    }    
    
    /***************************************************************************
     *                            SERVICE METHODS
     * @throws IOException 
     * 
     * This is the method that enters an infinite loop and accepts the client 
     * for connection and also starts a new Thread that continously request to
     * the request of the clients.
     * It also updates the other users whenever a new user has joined the 
     * session.
     */
    private void acceptClients() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client has been connected waiting for name.");
            String message = new Scanner(socket.getInputStream()).nextLine();
            System.out.println("Got a message : " + message);
            Matcher m = Pattern.compile("name:(\\w*)").matcher(message);
            if(m.matches()) {
                clients.put(m.group(1),new PrintWriter(socket.getOutputStream(),true));
                synchronized(jsonMessageHandler) {
                    jsonMessageHandler.updateUsers(clients.keySet());
                }
            }
            else
                System.out.println("Not Matches");       
            System.out.println("Client " + m.group(1) + " joined");
            new ServerReadOperation(socket,m.group(1),this).start();
            broadcast(this.hashMapToString());
        }
    }
    
    /**
     * @param message : the message in the form of string that needs to be 
     *                  broadcast.
     * 
     * This method is used to broadcast the given message to all the active 
     * clients that are connected to the server.
     */
    private void broadcast(String message) {
        for(Map.Entry<String,PrintWriter> client : clients.entrySet()) {
            client.getValue().println(message);
        }
    }
    
    
    /**
     * This is another service method that is used to initialize the basic 
     * variables in the class and the ServerSocket.
     * @throws IOException : due to the initialization of ServerSocket.
     */
    private void initialize()throws IOException {
        this.inetAddress = InetAddress.getLocalHost();
        this.serverSocket = new ServerSocket(65534);
        this.clients = new HashMap<>();
        this.jsonMessageHandler = new JSONMessageHandler(this.clients.keySet());
    }
    
    /**
     * This is a service method used by the other methods to convert the given 
     * hashMap into string so that it can be easily transferred over the network.
     * NOTE : It is transferred in the form of XML.
     * 
     * @return : returns the hashMap in the form of string.
     *************************END OF SERVICE METHODS****************************
     */
    private String hashMapToString() {
        String clientList = "Clients:";
        for (Map.Entry<String,PrintWriter> client : clients.entrySet()) {
            clientList += client.getKey() + ",";
        }
        return clientList;
    }
    
    /***************************************************************************
     *                                 GETTERS
     * Just some of the public methods to access some of the data members of 
     * this class. 
     */
    public JSONMessageHandler getJSONMessageHandler() { return this.jsonMessageHandler;  }
    public PrintWriter getClient(final String clientName) { return this.clients.get(clientName); }
    /*******************************END OF GETTERS*****************************/
    
    public static void main(String[] args) {
        ServerSide serverSide = new ServerSide();
    }
}
