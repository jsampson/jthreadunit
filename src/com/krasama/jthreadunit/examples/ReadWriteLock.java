package com.krasama.jthreadunit.examples;

public class ReadWriteLock
{
    private int readerCount;
    private boolean writeLocked;
    private int waitingWriters;

    public synchronized void acquireRead() throws InterruptedException
    {
        while (writeLocked || waitingWriters > 0)
        {
            wait();
        }

        readerCount++;
    }

    public synchronized void releaseRead()
    {
        readerCount--;
        notifyAll();
    }

    public synchronized void acquireWrite() throws InterruptedException
    {
        waitingWriters++;

        try
        {
            while (writeLocked || readerCount > 0)
            {
                wait();
            }
        }
        catch (InterruptedException interrupted)
        {
            notifyAll();
            throw interrupted;
        }
        finally
        {
            waitingWriters--;
        }

        writeLocked = true;
    }

    public synchronized void releaseWrite()
    {
        writeLocked = false;
        notifyAll();
    }
}
