/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Remote;

import java.io.Serializable;

/**
 *
 * @author jcvsa
 */
public class IMessage implements Serializable {
    
    private static final long serialVersionUID = 687991492884005033L;
    
    private final String timeSent; 
    private final String sender;
    private final String message; 
    
    private IMessage(String timeSent, String sender, String message){ 
        this.timeSent = timeSent; 
        this.sender = sender; 
        this.message = message; 
    }

    /**
     * @return the timeSent
     */
    public String getTimeSent() {
        return timeSent;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
}
