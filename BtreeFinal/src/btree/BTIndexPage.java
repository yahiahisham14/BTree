package btree;

import java.io.IOException;

import diskmgr.Page;
import global.PageId;
import global.RID;
import heap.InvalidSlotNumberException;
import heap.Tuple;

public class BTIndexPage extends BTSortedPage {

	public BTIndexPage(int i) throws ConstructPageException, IOException {
		super(i);
		setType(NodeType.INDEX);
		// TODO Auto-generated constructor stub
	}

	public BTIndexPage(Page page, int i) throws ConstructPageException,
			IOException {
		super(page, i);
		setType(NodeType.INDEX);
		// TODO Auto-generated constructor stub
	}

	public BTIndexPage(PageId pageid, int i) throws ConstructPageException,
			IOException {
		super(pageid, i);
		setType(NodeType.INDEX);
		// TODO Auto-generated constructor stub
	}

	public RID insertKey(KeyClass key, PageId pageNo) throws InsertRecException {
		
		RID temp=insertRecord(new KeyDataEntry(key, pageNo));

		return temp;
	}

	public KeyDataEntry getFirst(RID rid) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException, IOException,
			InvalidSlotNumberException {
		if (getSlotCnt() <= 0)
			return null;
		RID first = firstRecord();
		Tuple t = getRecord(first);
		rid.pageNo=first.pageNo;
		rid.slotNo=first.slotNo;
		return BT.getEntryFromBytes(t.getTupleByteArray(), t.getOffset(),
				t.getLength(), keyType, NodeType.INDEX);
	}

	public KeyDataEntry getNext(RID rid) throws IOException,
			InvalidSlotNumberException, KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		rid.slotNo++;
		if (rid.slotNo >= getSlotCnt())
			return null;
		Tuple t = super.getRecord(rid);
		return BT.getEntryFromBytes(t.getTupleByteArray(), t.getOffset(),
				t.getLength(), keyType, NodeType.INDEX);

	}

	public PageId getPageNoByKey(KeyClass key) throws IndexSearchException {

		KeyDataEntry entry;
		int i;
		try {
			for (i = getSlotCnt() - 1; i >= 0; i--) {
				entry = BT.getEntryFromBytes(getpage(), getSlotOffset(i),
						getSlotLength(i), keyType, NodeType.INDEX);
				if (BT.keyCompare(key, entry.key) >= 0) {
					return ((IndexData) entry.data).getData();
				}
			}
			return getPrevPage();
		} catch (Exception e) {
			throw new IndexSearchException(e, "Get entry failed");
		}

	}

	public PageId getLeftLink() throws IOException {
		return getPrevPage();
	}

	public void setLeftLink(PageId left) throws IOException {
		setPrevPage(left);
	}

}
