package net.sourceforge.jthreadunit;

import junit.framework.TestCase;

public class SynchronizedBlockTest extends TestCase
{
    private Object lock = new Object();

    public void testOneThread()
    {
        TestThread thread1 = new SynchronizedBlockTestThread();
        thread1.start();

        thread1.performAction("enter");
        thread1.performAction("leave");
    }

    public void testTwoThreads()
    {
        TestThread thread1 = new SynchronizedBlockTestThread();
        TestThread thread2 = new SynchronizedBlockTestThread();
        thread1.start();
        thread2.start();

        thread1.performAction("enter");
        thread2.actionShouldBlock("enter");
        thread1.performAction("leave");
        thread2.completeBlockedAction();
        thread2.performAction("leave");
    }

    private class SynchronizedBlockTestThread extends TestThread
    {
        public void doEnter() throws InterruptedException
        {
            synchronized (lock)
            {
                expectAction("leave");
            }
        }
    }
}
