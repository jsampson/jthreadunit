package net.sourceforge.jthreadunit;

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
        if (currentState == 0)
        {
            wait();
        }

        currentState--;
    }

    public synchronized void up()
    {
        currentState++;
        notify();
    }

    public synchronized int state()
    {
        return currentState;
    }
}
