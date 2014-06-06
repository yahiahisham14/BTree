package btree;

import java.awt.HeadlessException;
import java.io.IOException;

import diskmgr.Page;
import global.PageId;
import global.RID;
import heap.InvalidSlotNumberException;
import heap.Tuple;

public class BTLeafPage extends BTSortedPage {

	public BTLeafPage(int i) throws ConstructPageException, IOException {
		super(i);
		setType(NodeType.LEAF);
		
	}

	public BTLeafPage(Page page, int i) throws ConstructPageException,
			IOException {
		super(page, i);
		setType(NodeType.LEAF);
		
	}

	public BTLeafPage(PageId pageid, int i) throws ConstructPageException,
			IOException {
		super(pageid, i);
		setType(NodeType.LEAF);
		
	}

	public RID insertRecord(KeyClass key, RID dataRid)
			throws InsertRecException {
		KeyDataEntry k=new KeyDataEntry(key, dataRid);
		RID t=insertRecord(k);
		return t;
	}

	public KeyDataEntry getFirst(RID rid) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException,
			InvalidSlotNumberException, IOException {
		if (getSlotCnt() <= 0)
			return null;
		RID first = firstRecord();
		Tuple t = getRecord(first);
		rid.pageNo=first.pageNo;
		rid.slotNo=first.slotNo;
//		rid = first;
//		rid.copyRid(first);
		return BT.getEntryFromBytes(t.getTupleByteArray(), t.getOffset(),t.getLength(), keyType, NodeType.LEAF);
	}

	public KeyDataEntry getNext(RID rid) throws InvalidSlotNumberException,
			IOException, KeyNotMatchException, NodeNotMatchException,
			ConvertException {
		rid.slotNo++;
		if (rid.slotNo >= getSlotCnt())
			return null;
		Tuple t = getRecord(rid);
		return BT.getEntryFromBytes(t.getTupleByteArray(), t.getOffset(),
				t.getLength(), keyType, NodeType.LEAF);
	}

	public KeyDataEntry getCurrent(RID rid) throws IOException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			InvalidSlotNumberException {
		if (rid.slotNo >= getSlotCnt())
			return null;
		Tuple t = getRecord(rid);
		return BT.getEntryFromBytes(t.getTupleByteArray(), t.getOffset(),
				t.getLength(), keyType, NodeType.LEAF);
	}

	public boolean delEntry(KeyDataEntry dEntry) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException,
			InvalidSlotNumberException, DeleteRecException, IOException {
		KeyDataEntry dataE;
		RID r = new RID();

		for (dataE = getFirst(r); dataE != null; dataE = getNext(r)) {
			if (dataE.equals(dEntry)) {
				if (deleteSortedRecord(r) == false)
					return false;
				return true;
			}
		}
		return false;
	}

}
