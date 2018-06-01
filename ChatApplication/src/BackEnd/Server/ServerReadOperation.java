/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Server;

import BackEnd.JSONMessageHandler;
import BackEnd.Wrapper.Message;
import BackEnd.Wrapper.MessageConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gauravpunjabi
 * This is the thread that is created by the server for each and every client
 * whenever a client connects to the server.
 * So basically this thread reads all the messages by the client,decodes them
 * and then fulfills them accordingly.This is the thread that handles passing
 * the message from client a to b.
 * So whenever a client sends a message, it sends to the server with the name of
 * the user to whom it is intended.
 * Then the server accesses the outputStream of the intended receiver and writes
 * the message with the senders name on it.
 */
public class ServerReadOperation extends Thread implements ServerConstants {
    
    /**************************VARIABLE DECLARATION****************************/
    private Socket socket;
    private Scanner scanner;
    private ServerSide ref;	
    private String name;
    private PrintWriter receiver = null;  
    /*********************END OF VARIABLE DECLARATION**************************/
    
    /***************************************************************************
     *                             CONSTRUCTOR
     * This constructor accepts the socket object of the client to which it must
     * service to,name of the client for referencing its socket and a reference 
     * object of the ServerSide so it can communicate with the Main Server.
     * It basically just initializes all the variables and stores the references.
     * @param socket : the object of the socket of the client to whom it must
     *                 provide service.
     * @param name   : the name of the client.
     * @param ref    : the reference object of the Main Server.
     */
    public ServerReadOperation(final Socket socket,
                               final String name,
                               final ServerSide ref) {
        this.socket = socket;
        this.ref = ref;
        this.name = name;
        try
        {
            scanner = new Scanner(socket.getInputStream());
        }
        catch(IOException ioe){
                System.out.println("Server InputStream " + ioe);
        }
    }
    /**************************END_OF_CONSTRUCTOR******************************/
    
    
    /**
     * Just the overriding the method of Runnable.
     * It basically calls readRequests to perform further processing.
     */
    @Override
    public void run() {
       readRequests(); 
    }
    
    /***************************************************************************
     *                            SERVICE METHODS
     * This is just the main method that reads the message from the client and
     * passes it down to further process it.
     */
    private void readRequests() {
        while(true) {
            //SIMPLY READING ALL THE REQUESTS AND PASSING FORWARD TO PROCESS THEM.
            String message = scanner.nextLine();
            processRequests(message);
        }
    }
    
    /**
     * This method takes the message and decodes the message into request.
     * As each request is divide into 2 parts 
     *      1 - The Code for which type of request is it - 
     *          For example : 
     *              (i) MESSAGE - REQUEST
     *      2 - This part is the actual request.
     * These 2 parts are separated with the help of ':'
     * @param message : Request from the client.
     */
    private void processRequests(final String message) {
        //PATTERN FOR DECODING THE REQUEST FROM THE CLIENT 
        Matcher m = Pattern.compile("(\\w*):([\\w \\s\\d:\\.\\?]*)").matcher(message);
        
        if(m.matches()) {
            switch(m.group(1)) {
                case MESSAGE_CODE:
                    messageRequested(m.group(2));
                break;
            }
        }
    }
    
    /**
     * This is a service method for processing a message
     * The message request is divided into 2 parts.
     * They are : 
     *      1) The name of the client to whom the message must be forwarded.
     *      2) The actual message that needs to be forwarded.
     * These 2 parts are separated with the help of ':'.
     * @param messageRequest : message request.
     */
    private void messageRequested(final String messageRequest) {
        //CREATING A PATTERN FOR DECODING THE MESSAGE REQUEST OF CLIENT.
        Matcher clientMatcher = Pattern.compile("(\\w*):([\\w \\s\\d:\\.\\?]*)").matcher(messageRequest);
        if(clientMatcher.find()) {
            String message = clientMatcher.group(2);
            String receiversName = clientMatcher.group(1);
            
            //Accessing the printwriter of client so message can be forwarded.
            receiver = ref.getClient(clientMatcher.group(1));
            
            //Forwarding the message to the client.
            receiver.println(MESSAGE_CODE + ":" + name  + ":" + clientMatcher.group(2));
            
            //SAVING THE MESSAGE IN JSON FORMAT....
            saveMessage(message,receiversName);
        }
    }
    
    /**
     * This method is used to save the given message for both the sender and the 
     * receiver.It simply uses the reference of JSON from the main server to 
     * save the message.
     * @param message : The message that needs to be saved.
     * @param name : name of the receiver of this message.
     */
    private void saveMessage(final String message,
                             final String name) {
        //SIMPLY CREATING THE MESSAGE OBJECTS OF SENDER AND RECEIVER.
        Message sendersMessage = new Message(message, MessageConstants.MESSAGE_STATUS_SENT, true);
        Message receiversMessage = new Message(message);
        
        //ACCESSING THE MESSAGE HANDLER OBJECT FROM THE SERVER.
        JSONMessageHandler jsonMessageHandler = ref.getJSONMessageHandler();
        
        //USING THE SYNCHRONIZED BLOCK TO MAKE THE SAVE MESSAGE THREAD SAVE.
        synchronized(jsonMessageHandler) {
            //SIMPLY CALLING THE ADD FUNCTION.
            jsonMessageHandler.addMessage(this.name, sendersMessage);
            jsonMessageHandler.addMessage(message,receiversMessage);
        }
    }
    /**************************END_OF_SERVICE_METHODS**************************/
}
