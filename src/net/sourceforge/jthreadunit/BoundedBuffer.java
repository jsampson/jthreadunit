package net.sourceforge.jthreadunit;

public class BoundedBuffer
{
    private Object[] buffer;
    private int putAt, takeAt, occupied;

    public BoundedBuffer()
    {
        this(4);
    }

    public BoundedBuffer(int capacity)
    {
        buffer = new Object[capacity];
    }

    public synchronized void put(Object x) throws InterruptedException
    {
        while (occupied == buffer.length)
        {
            wait();
        }
        notify();
        ++occupied;
        putAt %= buffer.length;
        buffer[putAt++] = x;
    }

    public synchronized Object take() throws InterruptedException
    {
        while (occupied == 0)
        {
            wait();
        }
        notify();
        --occupied;
        takeAt %= buffer.length;
        return buffer[takeAt++];
    }
}
