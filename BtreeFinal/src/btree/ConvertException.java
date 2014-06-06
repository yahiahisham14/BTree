package btree;

import chainexception.ChainException;

public class ConvertException extends ChainException
{

    public ConvertException()
    {
    }

    public ConvertException(String s)
    {
        super(null, s);
    }

    public ConvertException(Exception exception, String s)
    {
        super(exception, s);
    }
}