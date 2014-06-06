package btree;

import chainexception.ChainException;

public class DeleteRecException extends ChainException
{

    public DeleteRecException()
    {
    }

    public DeleteRecException(String s)
    {
        super(null, s);
    }

    public DeleteRecException(Exception exception, String s)
    {
        super(exception, s);
    }
}