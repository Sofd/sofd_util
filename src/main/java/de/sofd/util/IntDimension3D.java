package de.sofd.util;

public class IntDimension3D extends Size
{
    protected int depth;
    
    public IntDimension3D( int width, int height, int depth )
    {
        super( width, height );
        this.depth = depth;
    }

    public int getDepth()
    {
        return depth;
    }

    public int getMax()
    {
        return Math.max( Math.max( width, height ), depth );
    }

    public int getMin()
    {
        return Math.min( Math.min( width, height ), depth );
    }

    public void setDepth( int depth )
    {
        this.depth = depth;
    }

    @Override
    public String toString()
    {
        return "[" + width + ", " + height + ", " + depth + "]";
    }

}