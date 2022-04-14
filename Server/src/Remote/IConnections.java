/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author jcvsa
 */
public interface IConnections extends Remote {
    
    /**
     * 
     * @param name
     * @return
     * @throws RemoteException 
     */
    boolean isConnected(String name) throws RemoteException; 
    
    /**
     * 
     * @param name
     * @throws RemoteException 
     */
    void disconnectUser(String name) throws RemoteException; 
    
    /**
     * 
     * @return
     * @throws RemoteException 
     */
    ArrayList<String> getUserNames() throws RemoteException;

    /**
     * 
     * @return
     * @throws RemoteException 
     */
    ArrayList<IMessage> getUserMessages() throws RemoteException;

    /**
     * 
     * @param messages
     * @throws RemoteException 
     */
    void setClientMessages(ArrayList<IMessage> messages) throws RemoteException;
    
}
