package net.sourceforge.jthreadunit;

import junit.framework.TestCase;

public class SemaphoreTest extends TestCase
{
    private Semaphore semaphore;
    private SemaphoreTestThread thread1;
    private SemaphoreTestThread thread2;
    private SemaphoreTestThread thread3;

    public void setUp(int initialState)
    {
        semaphore = new Semaphore(initialState);

        thread1 = new SemaphoreTestThread();
        thread2 = new SemaphoreTestThread();
        thread3 = new SemaphoreTestThread();

        thread1.start();
        thread2.start();
        thread3.start();
    }

    public void testOneThread()
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread1.performAction("up");
        assertEquals(1, semaphore.state());
    }

    public void testTwoThreads()
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread2.actionShouldBlock("down");
        thread1.performAction("up");
        thread2.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread2.performAction("up");
        assertEquals(1, semaphore.state());
    }

    public void testThreeThreadsTwoResources()
    {
        setUp(2);

        assertEquals(2, semaphore.state());

        thread1.performAction("down");
        assertEquals(1, semaphore.state());

        thread2.performAction("down");
        assertEquals(0, semaphore.state());

        thread3.actionShouldBlock("down");
        thread1.performAction("up");
        thread3.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread2.performAction("up");
        assertEquals(1, semaphore.state());

        thread3.performAction("up");
        assertEquals(2, semaphore.state());
    }

    public void testThreeThreadsOneResource()
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread2.actionShouldBlock("down");
        thread3.actionShouldBlock("down");
        thread1.performAction("up");

        thread2.completeBlockedAction();
        thread3.assertStillBlocked();
        assertEquals(0, semaphore.state());

        thread2.performAction("up");
        thread3.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread3.performAction("up");
        assertEquals(1, semaphore.state());
    }

    public void testInitialZeroDownFirst()
    {
        setUp(0);

        assertEquals(0, semaphore.state());

        thread1.actionShouldBlock("down");

        assertEquals(0, semaphore.state());

        thread2.performAction("up");
        thread1.completeBlockedAction();

        assertEquals(0, semaphore.state());
    }

    public void testInitialZeroUpFirst()
    {
        setUp(0);

        assertEquals(0, semaphore.state());

        thread1.performAction("up");

        assertEquals(1, semaphore.state());

        thread2.performAction("down");

        assertEquals(0, semaphore.state());
    }

    public void testInterrupt()
    {
        setUp(1);

        thread1.performAction("down");
        thread2.actionShouldBlock("down");
        thread2.interrupt();
        thread1.performAction("up");
        thread3.performAction("down");
        thread1.actionShouldBlock("down");
        thread2.actionShouldBlock("down");
        thread1.interrupt();
        thread3.performAction("up");
        thread2.completeBlockedAction();
    }

    public void testInvalidState()
    {
        try
        {
            new Semaphore(-1);
            fail("Should not allow negative initial state");
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    private class SemaphoreTestThread extends TestThread
    {
        public void doDown() throws InterruptedException
        {
            semaphore.down();
        }

        public void doUp()
        {
            semaphore.up();
        }
    }
}
