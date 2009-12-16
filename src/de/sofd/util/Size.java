package de.sofd.util;

public class Size
{
    protected int height;
    
    protected int width;

    public Size( int width, int height )
    {
        super();
        this.height = height;
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setHeight( int height )
    {
        this.height = height;
    }

    public void setWidth( int width )
    {
        this.width = width;
    }
}