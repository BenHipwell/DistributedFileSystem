import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Controller {
    
    private int cport;
    private int rFactor;
    private double timeout;
    private int rebalance;

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;

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
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), this);
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

    public ArrayList<Integer> handleStoreRequest(String fileName){
        ArrayList<Integer> allocatedDstores = new ArrayList<>();
        
        index.addNewEntry(fileName);
        //allocate dstores
        //update index entry dstore list

        //temp
        allocatedDstores.add(12346);
        allocatedDstores.add(64321);

        return allocatedDstores;
    }

}
