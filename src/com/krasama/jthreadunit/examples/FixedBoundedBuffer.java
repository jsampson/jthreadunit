package com.krasama.jthreadunit.examples;

import com.krasama.jthreadunit.TestThread;

public class FixedBoundedBuffer implements BoundedBuffer
{
    private Object[] buffer;
    private int putAt, takeAt, occupied;

    public FixedBoundedBuffer()
    {
        this(4);
    }

    public FixedBoundedBuffer(int capacity)
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
        notifyAll();
        --occupied;
        takeAt %= buffer.length;
        return buffer[takeAt++];
    }
}
