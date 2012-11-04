package de.sofd.util;

public class ShortRange
{
    protected short min;
    
    protected short max;

    public ShortRange( short min, short max )
    {
        this.min = min;
        this.max = max;
    }
    
    public int getDelta()
    {
        return ( max - min );
    }

    public short getMax()
    {
        return max;
    }

    public short getMin()
    {
        return min;
    }

    public void setMax( short max )
    {
        this.max = max;
    }

    public void setMin( short min )
    {
        this.min = min;
    }
    
    @Override
    public String toString()
    {
        return "[" + min + ", " + max + "]";
    }
    
}