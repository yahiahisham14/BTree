package btree;

import chainexception.ChainException;

public class KeyNotMatchException extends ChainException
{

    public KeyNotMatchException()
    {
    }

    public KeyNotMatchException(String s)
    {
        super(null, s);
    }

    public KeyNotMatchException(Exception exception, String s)
    {
        super(exception, s);
    }
}