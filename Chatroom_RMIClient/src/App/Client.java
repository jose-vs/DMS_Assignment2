package App;

import Remote.IClient;
import Remote.IConnections;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
public class Client implements IClient, Serializable {

    private static final long serialVersionUID = 4539829837598745973L;
    public static final String REGISTRY_URL = "localhost";

    //current state of the client
    private String name;
    private boolean isConnected;

    //remote objects
    private Registry registry;
    private IConnections remoteProxy;

    //app object used to connect to the interface and print messages
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
     * connects the client to the interface
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

            //create a client stub to send back to the server so that it
            //can bind the client to the rmi registry
            IClient stub = (IClient) UnicastRemoteObject.exportObject(this, 0);

            if (!this.remoteProxy.connectUser(name, stub)) {
                this.app.postMessage("[Server]: Error, this name is not available.\n");

                return false;
            }

            //update client state
            this.name = name;
            this.isConnected = true;

        } catch (RemoteException e) {
            System.err.println("[Error]: " + e);
            System.err.print("[Stack Trace]: ");
            this.app.postMessage("[Server]: Error with the server.\n");

            return false;
        }

        //send to all clients
        broadcastConnection();

        //retrieve messages sent by other clients
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
            // unbind the user on the server side.
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

        //broadcasts that this user has disconnected to the other clients
        broadcastDisconnection();

        // Remove the connected users so that the app is fully resetted
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
            //saves the message in the server and retrieve its time
            String time = this.remoteProxy.addMessage(this.name, message);

            remoteProxy.getUserNames().forEach(user -> {
                try {
                    //search each user in the registry and send the message to
                    //the client
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
     * sends the clients connection to every user
     */
    private void broadcastConnection() {
        try {
            this.remoteProxy.getUserNames().forEach(user -> {
                try {
                    //searches if the user exists then sending a connected notification to it
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
     * broadcasts a user disconnecting from the server to every user
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
     * retrieves every message sent from the server to display it on the client interface
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

    private class RicartArgrwala {

        private int timestamp; // current Lamport timestamp
        private int pendingReplies; //no. replies pending before cs allowed
        private boolean ownRequest; //whether this process has requested cs
        private int ownRequestTimestamp; // used when ownRequest
        private boolean inCriticalSection; // whether currently in cs
        private boolean stopRequested;

        public static final String REQUEST = "request";
        public static final String OKAY = "okay";
        public static final String TIMESTAMP = "timestamp";
        public static final String JOIN = "join";

        public Queue msgQueue = new ConcurrentLinkedQueue<>();

        public void receiveMessage(String message, IClient stub) {
            if (message.startsWith(REQUEST)) {
                try {

                } catch (NumberFormatException e) {

                }
            }
        }

        public void requestCritical() {

        }

        public void releaseCritical() {

        }

        private void increaseTime() {
            timestamp++;
        }

        private int getTime() {
            return timestamp;
        }

        private void updateTime(int otherTimestamp) {
            timestamp = Math.max(timestamp, otherTimestamp);
        }
    }

    private class ChandyLamport {

    }

    private class Message {

        private String type;
        private String message;
        private IClient stub;
        private int timestamp;

        public Message(String type, IClient stub, int timestamp) {
            this.type = type;
            this.stub = stub;
            this.timestamp = timestamp;
        }

        public Message(String Type, String message, IClient stub, int timestamp) {
            this.type = type;
            this.message = message;
            this.stub = stub;
            this.timestamp = timestamp;
        }

        public IClient getStub() {
            return this.stub;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public int getTimestamp() {
            return this.timestamp;
        }
    }
}
