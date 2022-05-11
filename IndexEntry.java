import java.util.ArrayList;

public class IndexEntry {
    
    private ArrayList<Integer> dstorePorts;
    private IndexEntryStatus status;

    public IndexEntry(){
        dstorePorts = new ArrayList<>();
        status = IndexEntryStatus.STORE_IN_PROGRESS;
    }

    public IndexEntry(Integer dstorePort){
        dstorePorts = new ArrayList<>();
        dstorePorts.add(dstorePort);
        status = IndexEntryStatus.STORE_IN_PROGRESS;
    }

    public void setStatus(IndexEntryStatus newStatus){
        this.status = newStatus;
    }

    public void setComplete(){
        status = IndexEntryStatus.STORE_COMPLETE;
    }

    public boolean isAvailable(){
        return status == IndexEntryStatus.STORE_COMPLETE;
    }

    public void addDstore(Integer dstorePort){
        dstorePorts.add(dstorePort);
    }

    public ArrayList<Integer> getDstorePorts(){
        return dstorePorts;
    }
    
}

enum IndexEntryStatus {
    STORE_IN_PROGRESS,
    STORE_COMPLETE,
    REMOVE_IN_PROGRESS,
    REMOVE_COMPLETE
}
