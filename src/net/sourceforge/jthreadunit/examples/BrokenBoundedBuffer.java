package net.sourceforge.jthreadunit.examples;

import net.sourceforge.jthreadunit.TestThread;

public class BrokenBoundedBuffer
{
    private Object[] buffer;
    private int putAt, takeAt, occupied;

    public BrokenBoundedBuffer()
    {
        this(4);
    }

    public BrokenBoundedBuffer(int capacity)
    {
        buffer = new Object[capacity];
    }

    public synchronized void put(Object x) throws InterruptedException
    {
        while (occupied == buffer.length)
        {
            wait();
        }
        TestThread.checkpoint("putNotify");
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
        TestThread.checkpoint("takeNotify");
        notify();
        --occupied;
        takeAt %= buffer.length;
        return buffer[takeAt++];
    }
}
