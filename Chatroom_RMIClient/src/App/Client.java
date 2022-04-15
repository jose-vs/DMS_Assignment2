package App;

import Remote.IClient;
import Remote.IConnections;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
public class Client implements IClient, Serializable {

    private static final long serialVersionUID = 4539829837598745973L;
    public static final String REGISTRY_URL = "localhost";

    private String name;
    private boolean isConnected;
    private Registry registry;
    private IConnections remoteProxy;

    private App app;

    public Client() {
        this.isConnected = false;

        /**
         * fetches remote objects
         */
        try {
            registry = LocateRegistry.getRegistry(REGISTRY_URL);
            remoteProxy = (IConnections) registry.lookup("/server");
            System.out.println(remoteProxy.appLaunchNotification());

        } catch (RemoteException e) {
            System.err.println("Unable to use registry: " + e);

        } catch (NotBoundException e) {
            System.err.println("Name greeting not currently bound: " + e);

        }

    }

    /**
     *
     * @param app
     */
    public final void connectInterface(App app) {
        this.app = app;
    }

    /**
     * 
     * @param name
     * @return 
     */
    public boolean connectUser(String name) {

        this.app.postMessage("[Server]: Connecting to server...\n");

        try {

            IClient stub = (IClient) UnicastRemoteObject.exportObject(this, 0);

            if (!this.remoteProxy.isConnected(name, stub)) {
                this.app.postMessage("[Server]: Error, this name is not available.\n");

                return false;
            }

            this.name = name;
            this.isConnected = true;

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error with the server.\n");

            return false;
        }

        broadcastConnection();
        getMessageHistory();

        this.app.postMessage("[Server]: You are connected as \"" + this.name + "\".\n");

        return true;
    }

    /**
     * 
     * @return 
     */
    public boolean disconnectUser() {
        this.app.postMessage("[Server]: disconnecting...\n");

        try {
            // Try to unbind the user on the server side.
            this.registry.unbind("/client/" + this.name);
            UnicastRemoteObject.unexportObject(this, true);

            this.remoteProxy.disconnectUser(this.name);
            this.isConnected = false;

        } catch (NotBoundException | RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error, cannot completely disconnect you. "
                    + "Your username may be unavailable until the server restarts.\n");
        }

        broadcastDisconnection();

        // Remove the connected users.
        this.app.clearAllUsers();

        this.app.postMessage("[Server]: Disconnected succesffuly.\n");

        return true;
    }

    /**
     * 
     * @param message 
     */
    public void sendMessage(String message) {
        try {
            String time = this.remoteProxy.addMessage(this.name, message);

            remoteProxy.getUserNames().forEach(user -> {
                try {
                    IClient client = (IClient) this.registry.lookup("/client/" + user);
                    client.postMessage(time, this.name, message);

                } catch (NotBoundException | RemoteException e) {
                    System.err.println("[Error]: " + e);
                    System.err.print("[Stack Trace]: ");
                    this.app.postMessage("[Server]: Error, cannot send this message "
                            + "to \"" + user + "\".\n");

                }
            });

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error, cannot send this message.\n");
        }
    }
    
    /**
     * 
     */
    private void broadcastConnection() {
        try {
            this.remoteProxy.getUserNames().forEach(user -> {
                try {
                    IClient client = (IClient) this.registry.lookup("/client/" + user);
                    client.connectedNotif(this.name);

                    if (!user.equals(this.name)) {
                        this.app.addToAllUsers(user);
                    }

                } catch (NotBoundException | RemoteException e) {
                    System.err.println("[Error]: " + e);
                    System.err.print("[Stack Trace]: ");
                    e.printStackTrace();
                    this.app.postMessage("[Server]: Error. Cannot broadcast your connection "
                            + "to \"" + user + "\".\n");

                }
            });

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            e.printStackTrace();
            this.app.postMessage("[Server]: Error. Cannot broadcast your connection.\n");

        }
    }

    /**
     * 
     */
    private void broadcastDisconnection() {
        try {
            remoteProxy.getUserNames().forEach(user -> {
                try {
                    IClient client = (IClient) this.registry.lookup("/client/" + user);
                    client.disconnectedNotif(this.name);

                } catch (NotBoundException | RemoteException e) {
                    System.err.println("[Error]: " + e);
                    System.err.print("[Stack Trace]: ");
                    this.app.postMessage("[Server]: Error. Cannot broadcast your disconnection."
                            + "to \"" + user + "\".\n");

                }
            });

            disconnectedNotif(this.name);

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error. Cannot broadcast your disconnection.\n");
        }
    }

    /**
     * 
     */
    private void getMessageHistory() {
        this.app.postMessage("[Server]: Loading previous messages...\n");

        try {
            remoteProxy.getUserMessages().forEach(userMessages -> {
                try {
                    postMessage(userMessages.getTimeSent(), userMessages.getSender(), userMessages.getMessage());
                } catch (RemoteException e) {
                    System.err.println("[Error]: " + e);
                    System.err.print("[Stack Trace]: ");
                    this.app.postMessage("[Server]: Error, Failed to load message history. "
                            + "from the history.\n");
                }
            });

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error. Failed to load message history.\n");

        }
    }

    /**
     * 
     * @return 
     */
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void postMessage(String time, String sender, String message) throws RemoteException {
        this.app.postMessage("(" + time + ") " + sender + ": " + message + "\n");
    }

    @Override
    public void connectedNotif(String user) throws RemoteException {
        if (!user.equals(this.name)) {
            this.app.postMessage(user + " entered the chatroom.\n");
        }

        this.app.addToAllUsers(user);
    }

    @Override
    public void disconnectedNotif(String user) throws RemoteException {
        this.app.removeFromAllUsers(user);

        if (!user.equals(this.name)) {
            this.app.postMessage(user + " left the chatroom.\n");
        }
    }
}
