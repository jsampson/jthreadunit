package net.sourceforge.jthreadunit.examples;

import junit.framework.TestCase;

import net.sourceforge.jthreadunit.TestThread;

public class BrokenBoundedBufferTest extends TestCase
{
    private BrokenBoundedBuffer buffer = new BrokenBoundedBuffer();

    public void testPutTake() throws InterruptedException
    {
        Object one = new Object();
        buffer.put(one);
        assertSame(one, buffer.take());
    }

    public void testPutTwoTakeTwo() throws InterruptedException
    {
        Object one = new Object();
        Object two = new Object();
        buffer.put(one);
        buffer.put(two);
        assertSame(one, buffer.take());
        assertSame(two, buffer.take());
    }

    public void testTwoThreads()
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

    public void testTakeTwoPutTwo()
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

    public void testPutSix()
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

    public void testTheBug()
    {
        buffer = new BrokenBoundedBuffer(1);

        Object object = new Object();

        TestThread consumer1 = new BufferTestThread(object);
        TestThread consumer2 = new BufferTestThread(object);
        TestThread producer1 = new BufferTestThread(object);
        TestThread producer2 = new BufferTestThread(object);

        consumer1.start();
        consumer2.start();
        producer1.start();
        producer2.start();

        consumer1.actionShouldBlock("take");
        consumer2.actionShouldBlock("take");

        producer1.performAction("put");
        producer2.actionShouldBlock("put");
        producer1.performAction("putNotify");

        consumer2.assertStillBlocked();
        producer2.assertStillBlocked();

        consumer1.completeBlockedActionWithActions("takeNotify");

        consumer2.assertStillBlocked();
        producer2.assertStillBlocked();

        consumer1.kill();
        consumer2.kill();
        producer1.kill();
        producer2.kill();
    }

    public class BufferTestThread extends TestThread
    {
        private Object object;

        public BufferTestThread(Object object)
        {
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
