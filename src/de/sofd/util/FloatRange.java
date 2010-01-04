package de.sofd.util;

public class FloatRange
{
    protected float min;
    
    protected float max;

    public FloatRange( float min, float max )
    {
        this.min = min;
        this.max = max;
    }
    
    public FloatRange()
    {
        this.min = 0;
        this.max = 1;
    }
    
    public float getDelta()
    {
        return ( max - min );
    }

    public float getMax()
    {
        return max;
    }

    public float getMin()
    {
        return min;
    }

    public void setMax( float max )
    {
        this.max = Math.max( Math.min( max, 1.0f ), 0.0f );
    }

    public void setMin( float min )
    {
        this.min = Math.max( Math.min( min, 1.0f ), 0.0f );
    }
    
    @Override
    public String toString()
    {
        return "[" + min + ", " + max + "]";
    }
}