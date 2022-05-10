import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Index {
    
    private ConcurrentHashMap<String,IndexEntry> index;

    public Index(){
        index = new ConcurrentHashMap<>();
    }

    public Boolean addNewEntry(String fileName){
        if (!index.contains(fileName)){
            System.out.println("INDEX: Adding file: " + fileName);
            index.put(fileName, new IndexEntry());
            return true;
        } else return false;
    }

    // public void addNewEntry(String fileName, Dstore dstore){
    //     if (index.contains(fileName)){
    //         index.get(fileName).addDstore(dstore);
    //     } else {
    //         index.put(fileName, new IndexEntry(dstore));

    //     }
    // }

    public void addDstoreToFile(String fileName, Dstore dstore){
        if (index.contains(fileName))
        index.get(fileName).addDstore(dstore);
    }

    public ArrayList<String> getReadyFilenames(){

        ArrayList<String> fileNames = new ArrayList<>();

        for (Map.Entry<String,IndexEntry> e : index.entrySet()) {
            if (e.getValue().isAvailable()){
                fileNames.add(e.getKey());
            }
        }

        return fileNames;
    }

    public IndexEntry getEntry(String fileName){
        return index.get(fileName);
    }
    

}
