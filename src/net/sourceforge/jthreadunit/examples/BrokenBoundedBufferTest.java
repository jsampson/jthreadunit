package net.sourceforge.jthreadunit.examples;

import net.sourceforge.jthreadunit.TestThread;

public class BrokenBoundedBufferTest extends BoundedBufferTest
{
    protected BoundedBuffer createBoundedBuffer()
    {
        return new BrokenBoundedBuffer();
    }

    public void testTheBug() throws Exception
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
}
