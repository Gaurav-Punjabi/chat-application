/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Client;

import BackEnd.Wrapper.Message;
import UserInterface.ChatFrame;
import UserInterface.components.ContactComponent;
import UserInterface.components.MessageLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author gauravpunjabi
 */
public class EventHandling extends MouseAdapter implements ActionListener,ClientConstants{
    /**
     * *************************************************************************
     *                              CONSTRUCTOR
     * *************************************************************************
     * @param chatFrame 
     */
    public EventHandling(ChatFrame chatFrame) {
        setChatFrame(chatFrame);
        initialize();
    }
    private void initialize() {
        this.name = chatFrame.getName();
        this.messages = new ArrayList<>();
        clientSide = new ClientSide(this);
    }
    /***************************************************************************
     *                            CLASS METHODS
     * ************************************************************************* 
     */
    public void sendMessage(String message) {
        clientSide.write(MESSAGE_CODE + ":" + receiverName + ":" + message);
        this.addMessage(message,true);
    }
    public void showContacts(ArrayList<ContactComponent> contacts) {
        chatFrame.showContacts(contacts);
    }
    public void addMessage(String message,boolean isSender) {
        this.addMessage(new Message(message,isSender));
    }
    public void addMessage(Message message) {
        messages.add(new MessageLabel(message));
        chatFrame.showMessages(messages);
    }
    
    
    /**
     * *************************************************************************
     *                          MOUSE EVENT METHODS
     * *************************************************************************
     */
    public void mouseClicked(MouseEvent me) {
       if(me.getSource() instanceof ContactComponent) {
            contactComponentClicked(me);
        } 
    }
    private void contactComponentClicked(MouseEvent me) {
        String name = ((ContactComponent)me.getSource()).getNameLabel().getText();
        if(this.receiverName != name) {
            this.receiverName = name;
            chatFrame.setReceiver(this.receiverName);
            chatFrame.removeAllMessages();
        }
    }
    
    
    /**
     * *************************************************************************
     *                         ACTION EVENT METHOD
     * *************************************************************************
     */
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == chatFrame.getMessageTextField()) {
            sendMessage(chatFrame.getMessageTextField().getText());
        }
    }
    
    /**
     * *************************************************************************
     *                                  GETTERS
     * *************************************************************************
     */
    public ChatFrame getChatFrame() { return this.chatFrame; }
    public String getName() { return this.name; }
    
    
    /**
     * *************************************************************************
     *                                  SETTERS
     * *************************************************************************
     */
    public void setChatFrame(ChatFrame chatFrame) { this.chatFrame = chatFrame; }
    
    
    /**
     * *************************************************************************
     *                          VARIABLE DECLARATIONS
     * *************************************************************************
     */
    private ChatFrame chatFrame;
    private ClientSide clientSide;
    private String receiverName,name;
    private ArrayList<MessageLabel> messages;
}
