package btree;

import chainexception.ChainException;

public class CloseException extends ChainException{
	
	public CloseException(Exception e,String s)
	{
		super(e,s);
	}

}
