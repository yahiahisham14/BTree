package btree;

import global.*;
import heap.InvalidSlotNumberException;

import java.io.*;

import diskmgr.Page;

import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;

// Referenced classes of package btree:
//            ConvertException, IndexData, IntegerKey, KeyDataEntry, 
//            KeyNotMatchException, LeafData, NodeNotMatchException, NodeType, 
//            StringKey, KeyClass

public class BT implements GlobalConst {

	public static final int keyCompare(KeyClass keyclass, KeyClass keyclass1)
			throws KeyNotMatchException {
		if ((keyclass instanceof IntegerKey)
				&& (keyclass1 instanceof IntegerKey))
			return ((IntegerKey) keyclass).getKey().intValue()
					- ((IntegerKey) keyclass1).getKey().intValue();
		if ((keyclass instanceof StringKey) && (keyclass1 instanceof StringKey))
			return ((StringKey) keyclass).getKey().compareTo(
					((StringKey) keyclass1).getKey());
		else
			throw new KeyNotMatchException(null, "key types do not match");
	}

	public static final int getKeyLength(KeyClass keyclass)
			throws KeyNotMatchException, IOException {
		if (keyclass instanceof StringKey) {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			DataOutputStream dataoutputstream = new DataOutputStream(
					bytearrayoutputstream);
			dataoutputstream.writeUTF(((StringKey) keyclass).getKey());
			return dataoutputstream.size();
		}
		if (keyclass instanceof IntegerKey)
			return 4;
		else
			throw new KeyNotMatchException(null, "key types do not match");
	}

	public static final int getDataLength(short word0)
			throws NodeNotMatchException {
		if (word0 == 12)
			return 8;
		if (word0 == 11)
			return 4;
		else
			throw new NodeNotMatchException(null, "key types do not match");
	}

	public static final int getKeyDataLength(KeyClass keyclass, short word0)
			throws KeyNotMatchException, NodeNotMatchException, IOException {
		return getKeyLength(keyclass) + getDataLength(word0);
	}

	public static final KeyDataEntry getEntryFromBytes(byte abyte0[], int i,
			int j, int k, short word0) throws KeyNotMatchException,
			NodeNotMatchException, ConvertException {
		try {
			Object obj1;
			byte byte0;
			if (word0 == 11) {
				byte0 = 4;
				obj1 = new IndexData(Convert.getIntValue((i + j) - 4, abyte0));
			} else if (word0 == 12) {
				byte0 = 8;
				RID rid = new RID();
				rid.slotNo = Convert.getIntValue((i + j) - 8, abyte0);
				rid.pageNo = new PageId();
				rid.pageNo.pid = Convert.getIntValue((i + j) - 4, abyte0);
				obj1 = new LeafData(rid);
			} else {
				throw new NodeNotMatchException(null, "node types do not match");
			}
			Object obj;
			if (k == 1)
				obj = new IntegerKey(
						new Integer(Convert.getIntValue(i, abyte0)));
			else if (k == 0)
				obj = new StringKey(Convert.getStrValue(i, abyte0, j - byte0));
			else
				throw new KeyNotMatchException(null, "key types do not match");
			return new KeyDataEntry(((KeyClass) (obj)), ((DataClass) (obj1)));
		} catch (IOException ioexception) {
			throw new ConvertException(ioexception, "convert faile");
		}
	}

