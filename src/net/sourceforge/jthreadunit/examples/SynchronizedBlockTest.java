package net.sourceforge.jthreadunit.examples;

import junit.framework.TestCase;

import net.sourceforge.jthreadunit.TestThread;

public class SynchronizedBlockTest extends TestCase
{
    private Object lock = new Object();

    public void testOneThread() throws Exception
    {
        TestThread thread1 = new SynchronizedBlockTestThread();
        thread1.start();

        thread1.performAction("enter");
        thread1.performAction("leave");

        thread1.kill();
    }

    public void testTwoThreads() throws Exception
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

        thread1.kill();
        thread2.kill();
    }

    private ThreadGroup threadGroup = new ThreadGroup("SynchronizedBlockTest");
    private int threadCount = 0;

    public class SynchronizedBlockTestThread extends TestThread
    {
        public SynchronizedBlockTestThread()
        {
            super(threadGroup,
                    "SynchronizedBlockTestThread-" + (++threadCount));
        }

        public void doEnter() throws InterruptedException
        {
            synchronized (lock)
            {
                expectAction("leave");
            }
        }
    }
}
