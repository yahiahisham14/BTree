package btree;

import chainexception.ChainException;

public class InsertRecException extends ChainException
{

    public InsertRecException()
    {
    }

    public InsertRecException(String s)
    {
        super(null, s);
    }

    public InsertRecException(Exception exception, String s)
    {
        super(exception, s);
    }
}