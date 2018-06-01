/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd;

import BackEnd.Wrapper.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author gauravpunjabi
 * This class is used to handle insert and delete operations of JSON to store 
 * the messages of user for backup.
 * This is simply done with the help of JSONObject in JSON.simple 3rd party API.
 * We store the messages of each user in a individual file in the form JSON.
 * This is done so that whenever the user opens the application the old chats can be accessed.
 */
public class JSONMessageHandler implements JSONMessageHandlerConstants {

    /***************************************************************************
     *                          VARIABLE DECLARATION
     ***************************************************************************
     */
    private JSONObject jsonObject;
    private HashMap<String, File> backups;

    /***************************************************************************
     *                              CONSTRUCTOR
     * @param names : a set of string with all the users connected to the server.
     *
     * This constructor accepts a set of active users and initializes their file
     * using initDataFiles function.
     ***************************************************************************
     */
    public JSONMessageHandler(final Set<String> names) {
        this.jsonObject = new JSONObject();
        this.backups = new HashMap<>();
        initDataFiles(names);
    }
    

    /***************************************************************************
     *                            INSERT METHOD
     * @param name : name of the user whose message needs to be saved
     * @param message : Message that needs to be saved
     * @param status : the status of the message(.i.e whether it is sent,read
     *                 ,etc...)
     * @param isSender : boolean variable indicating whether the user is sender
     *                   or receiver of this message that needs to be saved.
     * @return : returns (boolean) whether the message is successfully added or
     *           not.
     *           true - message has been saved successfully.
     *           false - message could not be saved due to some issue.
     * 
     * This method is simply used to add a message to the specified users data
     * file.
     */
    public boolean addMessage(final String name,
            final String message,
            final int status,
            final boolean isSender) {
        return this.addMessage(name, new Message(message, status, isSender));
    }
    
    /**
     * @param name : name of the user whose message needs to be saved
     * @param message : object of the message that needs to be saved.
     * @return : returns (boolean) whether the message is successfully added or
     *           not.
     *           true - message has been saved successfully.
     *           false - message could not be saved due to some issue.
     *
     * This method is simply used to add a message to the specified users data
     * file.
     ***************************************************************************
     */
    public boolean addMessage(final String name,
                              final Message message) {
        File file = backups.get(name);
        if (file == null) {
            return false;
        }
        JSONArray jsonArray;
        try {
            jsonObject = ((JSONObject)new JSONParser().parse(file.getPath()));
        } catch(ParseException pe) {
            System.out.println("addMessage : pe : " + pe.getMessage());
        } catch(Exception e){ 
            System.out.println("addMessage : e1 : " + e.getMessage());
        }
        try (FileOutputStream fos = new FileOutputStream(backups.get(name))) {
            if (jsonObject.get("messages") == null) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = (JSONArray) jsonObject.get("messages");
            }
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("message", message.getMessage());
            jsonMessage.put("status", message.getStatus());
            jsonMessage.put("isSender", message.isSender());
            jsonArray.add(jsonMessage);
            jsonObject.put("messages", jsonArray);
            String jsonString = jsonObject.toJSONString();
            fos.write(jsonString.getBytes());
        } catch (IOException ioe) {
            System.out.println("addMessage : ioe : " + ioe.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("addMessage : e : " + e.getMessage());
            return false;
        }
        return true;
    }

    /***************************************************************************
     *                           UPDATE METHOD
     * @param names : the set of names of active users
     * @return : returns (boolean) whether users have been updated successfully 
     *           or not
     *          true - users have been updated properly.
     *          false - there was some issue while updating users.
     * 
     * This method is used to update the list of users.As if a condition arrives 
     * where a new user becomes active or a new user is created and does not has 
     * a abstract file.Hence this method creates the abstract files if not 
     * present. 
     ***************************************************************************
     */
    public boolean updateUsers(final Set<String> names) {
        if (names == null) {
            return false;
        }
        for (String name : names) {
            File file = this.backups.get(name);
            if (file == null) {
                file = new File(BACKUP_LOCATION + name.hashCode() + ".json");
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    System.out.println("updateUsers : ioe : " + ioe.getMessage());
                } catch (Exception e) {
                    System.out.println("updateUsers : e :" + e.getMessage());
                }
                this.backups.put(name, file);
            }
        }
        return true;
    }
    
    /***************************************************************************
     *                             DATA ACCESS
     * @param name : name of the user whose messages needs to be accessed.
     * @return : a list of messages of the user whose name was provided.
     * 
     * This method is used to access the stored messages of a user by providing
     * the name of the user.It provides us with a list of message objects.
     ***************************************************************************
     */
    public List<Message> getMessages(final String name) {
        File file = this.backups.get(name);
        if(file == null) 
            return null;
        try {
            Object object = new JSONParser().parse(file.getPath());
            if(object == null)
                return null;
            JSONArray array = (JSONArray)((JSONObject)object).get("messages");
            if(array == null)
                return null;
            List<Message> messages = jsonToList(array);
            return messages;
        } catch(ParseException pe) {
            System.out.println("getMEssages : pe : " + pe.getMessage());
            return null;
        } catch(Exception e) {
            System.out.println("getMessages : e : " + e.getMessage());
            return null;
        }
    }
    
    /***************************************************************************
     *                        PRIVATE SERVICE METHODS
     * @param array : a JSON array which needs to be converted into list of 
     *                messages.
     * @return : returns a list of messages parsed from the given JSONArray
     * 
     * This is just a service method accessed by other methods to convert a 
     * JSONArray into List of Message Objects.
     */
    private List<Message> jsonToList(final JSONArray array) {
        List<Message> messages = new ArrayList<Message>();
        for(Object object : array) {
            Message message = jsonToMessage((JSONObject)object);
            if(message != null)
                messages.add(message);
        }
        return messages;
    }
    
    /*
     * @param names : set of names of active users
     * 
     * This method accepts the set of names and creates abstract files with their
     * hashCode if not already present.
     * We used the hashCode so the file could not be understood by the end-user
     * also so that it could be uniquely identified.
     */
    private void initDataFiles(final Set<String> names) {
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            String location = BACKUP_LOCATION + name.hashCode() + ".json";
            if (!new File(location).exists()) {
                try {
                    new File(location).createNewFile();
                } catch (IOException ioe) {
                    System.out.println("initDataFiles:JSONMessageHandler IO : " + ioe.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            backups.put(name, new File(location));
        }
    }
    
    /** 
     * @param target : jsonObject that needs to be converted into message object
     * @return : object of message parsed from the given JSONObject.
     * 
     * This is a service method used by the other methods to convert JSONObject 
     * into a Message.
     ***************************************************************************
     */
    private Message jsonToMessage(final JSONObject target) {
        String message = (String)target.get("message");
        int status = (int)target.get("status");
        boolean isSender = (boolean)target.get("isSender");
        return new Message(message, status, isSender);
    }
}
