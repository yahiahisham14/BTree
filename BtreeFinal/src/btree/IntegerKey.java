package btree;


// Referenced classes of package btree:
//            KeyClass

public class IntegerKey extends KeyClass
{

    public String toString()
    {
        return key.toString();
    }

    public IntegerKey(Integer integer)
    {
        key = new Integer(integer.intValue());
    }

    public IntegerKey(int i)
    {
        key = new Integer(i);
    }

    public Integer getKey()
    {
        return new Integer(key.intValue());
    }

    public void setKey(Integer integer)
    {
        key = new Integer(integer.intValue());
    }

    private Integer key;
}