/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Client;

import UserInterface.components.ContactComponent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * @author gauravpunjabi
 * This is the thread that handles all the responses from the server.
 * It continously listens to the stream shared by the client and the server.
 * And as soon as the response is received it processes it.
 */
public class ClientReadOperation extends Thread implements ClientConstants {
    /***************************************************************************
     *                             CONSTRUCTOR
     * This constructor just initializes all the variables.
     * @param inputStream   : the input stream that is shared between the client
     *                        and the server.
     * @param eventHandling : the reference object of the eventHandling.
     ***************************************************************************
     */
    public ClientReadOperation(final InputStream inputStream,
                               final EventHandling eventHandling) {
        this.eventHandling = eventHandling;
        scanner = new Scanner(inputStream);
    }	
    
    /***************************************************************************
     * JUST OVERRIDING THE METHOD OF RUNNABLE INTERFACE.
     */
    @Override
    public void run() {
        readResponses();
    }
    
    /***************************************************************************
     *                          SERVICE METHODS
     * This method just reads all the responses and passes it down for 
     * further decoding
     */
    private void readResponses() {
        while(true) {
            //JUST READING THE RESPONSE.
            String message = scanner.nextLine();
            decodeResponse(message);
        }
    }
    
    /**
     * This method is used to decode the response received from the server and
     * selects the appropriate operation that needs to be performed.
     * The response is divided into 2 parts : 
     *      1 - The type of response that determines which operation needs to 
     *          be performed.
     *      2 - The actual response from the server(containing data).
     * The response is divided into 2 parts with the help of ':'
     * @param response : the response from the server.
     */
    private void decodeResponse(final String response) {
        //CREATING THE PATTERN FOR DECODING THE RESPONSE
        Matcher matcher = Pattern.compile("([\\w]*):([\\w:\\d\\s, ]*)").matcher(response);
        if(matcher.matches()) {
            String operationType = matcher.group(1);
            String message = matcher.group(2);
            
            switch(operationType) 
            {
                case ERROR_CODE :
                    errorOperation();
                break;

                case MESSAGE_CODE :
                    messageOperation(message);
                break;

                case CLIENT_LIST :
                    clientListOperation(message);
                break;

            }
        }
    }
    
    /**
     * This method is used to perform operation when a error response is 
     * encountered.
     */
    private void errorOperation() {
        JOptionPane.showMessageDialog(null, "SOME ERROR HAS BEEN FOUND ON CLIENT SIDE");
    }
    
    /**
     * This method is used to perform operation when a message response is encountered.
     * PATTERN FOR FURTHER DECODING THE MESSAGE
     * AGAIN THE MESSAGE IS DIVIDED INTO 2 PARTS 
     *     1 - The name of the user who sent the message
     *     2 - The actual message sent from the user
     * The message is divide with the help of ':'.
     * @param message 
     */
    private void messageOperation(final String message) {
        Matcher subMatcher = Pattern.compile("(\\w*):([\\w\\d\\s \\.\\?]*)").matcher(message);
        if(subMatcher.matches()){
            eventHandling.addMessage(subMatcher.group(2), false);
        }
    }
    
    /**
     * This method is called when a clienList is received from the server.
     * This response contains a list of clients that are currently active.
     * The names of the clients are separated with the help of ','.
     * @param message 
     */
    private void clientListOperation(final String message) {
        Matcher clientMatcher = Pattern.compile("(\\w*),").matcher(message);
        this.contacts = new ArrayList<>();
        while(clientMatcher.find()) {
            if(!clientMatcher.group(1).equals(eventHandling.getName()))
                contacts.add(new ContactComponent(clientMatcher.group(1)));
        }
        eventHandling.showContacts(contacts);
    }
    /*************************END_OF_SERVICE_METHODS***************************/
    
    /*************************VARIABLE DECLARTAION*****************************/
    private EventHandling eventHandling;
    private Scanner scanner;
    private ArrayList<ContactComponent> contacts;
    /*********************END_OF_VARIABLE_DECLARATION**************************/
}
