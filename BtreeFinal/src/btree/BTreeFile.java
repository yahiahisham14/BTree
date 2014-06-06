package btree;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidBufferException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import diskmgr.DiskMgrException;
import diskmgr.FileEntryNotFoundException;
import diskmgr.FileIOException;
import diskmgr.InvalidPageNumberException;
import diskmgr.Page;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.SystemDefs;
import heap.HFPage;
import heap.InvalidSlotNumberException;

public class BTreeFile extends IndexFile implements GlobalConst {

	public BTreeHeaderPage headerPage = null;
	// public BTLeafPage root = null;
	public String header_Name;
	public static KeyDataEntry newchildentry = null;
	
	public static boolean isTraced=false;
	
	public static void traceFilename(String filename) throws IOException {
		isTraced=true;
	}

	public BTreeFile(String filename) throws Exception {
		// get header page id
		try {
			PageId headerID = SystemDefs.JavabaseDB.get_file_entry(filename);
			headerPage = new BTreeHeaderPage(headerID);
			header_Name = filename;
			
				
		} catch (Exception e) {
			throw new Exception("The isn't exist !!!!");
		}
	}

	public BTreeHeaderPage getHeaderPage() {
		return headerPage;
	}

	public BTreeFile(String filename, int keytype, int keysize,
			int delete_fashion) {
		try {
			if (SystemDefs.JavabaseDB.get_file_entry(filename) != null) {
				// open the exist file
				PageId headerID = SystemDefs.JavabaseDB
						.get_file_entry(filename);
				headerPage = new BTreeHeaderPage(headerID);
				header_Name = filename;
			
			
			} else {
				// create new file
				Page Page = new Page();
				PageId headerid = SystemDefs.JavabaseBM.newPage(Page, 1);
				SystemDefs.JavabaseDB.add_file_entry(filename, headerid);
				headerPage = new BTreeHeaderPage(Page);
				headerPage.setCurPage(headerid);
				headerPage.set_keyType((short) keytype);
				// create root page

				BTLeafPage root = new BTLeafPage(keytype);

				headerPage.set_rootId(root.getCurPage());
				header_Name = filename;
				SystemDefs.JavabaseBM.unpinPage(root.getCurPage(), true);
				// root.setCurPage(rootID);
			
			}
		} catch (Exception e) {

		}
	}

	public void close() throws ReplacerException, PageUnpinnedException,
			HashEntryNotFoundException, InvalidFrameNumberException,
			IOException, CloseException {
		if(!BTFileScan.finished)
		{
			throw new CloseException(null, " Scane hasn't been closed");
		}
		if (headerPage != null) {
			
			SystemDefs.JavabaseBM.unpinPage(headerPage.getCurPage(), true);
			headerPage = null;
		}
	}

	private PageId search(KeyClass key) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, IndexSearchException,
			ConstructPageException {

		return find(key, headerPage.get_rootId());

	}

	private PageId find(KeyClass key, PageId currPage) throws IOException,
			ReplacerException, HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, IndexSearchException,
			ConstructPageException {
		Page temp = new Page();
		SystemDefs.JavabaseBM.pinPage(currPage, temp, false);
		BTSortedPage spage = new BTSortedPage(temp, headerPage.get_keyType());
		if ((spage).getType() == NodeType.LEAF) {
			SystemDefs.JavabaseBM.unpinPage(currPage, false);
			return spage.getCurPage();
		} else {
			BTIndexPage i = new BTIndexPage(temp, headerPage.get_keyType());
			return find(key, (i).getPageNoByKey(key));
		}
	}

	public void destroyFile() throws IOException, ReplacerException,
			PageUnpinnedException, HashEntryNotFoundException,
			InvalidFrameNumberException, FileEntryNotFoundException,
			FileIOException, InvalidPageNumberException, DiskMgrException,
			InvalidBufferException, HashOperationException,
			PageNotReadException, BufferPoolExceededException,
			PagePinnedException, BufMgrException, ConstructPageException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			InvalidSlotNumberException {
		if (headerPage != null) {
			PageId headID = headerPage.get_rootId();
			if (headID.pid != INVALID_PAGE) {
				//trace=null;
				destroy_All(headID);
				
			}
			SystemDefs.JavabaseDB.delete_file_entry(header_Name);
			headerPage = null;
		}
	}

