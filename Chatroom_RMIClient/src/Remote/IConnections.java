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
     * @param stub
     * @return
     * @throws RemoteException
     */
    boolean isConnected(String name, IClient stub) throws RemoteException;

    /**
     *
     * @param name
     * @throws RemoteException
     */
    void disconnectUser(String name) throws RemoteException;

    /**
     *
     * @return @throws RemoteException
     */
    ArrayList<String> getUserNames() throws RemoteException;

    /**
     * 
     * @param sender
     * @param message
     * @return
     * @throws RemoteException 
     */
    String addMessage(String sender, String message) throws RemoteException;

    /**
     *
     * @return @throws RemoteException
     */
    ArrayList<Message> getUserMessages() throws RemoteException;

    /**
     *
     * @param messages
     * @throws RemoteException
     */
    void setClientMessages(ArrayList<Message> messages) throws RemoteException;

    /**
     * 
     * @return 
     * @throws java.rmi.RemoteException
     */
    public String appLaunchNotification() throws RemoteException;

}
