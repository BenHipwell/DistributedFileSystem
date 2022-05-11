import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Dstore {

    private int port;
    private int cport;
    private double timeout;
    private String folderName;

    private ServerSocket clientServerSocket;
    private Socket controllerSocket;

    private ArrayList<String> fileNames;

    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args){
        if (args.length == 4){
            new Dstore(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), args[3]);
        }
    }

    public Dstore(int port, int cport, double timeout, String foldername){
        this.port = port;
        this.cport = cport;
        this.timeout = timeout;
        this.folderName = foldername;

        fileNames = new ArrayList<>();
        initFolder();

        try {
            clientServerSocket = new ServerSocket(port);
            System.out.println("Server socket open: " + !clientServerSocket.isClosed());

            controllerSocket = new Socket(InetAddress.getLoopbackAddress(), this.cport);

            this.out = new PrintWriter(this.controllerSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(controllerSocket.getInputStream()));

            this.out.println("DSTORE" + " " + this.port);

            Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
                public void run(){
                    try {
                        clientServerSocket.close();
                        controllerSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Error thrown on closing server socket");
                        e.printStackTrace();
                    }
                }
            })));
            
            startClientServerSocket();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void startClientServerSocket(){
        System.out.println("Starting");
        while (true){
            //add: if num of dstores >= rFactor
            try {
                DstoreClientHandler dstoreClientHandler = new DstoreClientHandler(clientServerSocket.accept(), this);
                dstoreClientHandler.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendStoreAck(String fileName){
        this.out.println("STORE_ACK " + fileName);
    }

    // public void beginStore(String fileName){
    //     index.addDstoreToFile(fileName, this);
    // }

    public String getFolderName(){
        return folderName;
    }

    public int getNumFiles(){
        return fileNames.size();
    }

    private void initFolder(){
        File folderPath = new File(folderName);

        if (!folderPath.isDirectory()){
            folderPath.mkdirs();
        } else {
            for (File f : folderPath.listFiles()){
                f.delete();
            }
            // folderPath.delete();
            // folderPath.mkdirs();
        }
    }

}
