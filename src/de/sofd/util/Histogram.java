package de.sofd.util;

import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

//TODO: Oliver: letzte Version einchecken?
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

    public Histogram( WritableRaster wr, IntRange range )
    {
        //TODO: Oliver
    }

    public Histogram( ShortBuffer sb, IntRange range )
    {
        //TODO: Oliver
    }

    public Histogram( IntBuffer ib, IntRange range )
    {
        //TODO: Oliver
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