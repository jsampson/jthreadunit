package net.sourceforge.jthreadunit.examples;

import junit.framework.TestCase;

import net.sourceforge.jthreadunit.TestThread;

public class DeterminismTest extends TestCase
{
    private static final int THREAD_COUNT = 13;
    private static final int REPETITIONS = 10000;

    public static class CrazyThread extends Thread
    {
        ReadWriteLock lock = new ReadWriteLock();
        Throwable bad;

        public void run()
        {
            CrazyTestThread thread1 = new CrazyTestThread();
            CrazyTestThread thread2 = new CrazyTestThread();

            thread1.start();
            thread2.start();

            try
            {
                for (int i = 0; i < REPETITIONS; i++)
                {
                    thread1.performAction("acquireWrite");
                    thread2.actionShouldBlock("acquireWrite");
                    thread1.performAction("releaseWrite");
                    thread2.completeBlockedAction();
                    thread2.performAction("releaseWrite");
                }
            }
            catch (Error error)
            {
                bad = error;
                throw error;
            }
            catch (Exception exception)
            {
                bad = exception;
            }
            finally
            {
                thread1.kill();
                thread2.kill();
            }
        }

        public class CrazyTestThread extends TestThread
        {
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

    public void testCrazy() throws Exception
    {
        CrazyThread crazies[] = new CrazyThread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++)
        {
            crazies[i] = new CrazyThread();
        }

        for (CrazyThread crazy : crazies)
        {
            crazy.start();
        }

        for (CrazyThread crazy : crazies)
        {
            crazy.join();
        }

        for (CrazyThread crazy : crazies)
        {
            if (crazy.bad != null)
            {
                fail(crazy.bad.getMessage());
            }
        }
    }
}