	public static final byte[] getBytesFromEntry(KeyDataEntry keydataentry)
			throws KeyNotMatchException, NodeNotMatchException,
			ConvertException {
		try {
			int i = getKeyLength(keydataentry.key);
			int j = i;
			if (keydataentry.data instanceof IndexData)
				i += 4;
			else if (keydataentry.data instanceof LeafData)
				i += 8;
			byte abyte0[] = new byte[i];
			if (keydataentry.key instanceof IntegerKey)
				Convert.setIntValue(((IntegerKey) keydataentry.key).getKey()
						.intValue(), 0, abyte0);
			else if (keydataentry.key instanceof StringKey)
				Convert.setStrValue(((StringKey) keydataentry.key).getKey(), 0,
						abyte0);
			else
				throw new KeyNotMatchException(null, "key types do not match");
			if (keydataentry.data instanceof IndexData)
				Convert.setIntValue(
						((IndexData) keydataentry.data).getData().pid, j,
						abyte0);
			else if (keydataentry.data instanceof LeafData) {
				Convert.setIntValue(
						((LeafData) keydataentry.data).getData().slotNo, j,
						abyte0);
				Convert.setIntValue(
						((LeafData) keydataentry.data).getData().pageNo.pid,
						j + 4, abyte0);
			} else {
				throw new NodeNotMatchException(null, "node types do not match");
			}
			return abyte0;
		} catch (IOException ioexception) {
			throw new ConvertException(ioexception, "convert failed");
		}
	}

	public BT() {
	}

	public static void printPage(PageId pageno, int keyType)
			throws IOException, IteratorException, ConstructPageException,
			HashEntryNotFoundException, ReplacerException,
			PageUnpinnedException, InvalidFrameNumberException,
			KeyNotMatchException, NodeNotMatchException, ConvertException,
			InvalidSlotNumberException {
		BTSortedPage sortedPage = new BTSortedPage(pageno, keyType);
		int i;
		i = 0;
		if (sortedPage.getType() == NodeType.INDEX) {
			BTIndexPage indexPage = new BTIndexPage((Page) sortedPage, keyType);
			System.out.println("");
			System.out.println("**************To Print an Index Page ********");
			System.out
					.println("Current Page ID: " + indexPage.getCurPage().pid);
			System.out.println("Left Link      : "
					+ indexPage.getLeftLink().pid);

			RID rid = new RID();

			for (KeyDataEntry entry = indexPage.getFirst(rid); entry != null; entry = indexPage
					.getNext(rid)) {
				if (keyType == AttrType.attrInteger)
					System.out.println(i + " (key, pageId):   ("
							+ (IntegerKey) entry.key + ",  "
							+ (IndexData) entry.data + " )");
				if (keyType == AttrType.attrString)
					System.out.println(i + " (key, pageId):   ("
							+ (StringKey) entry.key + ",  "
							+ (IndexData) entry.data + " )");

				i++;
			}

			System.out.println("************** END ********");
			System.out.println("");
		} else if (sortedPage.getType() == NodeType.LEAF) {
			BTLeafPage leafPage = new BTLeafPage((Page) sortedPage, keyType);
			System.out.println("");
			System.out.println("**************To Print an Leaf Page ********");
			System.out.println("Current Page ID: " + leafPage.getCurPage().pid);
			System.out
					.println("Left Link      : " + leafPage.getPrevPage().pid);
			System.out
					.println("Right Link     : " + leafPage.getNextPage().pid);

			RID rid = new RID();

			for (KeyDataEntry entry = leafPage.getFirst(rid); entry != null; entry = leafPage
					.getNext(rid)) {
				if (keyType == AttrType.attrInteger)
					System.out.println(i + " (key, [pageNo, slotNo]):   ("
							+ (IntegerKey) entry.key + ",  "
							+ (LeafData) entry.data + " )");
				if (keyType == AttrType.attrString)
					System.out.println(i + " (key, [pageNo, slotNo]):   ("
							+ (StringKey) entry.key + ",  "
							+ (LeafData) entry.data);

				i++;
			}

			System.out.println("************** END ********");
			System.out.println("");
		} else {
			System.out
					.println("Sorry!!! This page is neither Index nor Leaf page.");
		}

		SystemDefs.JavabaseBM.unpinPage(pageno, true/* dirty */);
	}

