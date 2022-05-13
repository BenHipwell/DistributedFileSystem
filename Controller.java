import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    
    private int cport;
    private int rFactor;
    private double timeout;
    private int rebalance;

    private ServerSocket serverSocket;
    private ArrayList<ControllerClientHandler> clients;

    private ConcurrentHashMap<Integer, ControllerClientHandler> portToStoreEnd;
    private ConcurrentHashMap<String, StoreRequest> fileNameToReq;

    private Index index;

    public static void main(String[] args){
        if (args.length == 4){
            new Controller(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3]));
        }
    }

    public Controller(int cport, int rFactor, double timeout, int rebalance){
        this.cport = cport;
        this.rFactor = rFactor;
        this.timeout = timeout;
        this.rebalance = rebalance;

        index = new Index();
        clients = new ArrayList<>();
        portToStoreEnd = new ConcurrentHashMap<>();
        fileNameToReq = new ConcurrentHashMap<>();   
        
            try {
                serverSocket = new ServerSocket(cport);
                System.out.println("Server socket open: " + !serverSocket.isClosed());

                Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
                    public void run(){
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            System.out.println("Error thrown on closing server socket");
                            e.printStackTrace();
                        }
                    }
                })));
                
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public void start(){
        System.out.println("Starting");
        while (true){
            //add: if num of dstores >= rFactor
            try {
                ControllerClientHandler clientHandler = new ControllerClientHandler(serverSocket.accept(), this);
                clients.add(clientHandler);
                clientHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDstore(int port, ControllerClientHandler endpoint){
        clients.remove(endpoint);
        portToStoreEnd.put(port, endpoint);
    }

    public void removeDstore(int port){
        portToStoreEnd.remove(port);
    }

    public boolean addNewFile(String fileName, int fileSize){
        return index.addNewEntry(fileName, fileSize);
    }

    public ArrayList<Integer> handleStoreRequest(String fileName, ControllerClientHandler clientEndpoint){
        ArrayList<Integer> allocatedDstorePorts = new ArrayList<>();

        try {
            // index.addNewEntry(fileName);
            //allocate dstores
            //update index entry dstore list

            //temp allocating all dstores
            for (Integer i : portToStoreEnd.keySet()){
                allocatedDstorePorts.add(i);
            }

            fileNameToReq.put(fileName, new StoreRequest(clientEndpoint, allocatedDstorePorts));

            return allocatedDstorePorts;


        } catch (Exception e) {
            e.printStackTrace();
        } 
        return allocatedDstorePorts;
        
    }

    public void removeFile(String fileName, ControllerClientHandler clientEndpoint){
        IndexEntry entry = index.getEntry(fileName);
        entry.setRemoveInProgress();

        for (Integer i : entry.getDstorePorts()){
            System.out.println("SYSTEM: REMOVING FILE FROM " + i);
            synchronized (portToStoreEnd.get(i)){
                portToStoreEnd.get(i).sendRemoveToDstore(fileName);
            }
        }

        fileNameToReq.put(fileName, new StoreRequest(clientEndpoint, entry.getDstorePorts()));
    }

    public void removeAck(int port, String fileName){
        index.getEntry(fileName).removeDstore(port);
        checkRemoveComplete(fileName);
    }

    public void checkRemoveComplete(String fileName){
        StoreRequest request = fileNameToReq.get(fileName);
        System.out.println("CONTROLLER: Checking if " + fileName + " removal is complete ");

        if (index.getEntry(fileName).getDstorePorts().size() == 0){
            index.completeRemove(fileName);
            fileNameToReq.remove(fileName);
            synchronized (request.getClientEndpoint()){
                request.getClientEndpoint().sendRemoveCompleteToClient();
            }
        }
    }

    public void dstoreAck(int port, String fileName){
        index.getEntry(fileName).addDstore(port);
        checkStoreComplete(fileName);
    }

    private void checkStoreComplete(String fileName){
        StoreRequest storeRequest = fileNameToReq.get(fileName);
        System.out.println("CONTROLLER: Checking if " + fileName + " storage is complete ");

        if (storeRequest.getDstorePorts().equals(index.getEntry(fileName).getDstorePorts())){
            index.completeStore(fileName);
            fileNameToReq.remove(fileName);
            synchronized (storeRequest.getClientEndpoint()){
                storeRequest.getClientEndpoint().sendStoreCompleteToClient();
            }
        }
    }

    public int getDstoreStroringFile(String fileName, int dstoreIndex){
        IndexEntry entry;
        try {
                entry = index.getEntry(fileName);

                if (dstoreIndex < entry.getDstorePorts().size()){
                    return entry.getDstorePorts().get(dstoreIndex);
                } else return -2;
        
        } catch (Exception e){
                return -1;
        }

    }

    public boolean enoughDstores(){
        return portToStoreEnd.size() >= rFactor;
    }

    public int getFileSize(String fileName){
        return index.getEntry(fileName).getFileSize();
    }

    public int getDstoreCount(){
        return portToStoreEnd.size();
    }

    public int getClientCount(){
        return clients.size();
    }
}
