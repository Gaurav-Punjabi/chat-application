/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd.Wrapper;

/**
 *
 * @author gauravpunjabi
 */
public class Message implements MessageConstants {

    private String message;
    private int status;
    private boolean isSender;

    public Message(final String message, 
                   final int status, 
                   final boolean isSender) {
        this.message = message;
        this.status = status;
        this.isSender = isSender;
    }

    public Message(final String message, 
                   final int status) {
        this(message, status, false);
    }

    public Message(final String message, 
                   final boolean isSender) {
        this(message, MESSAGE_STATUS_SENT, isSender);
    }

    public Message(final String message) {
        this(message, MESSAGE_STATUS_SENT);
    }

    public Message() {
        this("");
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setIsSender(final boolean isSender) {
        this.isSender = isSender;
    }

}
