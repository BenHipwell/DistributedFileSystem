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

    int dstorePort;

    int dstoreIndex;

    private boolean closed;

    public ControllerClientHandler(Socket clientSocket, Controller controller){
        System.out.println("CONTROLLER SYSTEM: Starting client socket");
        this.clientSocket = clientSocket;
        this.controller = controller;
        closed = false;
        dstorePort = 0;
        dstoreIndex = 0;
    }

    public void run(){
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            while(!closed){

                String inputLine;

                if ((inputLine = in.readLine()) != null){
                    System.out.println("CONTROLLER SYSTEM: RECEIEVED = " + inputLine);
                    interpretInput(inputLine);
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

    private void interpretInput(String input){
        
        // if (controller.enoughDstores()){

            String[] words = input.split(" ");

            if (words[0].equals("STORE") && words.length == 3){
                handleStoreOperation(words);

            } else if (words[0].equals("LOAD") && words.length == 2){
                handleLoadOperation(words);

            } else if (words[0].equals("RELOAD") && words.length == 2){
                dstoreIndex++;
                handleLoadOperation(words);

            } else if (words[0].equals("JOIN") && words.length == 2){
                dstorePort = Integer.parseInt(words[1]);
                controller.addDstore(dstorePort, this);

            } else if (words[0].equals("STORE_ACK") && words.length == 2){
                String fileName = words[1];
                controller.dstoreAck(dstorePort, fileName);

            } else if (words[0].equals("REMOVE") && words.length == 2){
                handleRemoveOperation(words);

            } else if (words[0].equals("REMOVE_ACK") && words.length == 2){
                String fileName = words[1];
                controller.removeAck(dstorePort, fileName);

            } else if (input.equals("LIST")){
                out.println("LIST" + controller.getFileList());

            } else {
                //Handle invalid request
            }
        // } else {
        //     out.println("ERROR_NOT_ENOUGH_DSTORES");
        // }
    }

    private void handleStoreOperation(String[] words){
        String fileName = words[1];
        int fileSize = Integer.parseInt(words[2]);
            
        if (!controller.addNewFile(fileName, fileSize)){
            out.println("ERROR_FILE_ALREADY_EXISTS");
        }

        ArrayList<Integer> DstorePorts = controller.handleStoreRequest(fileName, this);
            
        String response = "STORE_TO";
        for (Integer port : DstorePorts){
            response = response + " " + port;
        }

        out.println(response);
    }

    private void handleLoadOperation(String[] words){
        int port = controller.getDstoreStroringFile(words[1],dstoreIndex);

        if (port == -1){
            out.println("ERROR_FILE_DOES_NOT_EXIST");
        } else if (port == -2){
            out.println("ERROR_LOAD");
        } else {
            out.println("LOAD_FROM " + port + controller.getFileSize(words[1]));
        }

    }

    private void handleRemoveOperation(String[] words){
        String fileName = words[1];
        try {
            controller.removeFile(fileName, this);
        } catch (Exception e) {
            out.println("ERROR_FILE_DOES_NOT_EXIST");
        }

    }

    synchronized public void sendRemoveToDstore(String fileName){
        System.out.println("CONTROLLER: SENDING REMOVE TO DSTORE " + fileName);
        out.println("REMOVE " + fileName);
    }

    synchronized public void sendStoreCompleteToClient(){
        out.println("STORE_COMPLETE");
    }
    
    synchronized public void sendRemoveCompleteToClient(){
        out.println("REMOVE_COMPLETE");
    }

    synchronized public void sendRebalanceMessage(String message){
        out.println(message);
    }
}
