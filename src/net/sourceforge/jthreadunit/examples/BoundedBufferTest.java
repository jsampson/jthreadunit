package net.sourceforge.jthreadunit.examples;

import junit.framework.TestCase;

import net.sourceforge.jthreadunit.TestThread;

public abstract class BoundedBufferTest extends TestCase
{
    protected BoundedBuffer buffer = createBoundedBuffer();

    protected abstract BoundedBuffer createBoundedBuffer();

    public void testPutTake() throws Exception
    {
        Object one = new Object();
        buffer.put(one);
        assertSame(one, buffer.take());
    }

    public void testPutTwoTakeTwo() throws Exception
    {
        Object one = new Object();
        Object two = new Object();
        buffer.put(one);
        buffer.put(two);
        assertSame(one, buffer.take());
        assertSame(two, buffer.take());
    }

    public void testTwoThreads() throws Exception
    {
        Object object = new Object();
        TestThread thread1 = new BufferTestThread(object);
        TestThread thread2 = new BufferTestThread(object);
        thread1.start();
        thread2.start();
        thread1.actionShouldBlock("take");
        thread2.performActions("put", "putNotify");
        thread1.completeBlockedActionWithActions("takeNotify");
        thread1.kill();
        thread2.kill();
    }

    public void testTakeTwoPutTwo() throws Exception
    {
        Object object1 = new Object();
        Object object2 = new Object();
        TestThread thread1 = new BufferTestThread(object1);
        TestThread thread2 = new BufferTestThread(object2);
        TestThread thread3 = new BufferTestThread(object1);
        TestThread thread4 = new BufferTestThread(object2);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread1.actionShouldBlock("take");
        thread2.actionShouldBlock("take");
        thread3.performActions("put", "putNotify");
        thread1.completeBlockedActionWithActions("takeNotify");
        thread4.performActions("put", "putNotify");
        thread2.completeBlockedActionWithActions("takeNotify");
        thread1.kill();
        thread2.kill();
        thread3.kill();
        thread4.kill();
    }

    public void testPutSix() throws Exception
    {
        Object object = new Object();

        TestThread thread1 = new BufferTestThread(object);
        TestThread thread2 = new BufferTestThread(object);
        TestThread thread3 = new BufferTestThread(object);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.performActions("put", "putNotify");
        thread1.performActions("put", "putNotify");
        thread1.performActions("put", "putNotify");
        thread1.performActions("put", "putNotify");
        thread1.actionShouldBlock("put");
        thread2.actionShouldBlock("put");

        thread3.performActions("take", "takeNotify");
        thread1.completeBlockedActionWithActions("putNotify");

        thread3.performActions("take", "takeNotify");
        thread2.completeBlockedActionWithActions("putNotify");

        thread1.kill();
        thread2.kill();
        thread3.kill();
    }

    private ThreadGroup threadGroup = new ThreadGroup("BoundedBufferTest");
    private int threadCount = 0;

    public class BufferTestThread extends TestThread
    {
        private Object object;

        public BufferTestThread(Object object)
        {
            super(threadGroup, "BufferTestThread-" + (++threadCount));
            this.object = object;
        }

        public void doPut() throws InterruptedException
        {
            buffer.put(object);
        }

        public void doTake() throws InterruptedException
        {
            assertSame(object, buffer.take());
        }
    }
}
