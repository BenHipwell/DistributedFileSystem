import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

public class Index {
    
    private ConcurrentHashMap<String,IndexEntry> index;

    public Index(){
        index = new ConcurrentHashMap<>();
    }

    public Boolean addNewEntry(String fileName, int fileSize){
        if (!index.contains(fileName)){
            System.out.println("INDEX: Adding file: " + fileName);
            index.put(fileName, new IndexEntry(fileSize));
            return true;
        } else return false;
    }

    public void addDstoreToFile(String fileName, Integer dstorePort){
        if (index.contains(fileName))
        index.get(fileName).addDstore(dstorePort);
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

    public void completeStore(String fileName){
        index.get(fileName).setStoreComplete();
    }

    public void completeRemove(String fileName){
        index.get(fileName).setRemoveComplete();
    }

    public IndexEntry getEntry(String fileName){
        return index.get(fileName);
    }

    public KeySetView<String, IndexEntry> getFiles(){
        return index.keySet();
    }
    

}
