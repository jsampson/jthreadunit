package net.sourceforge.jthreadunit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class ReadWriteLockTest extends TestCase
{
    private ReadWriteLock lock;
    private ReadWriteLockTestThread thread1;
    private ReadWriteLockTestThread thread2;
    private ReadWriteLockTestThread thread3;
    private ReadWriteLockTestThread thread4;

    public void setUp()
    {
        lock = new ReadWriteLock();

        thread1 = new ReadWriteLockTestThread();
        thread2 = new ReadWriteLockTestThread();
        thread3 = new ReadWriteLockTestThread();
        thread4 = new ReadWriteLockTestThread();

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    public void testOneReader() throws Exception
    {
        thread1.performAction("acquireRead");
        thread1.performAction("releaseRead");
    }

    public void testOneWriter() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread1.performAction("releaseWrite");
    }

    public void testTwoWriters() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
    }

    public void testOneReaderOneWriter() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseRead");
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
    }

    public void testOneWriterOneReader() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireRead");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread2.performAction("releaseRead");
    }

    public void testTwoReaders() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.performAction("acquireRead");
        thread1.performAction("releaseRead");
        thread2.performAction("releaseRead");
    }

    public void testTwoReadersOneWriter() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.performAction("acquireRead");
        thread3.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseRead");
        thread3.assertStillBlocked();
        thread2.performAction("releaseRead");
        thread3.completeBlockedAction();
        thread3.performAction("releaseWrite");
    }

    public void testOneWriterTwoReaders() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireRead");
        thread3.actionShouldBlock("acquireRead");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread3.completeBlockedAction();
        thread2.performAction("releaseRead");
        thread3.performAction("releaseRead");
    }

    public void testFairToWriters() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread1.performAction("releaseRead");
        thread3.assertStillBlocked();
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
        thread3.completeBlockedAction();
        thread3.performAction("releaseRead");
    }

    public void testWriterInterrupted() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread2.interrupt();
        thread1.performAction("releaseRead");
        thread3.performAction("acquireRead");
        thread3.performAction("releaseRead");
    }

    public void testWriterInterruptedNotifiesReaders() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread4.actionShouldBlock("acquireRead");
        thread2.interrupt();
        thread3.completeBlockedAction();
        thread4.completeBlockedAction();
        thread1.performAction("releaseRead");
        thread3.performAction("releaseRead");
        thread4.performAction("releaseRead");
    }

    public void testReallyFunky() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread4.actionShouldBlock("acquireWrite");
        thread2.interrupt();
        thread1.performAction("releaseRead");
        thread4.completeBlockedAction();
        thread4.performAction("releaseWrite");
        thread3.completeBlockedAction();
        thread3.performAction("releaseRead");
    }

    private class ReadWriteLockTestThread extends TestThread
    {
        public void doAcquireRead() throws InterruptedException
        {
            lock.acquireRead();
        }

        public void doReleaseRead()
        {
            lock.releaseRead();
        }

        public void doAcquireWrite() throws InterruptedException
        {
            lock.acquireWrite();
        }

        public void doReleaseWrite()
        {
            lock.releaseWrite();
        }
    }
}
