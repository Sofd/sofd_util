package de.sofd.util;

public class Bounds
{
    protected int maxX;
    protected int maxY;

    protected int minX;

    protected int minY;

    public Bounds( int minX, int minY, int maxX, int maxY )
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public int getMaxX()
    {
        return maxX;
    }

    public int getMaxY()
    {
        return maxY;
    }

    public int getMinX()
    {
        return minX;
    }

    public int getMinY()
    {
        return minY;
    }

    public void setMaxX( int maxX )
    {
        this.maxX = maxX;
    }
    
    public void setMaxY( int maxY )
    {
        this.maxY = maxY;
    }
    
    public void setMinX( int minX )
    {
        this.minX = minX;
    }
    
    public void setMinY( int minY )
    {
        this.minY = minY;
    }

    public void resize( int minX,
                        int minY,
                        int maxX,
                        int maxY )
    {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
    
}