import java.io.File;
import java.io.IOException;
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

    public void main(String[] args){
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
            clientServerSocket = new ServerSocket(cport);
            System.out.println("Server socket open: " + !clientServerSocket.isClosed());

            Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
                public void run(){
                    try {
                        clientServerSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Error thrown on closing server socket");
                        e.printStackTrace();
                    }
                }
            })));
            
            // startClientServerSocket();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

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
            folderPath.delete();
            folderPath.mkdirs();
        }
    }
}
