package com.krasama.jthreadunit.examples;

import junit.framework.TestCase;

import com.krasama.jthreadunit.TestThread;

public class SynchronizedBlockTest extends TestCase
{
    private Object lock = new Object();

    public void testOneThread() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testOneThread");
        TestThread thread1 = new SynchronizedBlockTestThread(group, "thread1");
        thread1.start();

        thread1.performAction("enter");
        thread1.performAction("leave");

        thread1.kill();
    }

    public void testTwoThreads() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testTwoThreads");
        TestThread thread1 = new SynchronizedBlockTestThread(group, "thread1");
        TestThread thread2 = new SynchronizedBlockTestThread(group, "thread2");
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

    public class SynchronizedBlockTestThread extends TestThread
    {
        public SynchronizedBlockTestThread(ThreadGroup group, String name)
        {
            super(group, name);
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
