package net.sourceforge.jthreadunit;

public class Semaphore
{
    private Object lock;
    private int currentState;

    public Semaphore(int initialState)
    {
        if (initialState < 0)
        {
            throw new IllegalArgumentException(
                    "Semaphore initial state must not be negative");
        }

        this.lock = new Object();
        this.currentState = initialState;
    }

    public Semaphore()
    {
        this(1);
    }

    public void down() throws InterruptedException
    {
        synchronized (lock)
        {
            if (currentState == 0)
            {
                lock.wait();
            }

            currentState--;
        }
    }

    public void up()
    {
        synchronized (lock)
        {
            currentState++;
            lock.notify();
        }
    }

    public int state()
    {
        synchronized (lock)
        {
            return currentState;
        }
    }
}
