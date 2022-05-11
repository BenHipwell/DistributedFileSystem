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
    private ConcurrentHashMap<String, StoreRequest> fileNameToStoreReq;

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
        fileNameToStoreReq = new ConcurrentHashMap<>();   
        
            try {
                serverSocket = new ServerSocket(cport);
                System.out.println("Server socket open: " + !serverSocket.isClosed());

                Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
                    public void run(){
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            System.out.println("Error thrown on closing server socket");
                            e.printStackTrace();
                        }
                    }
                })));
                
                start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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

    public boolean addNewFile(String fileName){
        return index.addNewEntry(fileName);
    }

    public ArrayList<Integer> handleStoreRequest(String fileName, ControllerClientHandler clientEndpoint){
        ArrayList<Integer> allocatedDstorePorts = new ArrayList<>();

        try {
            
            
        
            // index.addNewEntry(fileName);
            //allocate dstores
            //update index entry dstore list

            //temp

            for (Integer i : portToStoreEnd.keySet()){
                allocatedDstorePorts.add(i);
            }

            // allocatedDstorePorts.add(12346);
            // allocatedDstorePorts.add(64321);

        
            fileNameToStoreReq.put(fileName, new StoreRequest(clientEndpoint, allocatedDstorePorts));

            return allocatedDstorePorts;


        } catch (Exception e) {
            e.printStackTrace();
        } 
        // finally {
        //     waitForDstoreAcks(allocatedDstorePorts);
        // }
        return allocatedDstorePorts;
        
    }

    public void waitForDstoreAcks(ArrayList<Integer> dstores){
        //remember Dstores are in constant connection with controller already
        //USE INDEX!!
        //STORE_ACK from Controller-Dstore connection updates index
        //Updating index then updates this somehow
        //Stop waiting when all indexes match up to STORE request
        //Then index -> store complete
        //ControllerClientHandler -> STORE_COMPLETE
    }

    public void dstoreAck(int port, String fileName){
        index.getEntry(fileName).addDstore(port);
        checkStoreComplete(fileName);
    }

    private void checkStoreComplete(String fileName){
        StoreRequest storeRequest = fileNameToStoreReq.get(fileName);
        if (storeRequest.getDstorePorts() == index.getEntry(fileName).getDstorePorts()){
            index.completeStore(fileName);
            storeRequest.getClientEndpoint().sendStoreComplete();
        }
    }

    public int getDstoreCount(){
        return portToStoreEnd.size();
    }
}
