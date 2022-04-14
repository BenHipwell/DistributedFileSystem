public class Dstore {

    static int port;
    static int cport;
    static double timeout;
    static String folder;

    public static void main(String[] args){
        if (args.length == 4){
            port = Integer.parseInt(args[0]);
            cport = Integer.parseInt(args[1]);
            timeout = Double.parseDouble(args[2]);
            folder = args[3];
        }
    }
}
