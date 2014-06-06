package btree;


/**
 * Base class for a index file scan
 */
public abstract class IndexFileScan 
{
  /**
   * Get the next record.
   * @return the KeyDataEntry, which contains the key and data
 * @throws ScanException 
   */
  abstract public KeyDataEntry get_next() throws ScanException;

  /** 
   * Delete the current record.
 * @throws ScanException 
   */
   abstract public void delete_current() throws ScanException;

  /**
   * Returns the size of the key
   * @return the keysize
   */
  abstract public int keysize();
}
