package net.sourceforge.jthreadunit.examples;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import net.sourceforge.jthreadunit.TestThread;

public class AssertionTest extends TestCase
{
    private Object lock = new Object();

    public void testAssertShouldNotBeBlocked() throws Exception
    {
        TestThread thread1 = new AssertionTestThread();
        TestThread thread2 = new AssertionTestThread();
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
                    "AssertionTestThread-2[" + thread2.getId()
                    + "] should not be blocked during \"enter\" "
                    + "<AssertionTestThread-1[" + thread1.getId()
                    + "]: Waiting on itself> "
                    + "<AssertionTestThread-2[" + thread2.getId()
                    + "]: Blocked on java.lang.Object@"
                    + Integer.toHexString(lock.hashCode())
                    + " held by AssertionTestThread-1[" + thread1.getId()
                    + "]>",
                    good.getMessage());
        }

        thread1.kill();
        thread2.kill();
    }

    public void testAssertShouldBeBlocked() throws Exception
    {
        TestThread thread = new AssertionTestThread();
        thread.start();

        try
        {
            thread.actionShouldBlock("enter");
            fail();
        }
        catch (AssertionFailedError good)
        {
            assertEquals(
                    "Action should be blocked",
                    good.getMessage());
        }

        thread.kill();
    }

    private ThreadGroup threadGroup = new ThreadGroup("AssertionTest");
    private int threadCount = 0;

    public class AssertionTestThread extends TestThread
    {
        public AssertionTestThread()
        {
            super(threadGroup,
                    "AssertionTestThread-" + (++threadCount));
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

