import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
        System.out.println("DSTORE SYSTEM: Starting client socket");
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
                    System.out.println("DSTORE SYSTEM: RECEIEVED = " + inputLine);
                    String response = interpretInput(inputLine);
                    // System.out.println("DSTORE SYSTEM: SENDING = " + response);
                    // out.println(response);
                }
            }
            
            System.out.println("DSTORE SYSTEM: CLOSING");

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
            System.out.println("DSTORE SYSTEM: STORE COMMAND DETECTED ");

            this.fileName = words[1];
            this.fileSize = Integer.parseInt(words[2]);
            data = new byte[fileSize];
            // dstore.beginStore(fileName);
            // response = "ACK";
            System.out.println("DSTORE SYSTEM: Sending ACK");
            out.println("ACK");
            handleFile();
        }


        return response;
    }

    private void handleFile(){

        int bytesRead = 0;
        int current = 0;

        File file = new File(dstore.getFolderName() + File.separator + fileName);

        try {
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

                // bytesRead = clientSocket.getInputStream().read(data,0,data.length);
                // current = bytesRead;

                do {
                    bytesRead = clientSocket.getInputStream().read(data,current,fileSize-current);
                    if (bytesRead >= 0){
                        current += bytesRead;
                    }
                    System.out.println("DSTORE SYSTEM: Downloading file " + bytesRead + "/" + fileSize);
                } while (bytesRead < fileSize);

                outputStream.write(data,0,current);
                outputStream.flush();
                System.out.println("DSTORE SYSTEM: File  " + fileName + " downloaded");

                dstore.sendStoreAck(fileName);

                outputStream.close();
                this.closed = true;

        } catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }


    }

}
