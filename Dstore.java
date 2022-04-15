public class Dstore {

    private int port;
    private int cport;
    private double timeout;
    private String folderName;

    public void main(String[] args){
        if (args.length == 4){
            port = Integer.parseInt(args[0]);
            cport = Integer.parseInt(args[1]);
            timeout = Double.parseDouble(args[2]);
            folderName = args[3];
        }
    }

    public String getFolderName(){
        return folderName;
    }
}