	public static void printAllLeafPages(BTreeHeaderPage header)
			throws IOException, ConstructPageException, IteratorException,
			HashEntryNotFoundException, InvalidFrameNumberException,
			PageUnpinnedException, ReplacerException, KeyNotMatchException,
			NodeNotMatchException, ConvertException, InvalidSlotNumberException {
		if (header.get_rootId().pid == INVALID_PAGE) {
			System.out.println("The Tree is Empty!!!");
			return;
		}

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out
				.println("---------------The B+ Tree Leaf Pages---------------");

		_printAllLeafPages(header.get_rootId(), header.get_keyType());

		System.out.println("");
		System.out.println("");
		System.out
				.println("------------- All Leaf Pages Have Been Printed --------");
		System.out.println("");
		System.out.println("");
	}

	private static void _printAllLeafPages(PageId currentPageId, int keyType)
			throws IOException, ConstructPageException, IteratorException,
			InvalidFrameNumberException, HashEntryNotFoundException,
			PageUnpinnedException, ReplacerException, KeyNotMatchException,
			NodeNotMatchException, ConvertException, InvalidSlotNumberException {

		BTSortedPage sortedPage = new BTSortedPage(currentPageId, keyType);

		if (sortedPage.getType() == NodeType.INDEX) {
			BTIndexPage indexPage = new BTIndexPage((Page) sortedPage, keyType);

			_printAllLeafPages(indexPage.getLeftLink(), keyType);

			RID rid = new RID();
			for (KeyDataEntry entry = indexPage.getFirst(rid); entry != null; entry = indexPage
					.getNext(rid)) {
				_printAllLeafPages(((IndexData) entry.data).getData(), keyType);
			}
		}

		if (sortedPage.getType() == NodeType.LEAF) {
//			for (PageId p=currentPageId;p.pid!=-1;p=sortedPage.getNextPage())
//			{
//				printPage(currentPageId, keyType);
//				sortedPage
//			}
//			
			
			printPage(currentPageId, keyType);
		}

		SystemDefs.JavabaseBM.unpinPage(currentPageId, true/* dirty */);
	}

	public static void printBTree(BTreeHeaderPage header) throws IOException,
			ConstructPageException, IteratorException,
			HashEntryNotFoundException, InvalidFrameNumberException,
			PageUnpinnedException, ReplacerException, KeyNotMatchException,
			NodeNotMatchException, ConvertException, InvalidSlotNumberException {
		if (header.get_rootId().pid == INVALID_PAGE) {
			System.out.println("The Tree is Empty!!!");
			return;
		}

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out
				.println("---------------The B+ Tree Structure---------------");

		System.out.println(1 + "     " + header.get_rootId());

		_printTree(header.get_rootId(), "     ", 1, header.get_keyType());

		System.out.println("--------------- End ---------------");
		System.out.println("");
		System.out.println("");
	}
	

	private static void _printTree(PageId currentPageId, String prefix, int i,
			int keyType) throws IOException, ConstructPageException,
			IteratorException, HashEntryNotFoundException,
			InvalidFrameNumberException, PageUnpinnedException,
			ReplacerException, KeyNotMatchException, NodeNotMatchException,
			ConvertException, InvalidSlotNumberException {

		BTSortedPage sortedPage = new BTSortedPage(currentPageId, keyType);
		prefix = prefix + "       ";
		i++;
		if (sortedPage.getType() == NodeType.INDEX) {
			BTIndexPage indexPage = new BTIndexPage((Page) sortedPage, keyType);

			System.out.println(i + prefix + indexPage.getPrevPage());
			_printTree(indexPage.getPrevPage(), prefix, i, keyType);

			RID rid = new RID();
			for (KeyDataEntry entry = indexPage.getFirst(rid); entry != null; entry = indexPage
					.getNext(rid)) {
				System.out.println(i + prefix + (IndexData) entry.data);
				_printTree(((IndexData) entry.data).getData(), prefix, i,
						keyType);
			}
		}
		SystemDefs.JavabaseBM.unpinPage(currentPageId, true/* dirty */);
	}
}