	private void destroy_All(PageId pgId) throws ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			IOException, ConstructPageException, KeyNotMatchException,
			NodeNotMatchException, ConvertException,
			InvalidSlotNumberException, HashEntryNotFoundException,
			InvalidBufferException, DiskMgrException {
		BTSortedPage sortedpage;
		Page page = new Page();
		SystemDefs.JavabaseBM.pinPage(pgId, page, false);
		System.out.println(pgId.pid + "  pin");
		sortedpage = new BTSortedPage(page, headerPage.get_keyType());

		if (sortedpage.getType() == NodeType.INDEX) {
			BTIndexPage index = new BTIndexPage(page, headerPage.get_keyType());
			RID R = new RID();
			PageId newID = pgId;
			KeyDataEntry data_Entry;
			KeyDataEntry data_Entry_temp;
			destroy_All(index.getLeftLink());
			for (data_Entry = index.getFirst(R); data_Entry != null; data_Entry = index.getNext(R)) {
				
				newID = ((IndexData) (data_Entry.data)).getData();
				destroy_All(newID);
			}
			System.out.println(pgId.pid + "  unpin");
			SystemDefs.JavabaseBM.unpinPage(pgId, false);
			SystemDefs.JavabaseBM.freePage(pgId);
		} else {
			System.out.println(pgId.pid + "  unpin");
			SystemDefs.JavabaseBM.unpinPage(pgId, false);
			SystemDefs.JavabaseBM.freePage(pgId);
		}

	}

	@Override
	public void insert(KeyClass data, RID rid) {
		// TODO Auto-generated method stub

		try {
			newchildentry = null;
			innerInsert(data, rid, headerPage.get_rootId(), newchildentry);
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}

	}

