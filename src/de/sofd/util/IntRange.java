package de.sofd.util;

public class IntRange
{
    protected int min;
    
    protected int max;

    public IntRange( int min, int max )
    {
        this.min = min;
        this.max = max;
    }
    
    public int getDelta()
    {
        return (max - min);
    }

    public int getMax()
    {
        return max;
    }

    public int getMin()
    {
        return min;
    }

    public void setMax( int max )
    {
        this.max = max;
    }

    public void setMin( int min )
    {
        this.min = min;
    }
    
    @Override
    public String toString()
    {
        return "[" + min + ", " + max + "]";
    }
    
}