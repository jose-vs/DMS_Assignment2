/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author jcvsa
 */
public interface IClient extends Remote {
    
    /**
     * 
     * @param time
     * @param sender
     * @param message
     * @throws RemoteException 
     */
    void postMessage (String time, String sender, String message) throws RemoteException; 
    
    /**
     * 
     * @param user
     * @throws RemoteException 
     */
    void connectedNotif(String user) throws RemoteException; 
    
    /**
     * 
     * @param user
     * @throws RemoteException 
     */
    void disconnectedNotif(String user) throws RemoteException; 
               
}
