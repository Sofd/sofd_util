package de.sofd.util;

import java.awt.image.Raster;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Histogram
{
    protected int[] frequencies;
    
    protected IntRange range;
    
    protected int size;
    
    public Histogram( int[] frequencies, IntRange range )
    {
        super();
        this.frequencies = frequencies;
        this.range = range;
    }

    public Histogram(ShortBuffer buffer, IntRange range) {
        init(buffer, range);
    }
    
    public Histogram(IntBuffer buffer, IntRange range) {
        init(buffer, range);
    }
    
    public Histogram(Raster raster, IntRange range) {
        this.range = range;
        this.size = raster.getDataBuffer().getSize();
        
        int freqs = range.getDelta() + 1;
        frequencies = new int[freqs];

        for (int i = 0; i < freqs; ++i)
            frequencies[i] = 0;

        int rangeMin = range.getMin();
        
        for (int x = 0; x < raster.getWidth(); ++x) {
            for (int y = 0; y < raster.getHeight(); ++y) {
                int value = raster.getSample(x, y, 0);
                int freqIndex = value - rangeMin;
    
                frequencies[freqIndex]++;
            }
        }
    }
    
    protected void init(Buffer buffer, IntRange range) {
        this.range = range;
        this.size = buffer.capacity();
        
        int freqs = range.getDelta() + 1;
        frequencies = new int[freqs];

        for (int i = 0; i < freqs; ++i)
            frequencies[i] = 0;

        int rangeMin = range.getMin();
        
        if (buffer instanceof ShortBuffer) {
            ShortBuffer sbuf = (ShortBuffer)buffer;
            for (int i = 0; i < size; ++i) {
                short value = sbuf.get(i);
                int freqIndex = value - rangeMin;
    
                frequencies[freqIndex]++;
            }
        } else if (buffer instanceof IntBuffer) {
            IntBuffer ibuf = (IntBuffer)buffer;
            for (int i = 0; i < size; ++i) {
                int value = ibuf.get(i);
                int freqIndex = value - rangeMin;
    
                frequencies[freqIndex]++;
            }
        }
    }
    
    public double getExpectedValue() {
        double ev = 0;
        
        for (int i=0; i<frequencies.length; ++i) {
            ev += (range.getMin() + i) * frequencies[i] * 1.0 / size;
        }
        
        return ev;
    }

    public int[] getFrequencies()
    {
        return frequencies;
    }
    
    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }
    
    /**
     * Returns the minimum value(index) for a relative sum value.
     * @param relSum Relative sum value, between 0.0 and 1.0.
     * @return Minimum histogram value which fulfilles the relative sum value.
     * @throws Exception If relSum is out of range.
     */
    public int getValueOfRelativeSum(float relSum) throws Exception {
        if (relSum < 0.0 || relSum > 1.0)
            throw new Exception("relSum out of range");
        
        if (relSum == 0.0) return range.getMin();
        
        double sum = 0;
        
        for (int i=0; i<frequencies.length; ++i) {
            sum += frequencies[i];
            
            if (sum/size >= relSum)
                return (range.getMin() + i);
                
        }
        
        return range.getMax();
    }
    
    public double getVariance() {
        double var = 0;
        
        double ev = getExpectedValue();
        
        for (int i=0; i<frequencies.length; ++i) {
            double tv = (ev - range.getMin() - i) * frequencies[i] * 1.0 / size;
            var += tv * tv;
        }
        
        return var;
    }

    public IntRange getRange()
    {
        return range;
    }

    public int getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return "Nr. of freqs : " + frequencies.length + ", size : " + size 
            + ", expected value : " + getExpectedValue() 
            + ", variance : " + getVariance()
            + ", standard deviation : " + getStandardDeviation();
    }
    
    
}