package com.krasama.jthreadunit.examples;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import com.krasama.jthreadunit.TestThread;

public class AssertionTest extends TestCase
{
    private Object lock = new Object();

    public void testAssertShouldNotBeBlocked() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testAssertShouldNotBeBlocked");
        TestThread thread1 = new AssertionTestThread(group, "thread1");
        TestThread thread2 = new AssertionTestThread(group, "thread2");
        thread1.start();
        thread2.start();

        thread1.performAction("enter");

        try
        {
            thread2.performAction("enter");
            fail();
        }
        catch (AssertionFailedError good)
        {
            assertEquals(
                    "thread2[" + thread2.getId()
                    + "] should not be blocked during \"enter\" "
                    + "<thread1[" + thread1.getId() + "]: Waiting on itself> "
                    + "<thread2[" + thread2.getId()
                    + "]: Blocked on java.lang.Object@"
                    + Integer.toHexString(lock.hashCode())
                    + " held by thread1[" + thread1.getId() + "]>",
                    good.getMessage());
        }

        thread1.kill();
        thread2.kill();
    }

    public void testAssertShouldBeBlocked() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testAssertShouldBeBlocked");
        TestThread thread = new AssertionTestThread(group, "thread");
        thread.start();

        try
        {
            thread.actionShouldBlock("enter");
            fail();
        }
        catch (AssertionFailedError good)
        {
            assertEquals(
                    "thread[" + thread.getId()
                    + "] should be blocked during \"enter\"",
                    good.getMessage());
        }

        thread.kill();
    }

    public class AssertionTestThread extends TestThread
    {
        public AssertionTestThread(ThreadGroup group, String name)
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