	private void innerInsert(KeyClass data, RID rid, PageId pageid,
			KeyDataEntry newchild) throws IOException, IndexSearchException,
			ReplacerException, HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, ConstructPageException,
			InsertRecException, DiskMgrException, KeyNotMatchException,
			NodeNotMatchException, ConvertException,
			InvalidSlotNumberException, DeleteRecException {

		Page pge = new Page();
		System.out.println(pageid.pid + " pin");
		SystemDefs.JavabaseBM.pinPage(pageid, pge, false);
		BTSortedPage page = new BTSortedPage(pge, headerPage.get_keyType());
		if (page.getType() == NodeType.INDEX) {
			// not a leaf node
			// find i such that K1<Ki<K final & node pointer <-- next id
			PageId nextid = new BTIndexPage(pge, headerPage.get_keyType())
					.getPageNoByKey(data);
			System.out.println(pageid.pid + " unpin");
			SystemDefs.JavabaseBM.unpinPage(pageid, false);
			innerInsert(data, rid, nextid, newchildentry);
			if (newchildentry == null) {
				// System.out.println(pageid.pid+" unpin");
				// SystemDefs.JavabaseBM.unpinPage(pageid, true);
				return;
			} else {
				BTIndexPage index = new BTIndexPage(pge,
						headerPage.get_keyType());
				index.setCurPage(pageid);
				if (index.available_space() >= BT.getKeyDataLength(
						newchildentry.key, NodeType.INDEX)) {
					// there is space
					System.out.println("zlot count  " + index.getSlotCnt());
					System.out.println("avalible zepazes   "
							+ index.available_space());
					index.insertRecord(newchildentry);
					newchildentry = null;
					return;

				} else {
					// there is no space && split
					BTIndexPage newIndex = new BTIndexPage(
							headerPage.get_keyType());
					System.out.println(newIndex.getCurPage().pid + " pin");
					// split the Index
					RID lrid = new RID();
					int last = index.getSlotCnt();
					KeyDataEntry temp = index.getFirst(lrid);
					KeyDataEntry temp2 = null;
					while (lrid.slotNo != ((last + 1) / 2) - 1) {

						temp = index.getNext(lrid);
					}
					if (BT.keyCompare(newchildentry.key, temp.key) > 0) {
						newIndex.insertRecord(newchildentry);
					} else {
						newIndex.insertRecord(temp);
						index.deleteSortedRecord(lrid);
						index.insertRecord(newchildentry);
					}
					temp = index.getNext(lrid);

					RID tete = new RID();
					tete.pageNo = lrid.pageNo;
					tete.slotNo = lrid.slotNo;

					for (int i = (last + 1) / 2; i < last - 1; i++) {
						newIndex.insertRecord(temp);
						temp2 = temp;
						temp = index.getNext(tete);
						tete.pageNo = lrid.pageNo;
						tete.slotNo = lrid.slotNo;
						index.deleteSortedRecord(tete);
					}
					newIndex.insertRecord(temp);
					index.deleteSortedRecord(tete);

					RID asserid = new RID();
					KeyDataEntry asser = newIndex.getFirst(asserid);
					newIndex.deleteSortedRecord(asserid);
					newIndex.setLeftLink(((IndexData) (asser.data)).getData());
					newchildentry = new KeyDataEntry(asser.key,
							newIndex.getCurPage());

					// n is the root
					if (index.getCurPage().pid == headerPage.get_rootId().pid) {
						BTIndexPage newroot = new BTIndexPage(
								headerPage.get_keyType());
						System.out.println(newroot.getCurPage().pid + " pin");
						headerPage.set_rootId(newroot.getCurPage());
						newroot.setLeftLink(index.getCurPage());
						newroot.insertRecord(newchildentry);
						System.out.println(newroot.getCurPage().pid + " unpin");
						SystemDefs.JavabaseBM.unpinPage(newroot.getCurPage(),
								true);
					}
					System.out.println(newIndex.getCurPage().pid + " unpin");
					SystemDefs.JavabaseBM
							.unpinPage(newIndex.getCurPage(), true);
					return;
				}
			}

		} else {
			// leaf Node
			BTLeafPage Leaf = new BTLeafPage(pge, headerPage.get_keyType());
			// Leaf.setCurPage(pageid);
			if (Leaf.available_space() >= BT.getKeyDataLength(data,
					NodeType.LEAF)) {
				// the page has space
				Leaf.insertRecord(data, rid);
				System.out.println(pageid.pid + " unpin");
				SystemDefs.JavabaseBM.unpinPage(pageid, true);
				return;

			} else {
				BTLeafPage newLeaf = new BTLeafPage(headerPage.get_keyType());
				System.out.println(newLeaf.getCurPage().pid + " pin");

				// split the leaf
				RID lrid = new RID();
				int last = Leaf.getSlotCnt();
				KeyDataEntry temp = Leaf.getFirst(lrid);
				KeyDataEntry temp2 = null;
				while (lrid.slotNo != ((last + 1) / 2) - 1) {
					temp = Leaf.getNext(lrid);
				}

				if (BT.keyCompare(data, temp.key) > 0) {
					newLeaf.insertRecord(data, rid);

				} else {
					newLeaf.insertRecord(temp);
					Leaf.delEntry(temp);
					Leaf.insertRecord(data, rid);
				}
				temp = Leaf.getNext(lrid);

				RID tete = new RID();
				tete.pageNo = lrid.pageNo;
				tete.slotNo = lrid.slotNo;
				for (int i = (last + 1) / 2; i < last - 1; i++) {
					newLeaf.insertRecord(temp);
					temp2 = temp;
					temp = Leaf.getNext(tete);
					tete.pageNo = lrid.pageNo;
					tete.slotNo = lrid.slotNo;
					Leaf.delEntry(temp2);
				}
				newLeaf.insertRecord(temp);
				Leaf.delEntry(temp);

				newchildentry = new KeyDataEntry(
						newLeaf.getFirst(new RID()).key, newLeaf.getCurPage());
				// if root was leaf
				if (Leaf.getCurPage().pid == headerPage.get_rootId().pid) {
					BTIndexPage newroot = new BTIndexPage(
							headerPage.get_keyType());
					System.out.println(newroot.getCurPage().pid + " pin");
					headerPage.set_rootId(newroot.getCurPage());
					newroot.setLeftLink(Leaf.getCurPage());
					newroot.insertRecord(newchildentry);
					System.out.println(newroot.getCurPage().pid + " unpin");
					SystemDefs.JavabaseBM.unpinPage(newroot.getCurPage(), true);

				}
				// set the linked List

				HFPage p1 = new HFPage();
				PageId third = Leaf.getNextPage();
				int x = third.pid;
				if (x != -1) {
					System.out.println(third.pid + " pin");
					SystemDefs.JavabaseBM.pinPage(third, p1, false);
					p1.setPrevPage(newLeaf.getCurPage());
				}
				newLeaf.setNextPage(third);
				newLeaf.setPrevPage(Leaf.getCurPage());
				Leaf.setNextPage(newLeaf.getCurPage());
				if (x != -1) {
					System.out.println(x + " unpin");
					SystemDefs.JavabaseBM.unpinPage(new PageId(x), true);
				}
				System.out.println(newLeaf.getCurPage().pid + " unpin");
				SystemDefs.JavabaseBM.unpinPage(newLeaf.getCurPage(), true);
				pageid = Leaf.getCurPage();
			}

		}
		System.out.println(pageid.pid + " unpin");
		SystemDefs.JavabaseBM.unpinPage(pageid, true);
	}

