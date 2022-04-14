
import Remote.IConnections;
import Remote.IMessage;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
public class Server implements IConnections {

    private final ArrayList<String> userNames;
    private ArrayList<IMessage> userMessages;

    public Server() {
        this.userNames = new ArrayList<>();
        this.userMessages = new ArrayList<>();
    }

    @Override
    public boolean isConnected(String name) throws RemoteException {
        if (userNames.contains(name)) {
            return false;
        }

        System.out.println("User connection: " + name);
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
    public ArrayList<IMessage> getUserMessages() throws RemoteException {
        return this.userMessages;
    }

    @Override
    public void setClientMessages(ArrayList<IMessage> messages) throws RemoteException {
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
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("ChatRoom", stub);

            System.out.println("Names bound in RMI registry");

        } catch (RemoteException e) {
            System.err.println("Unable to bind to registry: " + e);
        }

        System.out.println("Server ready...");

    }

}
