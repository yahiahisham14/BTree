package btree;

import java.io.IOException;

import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.SystemDefs;

public class BTFileScan extends IndexFileScan implements GlobalConst {
	private boolean destroyed = false;
	private boolean started = false;
	public BTreeFile btree;
	public KeyClass endKey;
	public BTLeafPage currentLeaf;
	public RID currentRID;
	public boolean directed;
	public KeyDataEntry data;
	public static boolean finished=true;

	public BTFileScan() {
	}

	@Override
	// leaf page constructor
	public KeyDataEntry get_next() throws ScanException {
		KeyDataEntry tarKeyDataEntry = null;
		try {
			if (!destroyed) {
				if (!started) {
					started = true;
					if (!directed || currentRID.slotNo == 0)
					{
						data=currentLeaf.getFirst(currentRID);
						
							
						return data;
					}
					currentRID.slotNo--;
					data=currentLeaf.getNext(currentRID);
					
						
					return data;
				}
				data = currentLeaf.getNext(currentRID);
				if (endKey != null && BT.keyCompare(data.key, endKey) >= 0) {
					
						
					return null;
				}

				if (data == null) {
					PageId nextPID;
					nextPID = currentLeaf.getNextPage();
					if (nextPID.pid == -1) {
						// scan finished
						
						
						return null;
					} else {
						SystemDefs.JavabaseBM.unpinPage(
								currentLeaf.getCurPage(), false);
						currentLeaf = new BTLeafPage(nextPID,
								btree.headerPage.get_keyType());
						RID firstRID = new RID();
						data = currentLeaf.getFirst(firstRID);
						currentRID = firstRID;
						
						
						return data;
					}
				} else {
					if (endKey == null || !data.key.equals(endKey)) {
						
						
						return data;
					} else {
						// end
						
						
						return null;
					}
				}
			}
		} catch (Exception e) {
			throw new ScanException(e, "Bad Scan");
		}
		return tarKeyDataEntry;
	}

	public void delete_current() throws ScanException {
		try {
			if (!destroyed) {
				
					
				currentLeaf.delEntry(data);
			}
		} catch (Exception e) {
			throw new ScanException(e, "Bad Scan!");
		}

	}

	public int keysize() {
		int x;
		try {
			x = btree.headerPage.get_maxKeySize();
		} catch (Exception e) {
			return -1;
		}
		return x;
	}

	public void destroyBTreeFileScan() throws IOException, ReplacerException,
			PageUnpinnedException, HashEntryNotFoundException,
			InvalidFrameNumberException {
		if (!destroyed) {
			SystemDefs.JavabaseBM.unpinPage(currentLeaf.getCurPage(), false);
		}
		destroyed = true;
	}
}
