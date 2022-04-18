import java.io.File;
import java.util.ArrayList;

public class Dstore {

    private int port;
    private int cport;
    private double timeout;
    private String folderName;

    private ArrayList<String> fileNames;

    public void main(String[] args){
        if (args.length == 4){
            port = Integer.parseInt(args[0]);
            cport = Integer.parseInt(args[1]);
            timeout = Double.parseDouble(args[2]);
            folderName = args[3];

            fileNames = new ArrayList<>();

            initFolder();
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
