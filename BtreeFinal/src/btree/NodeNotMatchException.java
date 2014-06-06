package btree;

import chainexception.ChainException;

public class NodeNotMatchException extends ChainException
{

    public NodeNotMatchException()
    {
    }

    public NodeNotMatchException(String s)
    {
        super(null, s);
    }

    public NodeNotMatchException(Exception exception, String s)
    {
        super(exception, s);
    }
}