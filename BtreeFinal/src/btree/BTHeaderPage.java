package btree;


	
	import java.io.*;
    import diskmgr.*;
	import bufmgr.*;
	import global.*;
	import heap.*;
	
	  /**
18	   * Intefrace of a B+ tree index header page. 
19	   * Here we use a HFPage as head page of the file
20	   * Inside the headpage, Logicaly, there are only seven
21	   * elements inside the head page, they are
22	   * magic0, rootId, keyType, maxKeySize, deleteFashion,
23	   * and type(=NodeType.BTHEAD)
24	   */
	class BTreeHeaderPage extends HFPage {
	 
	  void setPageId(PageId pageno)
	    throws IOException
	    {
	      setCurPage(pageno);
	    }
	 
	  PageId getPageId()
	    throws IOException
	    {
	      return getCurPage();
	    }
	 
	 
	  /** set the rootId
58	   */
	  void  set_rootId( PageId rootID )
	    throws IOException
	    {
	      setNextPage(rootID);
	    };
	 
	  /** get the rootId
66	   */
	  PageId get_rootId()
	    throws IOException
	    {
      return getNextPage();
	    }
	 
	  /** set the key type
	   */ 
	  void set_keyType( short key_type )
	    throws IOException
	    {
	      setSlot(3, (int)key_type, 0);
	    }

	 
	  /** get the key type
82	   */
	  short get_keyType()
	    throws IOException
	    {
	      return   (short)getSlotLength(3);
	    }
	 
	  /** get the max keysize
90	   */
	  void set_maxKeySize(int key_size )
	    throws IOException
	    {
	      setSlot(1, key_size, 0);
	    }
	 
	  /** set the max keysize
98	   */
	  int get_maxKeySize()
	    throws IOException
	    {
	      return getSlotLength(1);
	    }
	 
	 
	 
	  /** pin the page with pageno, and get the corresponding SortedPage
124	   */
	  public BTreeHeaderPage(PageId pageno)
	    throws ConstructPageException
	    {
	      super();
	      try {
	       
	        SystemDefs.JavabaseBM.pinPage(pageno, this, false/*Rdisk*/);
	      }
	      catch (Exception e) {
	        throw new ConstructPageException(e, "pinpage failed");
	      }
	    }
	 
	  /**associate the SortedPage instance with the Page instance */
	  public BTreeHeaderPage(Page page) {
	   
	    super(page);
	  } 
	 
	 
	  /**new a page, and associate the SortedPage instance with the Page instance
146	   */
	  public BTreeHeaderPage( )
	    throws ConstructPageException
	    {
	      super();
	      try{
	        Page apage=new Page();
	        PageId pageId=SystemDefs.JavabaseBM.newPage(apage,1);
	        if (pageId==null)
	          throw new ConstructPageException(null, "new page failed");
	        this.init(pageId, apage);
	       
	      }
	      catch (Exception e) {
	        throw new ConstructPageException(e, "construct header page failed");
	      }
	    } 
	 
	} // end of BTreeHeaderPage
