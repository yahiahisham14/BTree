package btree;


// Referenced classes of package btree:
//            KeyClass

public class StringKey extends KeyClass
{

    public String toString()
    {
        return key;
    }

    public StringKey(String s)
    {
        key = new String(s);
    }

    public String getKey()
    {
        return new String(key);
    }

    public void setKey(String s)
    {
        key = new String(s);
    }

    private String key;
}