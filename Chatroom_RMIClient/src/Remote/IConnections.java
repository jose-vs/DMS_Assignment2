/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Connections Remote Interface 
 * 
 * @author jcvsa
 */
public interface IConnections extends Remote {

    /**
     * Connect the user to the RMI Registry and return true if successful
     * 
     * @param name
     * @param stub
     * @return
     * @throws RemoteException
     */
    boolean connectUser(String name, IClient stub) throws RemoteException;

    /**
     * removes the client from the list of connected users
     * 
     * @param name
     * @throws RemoteException
     */
    void disconnectUser(String name) throws RemoteException;

    /**
     *  get a list of all users
     * 
     * @return @throws RemoteException
     */
    ArrayList<String> getUserNames() throws RemoteException;

    /**
     * add the messsage from the sender to the server message history. 
     * 
     * @param sender
     * @param message
     * @return
     * @throws RemoteException 
     */
    String addMessage(String sender, String message) throws RemoteException;

    /**
     *  return a list of all the user message
     * 
     * @return @throws RemoteException
     */
    ArrayList<Message> getUserMessages() throws RemoteException;

    /**
     * sends a notification to the server and client whenever it succesfully
     * connects to the rmi registry 
     * 
     * @return 
     * @throws java.rmi.RemoteException
     */
    public String appLaunchNotification() throws RemoteException;

}
