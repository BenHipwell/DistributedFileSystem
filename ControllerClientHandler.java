import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ControllerClientHandler extends Thread {
    private Socket clientSocket;
    private Controller controller;
    private PrintWriter out;
    private BufferedReader in;

    int dstorePort = 0;

    private boolean closed;

    public ControllerClientHandler(Socket clientSocket, Controller controller){
        System.out.println("CONTROLLER SYSTEM: Starting client socket");
        this.clientSocket = clientSocket;
        this.controller = controller;
        closed = false;
    }

    public void run(){
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            while(!closed){

                String inputLine;

                if ((inputLine = in.readLine()) != null){
                    // out.println(inputLine);
                    System.out.println("CONTROLLER SYSTEM: RECEIEVED = " + inputLine);
                    String response = interpretInput(inputLine);
                    System.out.println("CONTROLLER SYSTEM: SENDING = " + response);
                    out.println(response);
                }
            }
            
            System.out.println("CONTROLLER SYSTEM: CLOSING");

            in.close();
            out.close();
            clientSocket.close();
            if (dstorePort != 0){
                controller.removeDstore(dstorePort);
            }
            this.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    private String interpretInput(String input){
        
        String[] words = input.split(" ");
        String response = "";

        if (words[0].equals("STORE") && words.length == 3){
            String fileName = words[1];
            String fileSize = words[2];
            
            if (!controller.addNewFile(fileName)){
                return "ERROR_FILE_ALREADY_EXISTS";
            }

            ArrayList<Integer> DstorePorts = controller.handleStoreRequest(fileName, this);
            
            response = "STORE_TO";
            for (Integer port : DstorePorts){
                response = response + " " + port;
            }
        } else if (words[0].equals("DSTORE") && words.length == 2){
            dstorePort = Integer.parseInt(words[1]);
            controller.addDstore(dstorePort, this);
        } else if (words[0].equals("STORE_ACK") && words.length == 2){
            String fileName = words[1];
            controller.dstoreAck(dstorePort, fileName);
        } else {
            //Handle invalid request
            return "";
        }
        return response;
    }

    public void sendStoreComplete(){
        //if client, not dstore
        if (dstorePort == 0){
            this.out.println("STORE_COMPLETE");
        }
    }
    // private void handleStoreOperation(){

    // }

}
