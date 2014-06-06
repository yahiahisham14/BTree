package btree;

import global.PageId;

// Referenced classes of package btree:
//            DataClass

public class IndexData extends DataClass
{

    public String toString()
    {
        return (new Integer(pageId.pid)).toString();
    }

    IndexData(PageId pageid)
    {
        pageId = new PageId(pageid.pid);
    }

    IndexData(int i)
    {
        pageId = new PageId(i);
    }

    protected PageId getData()
    {
        return new PageId(pageId.pid);
    }

    protected void setData(PageId pageid)
    {
        pageId = new PageId(pageid.pid);
    }

    private PageId pageId;
}