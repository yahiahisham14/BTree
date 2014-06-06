package btree;

import chainexception.ChainException;

public class ScanException extends ChainException{
	
	public ScanException(Exception e,String s)
	{
		super(e, s);
	}

}
