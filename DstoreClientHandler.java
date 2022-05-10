import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DstoreClientHandler extends Thread {
    private Socket clientSocket;
    private Dstore dstore;
    private PrintWriter out;
    private BufferedReader in;

    private byte[] data;

    private boolean closed;

    private String fileName;
    private int fileSize;

    public DstoreClientHandler(Socket clientSocket, Dstore dstore){
        System.out.println("Starting client socket");
        this.clientSocket = clientSocket;
        this.dstore = dstore;
        closed = false;
    }

    public void run(){
        try {
            while(!closed){

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;

                if ((inputLine = in.readLine()) != null){
                    // out.println(inputLine);
                    System.out.println("SYSTEM: RECEIEVED = " + inputLine);
                    String response = interpretInput(inputLine);
                    System.out.println("SYSTEM: SENDING = " + response);
                    out.println(response);
                }
            }
            
            System.out.println("SYSTEM: CLOSING");

            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

    private String interpretInput(String input){
        
        String[] words = input.split(" ");
        String response = "";

        if (words[0].equals("STORE") && words.length == 3){
            this.fileName = words[1];
            this.fileSize = Integer.parseInt(words[2]);

            data = new byte[fileSize];

            dstore.beginStore(fileName);
            response = "ACK";
        }


        return response;
    }

    private void handleFile(File file){
        
    }

}
