package btree;

import global.PageId;
import global.RID;

// Referenced classes of package btree:
//            DataClass

public class LeafData extends DataClass
{

    public String toString()
    {
        String s = "[ " + (new Integer(myRid.pageNo.pid)).toString() + " " + (new Integer(myRid.slotNo)).toString() + " ]";
        return s;
    }

    LeafData(RID rid)
    {
        myRid = new RID(rid.pageNo, rid.slotNo);
    }

    public RID getData()
    {
        return new RID(myRid.pageNo, myRid.slotNo);
    }

    public void setData(RID rid)
    {
        myRid = new RID(rid.pageNo, rid.slotNo);
    }

    private RID myRid;
}