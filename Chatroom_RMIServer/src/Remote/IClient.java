/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Client Remote Interface
 * 
 * @author jcvsa
 */
public interface IClient extends Remote {
    
    /**
     * posts the message sent by the sender at a time
     * 
     * @param time
     * @param sender
     * @param message
     * @throws RemoteException 
     */
    void postMessage (String time, String sender, String message) throws RemoteException; 
    
    /**
     * notifies that the user has connected to the app. 
     * 
     * @param user
     * @throws RemoteException 
     */
    void connectedNotif(String user) throws RemoteException; 
    
    /**
     * notifies that the user has disconnected from the app
     * 
     * @param user
     * @throws RemoteException 
     */
    void disconnectedNotif(String user) throws RemoteException; 
            
    
}
