import java.util.ArrayList;

public class IndexEntry {
    
    private ArrayList<Dstore> dstores;
    private IndexEntryStatus status;

    public IndexEntry(){
        dstores = new ArrayList<>();
        status = IndexEntryStatus.STORE_IN_PROGRESS;
    }

    public IndexEntry(Dstore dstore){
        dstores = new ArrayList<>();
        dstores.add(dstore);
        status = IndexEntryStatus.STORE_IN_PROGRESS;
    }

    public void setStatus(IndexEntryStatus newStatus){
        this.status = newStatus;
    }

    public boolean isAvailable(){
        return status == IndexEntryStatus.STORE_COMPLETE;
    }

    public void addDstore(Dstore dstore){
        dstores.add(dstore);
    }

}

enum IndexEntryStatus {
    STORE_IN_PROGRESS,
    STORE_COMPLETE,
    REMOVE_IN_PROGRESS,
    REMOVE_COMPLETE
}
