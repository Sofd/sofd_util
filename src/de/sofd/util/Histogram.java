package de.sofd.util;

public class Histogram
{
    protected int[] frequencies;
    
    protected ShortRange range;

    public Histogram( int[] frequencies, ShortRange range )
    {
        super();
        this.frequencies = frequencies;
        this.range = range;
    }

    public int[] getFrequencies()
    {
        return frequencies;
    }

    public void setFrequencies( int[] frequencies )
    {
        this.frequencies = frequencies;
    }

    public ShortRange getRange()
    {
        return range;
    }

    public void setRange( ShortRange range )
    {
        this.range = range;
    }
    
    
}