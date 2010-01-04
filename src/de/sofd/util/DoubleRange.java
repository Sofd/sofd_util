package de.sofd.util;

public class DoubleRange
{
    protected double min;
    
    protected double max;

    public DoubleRange( double min, double max )
    {
        this.min = min;
        this.max = max;
    }
    
    public DoubleRange()
    {
        this.min = 0;
        this.max = 1;
    }
    
    public double getDelta()
    {
        return ( max - min );
    }

    public double getMax()
    {
        return max;
    }

    public double getMin()
    {
        return min;
    }

    public void setMax( double max )
    {
        this.max = max;
    }

    public void setMin( double min )
    {
        this.min = min;
    }
    
    @Override
    public String toString()
    {
        return "[" + min + ", " + max + "]";
    }
    
}