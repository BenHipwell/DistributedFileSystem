import java.io.IOException;
import java.net.ServerSocket;

public class Controller {
    
    static int cport;
    static int rFactor;
    static double timeout;
    static int rebalance;

    static ServerSocket serverSocket;

    public static void main(String[] args){
        if (args.length == 4){
            cport = Integer.parseInt(args[0]);
            rFactor = Integer.parseInt(args[1]);
            timeout = Double.parseDouble(args[2]);
            rebalance = Integer.parseInt(args[3]);
            
            try {
                serverSocket = new ServerSocket(cport);
                System.out.println("Server socket open: " + !serverSocket.isClosed());

                Runtime.getRuntime().addShutdownHook(new Thread((new Runnable() {
                    public void run(){
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            System.out.println("Error thrown on starting socket");
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
    }


    public static void start(){
        System.out.println("Starting");
        while (true){
            try {
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
