import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    
    private int cport;
    private double timeout;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public static void main(String[] args){
        if (args.length == 2){
            new TestClient(Integer.parseInt(args[0]), Double.parseDouble(args[1]));
        }
    }

    public TestClient(int cport, double timeout){
        this.cport = cport;
        this.timeout = timeout;

        try {
            socket = new Socket(InetAddress.getLoopbackAddress(), this.cport);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out.println("STORE fileName1 420");
            System.out.println("SYSTEM: is connected?" + socket.isConnected());
            
            String line;

            while ((line = in.readLine()) != null){
                System.out.println("SYSTEM: CLIENT RECEIVED " + line);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }
    }

}
