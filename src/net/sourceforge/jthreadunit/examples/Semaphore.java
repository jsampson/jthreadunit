package net.sourceforge.jthreadunit.examples;

import net.sourceforge.jthreadunit.TestThread;

public class Semaphore
{
    private int currentState;

    public Semaphore(int initialState)
    {
        if (initialState < 0)
        {
            throw new IllegalArgumentException(
                    "Semaphore initial state must not be negative");
        }

        this.currentState = initialState;
    }

    public synchronized void down() throws InterruptedException
    {
        while (currentState == 0)
        {
            wait();
        }

        currentState--;
    }

    public synchronized void up() throws InterruptedException
    {
        currentState++;
        TestThread.checkpoint("notify");
        notify();
    }

    public synchronized int state()
    {
        return currentState;
    }
}
