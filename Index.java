import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Index {
    
    private ConcurrentHashMap<String,IndexEntry> index;

    public Index(){
        index = new ConcurrentHashMap<>();
    }

    public void addNewEntry(String fileName){
        index.put(fileName, new IndexEntry());
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

}