	@Override
	public boolean Delete(KeyClass data, RID rid) {
		// TODO Auto-generated method stub
		try {
			if (headerPage == null)
				return false;
			PageId LeafId = search(data);
			Page pge = new Page();
			SystemDefs.JavabaseBM.pinPage(LeafId, pge, false);
			BTLeafPage LeafPage = new BTLeafPage(pge, headerPage.get_keyType());
			boolean result = (LeafPage).delEntry(new KeyDataEntry(data, rid));
			SystemDefs.JavabaseBM.unpinPage(LeafId, true);
			
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	// SCANNER

	public BTFileScan new_scan(KeyClass lo_key, KeyClass hi_key)
			throws ConstructPageException, IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, IndexSearchException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			InvalidSlotNumberException {
		BTLeafPage startLeaf;
		PageId startPageId;
		BTFileScan scanner = new BTFileScan();
		if (lo_key == null) {
			PageId current = headerPage.get_rootId();
			Page pge = new Page();
			SystemDefs.JavabaseBM.pinPage(current, pge, false);
			BTSortedPage p = new BTSortedPage(pge, headerPage.get_keyType());
			while (p.getType() != NodeType.LEAF) {
				p = new BTSortedPage((new BTIndexPage(p, NodeType.INDEX)).getLeftLink(),
						headerPage.get_keyType());
				SystemDefs.JavabaseBM.unpinPage(current, false);
				current = p.getCurPage();
			}
			startLeaf = new BTLeafPage(p, headerPage.get_keyType());
			scanner.currentRID = startLeaf.firstRecord();
			//SystemDefs.JavabaseBM.unpinPage(p.getCurPage(), false);
		} else {
			startPageId = search(lo_key);
			startLeaf = new BTLeafPage(startPageId, headerPage.get_keyType());
			RID newRID=new RID();
			KeyDataEntry teto=startLeaf.getFirst(newRID);
			while (BT.keyCompare(teto.key, lo_key)!=0)
			{
				teto=startLeaf.getNext(newRID);
			}
			scanner.currentRID = newRID;
			scanner.directed=true;
		}
		
		scanner.btree = this;
		scanner.endKey = hi_key;
		scanner.currentLeaf = startLeaf;
		scanner.finished=true;
		return scanner;
	}
}
