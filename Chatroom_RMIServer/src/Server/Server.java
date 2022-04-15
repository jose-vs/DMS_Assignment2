package Server;

import Remote.IClient;
import Remote.IConnections;
import Remote.Message;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
public class Server implements IConnections {

    private final ArrayList<String> userNames;
    private ArrayList<Message> userMessages;
    
    private static Registry registry;

    public Server() {
        this.userNames = new ArrayList<>();
        this.userMessages = new ArrayList<>();
    }

    @Override
    public boolean isConnected(String name, IClient stub) throws RemoteException {
        if (userNames.contains(name)) {
            return false;
        }

        System.out.println("User connection: " + name);
        
        registry.rebind("/client/" + name, stub);
        userNames.add(name);
        return true;
    }

    @Override
    public void disconnectUser(String name) throws RemoteException {
        System.out.println("User left: " + name);
        userNames.remove(name);
    }

    @Override
    public ArrayList<String> getUserNames() throws RemoteException {
        return this.userNames;
    }

    @Override
    public String addMessage(String sender, String message) throws RemoteException {
        String DATE_FORMAT = "HH:mm:ss";
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        this.userMessages.add(new Message(time, sender, message));

        return time;
    }

    @Override
    public ArrayList<Message> getUserMessages() throws RemoteException {
        return this.userMessages;
    }

    @Override
    public void setClientMessages(ArrayList<Message> messages) throws RemoteException {
        this.userMessages = messages;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Server remoteObject = new Server();

        try {
            try {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            } catch (RemoteException e) {
                System.out.println("Can't create a new registry");
            }

            IConnections stub = (IConnections) UnicastRemoteObject.exportObject(remoteObject, 0);
            registry = LocateRegistry.getRegistry();
            registry.rebind("/server", stub);

            System.out.println("Names bound in RMI registry");

        } catch (RemoteException e) {
            System.err.println("Unable to bind to registry: " + e);
        }

        System.out.println("Server ready...");

    }

    
    @Override
    public String appLaunchNotification() throws RemoteException {
        System.out.println("An app connected");
        return "Application launched..";
    }

}