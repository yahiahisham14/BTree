package btree;

import chainexception.ChainException;

public class ConstructPageException extends ChainException
{

    public ConstructPageException()
    {
    }

    public ConstructPageException(String s)
    {
        super(null, s);
    }

    public ConstructPageException(Exception exception, String s)
    {
        super(exception, s);
    }
}