package com.krasama.jthreadunit.examples;

import com.krasama.jthreadunit.TestThread;

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

        ThreadGroup group = new ThreadGroup("testTheBug-broken");
        TestThread consumer1 = new BufferTestThread(group, "consumer1", object);
        TestThread consumer2 = new BufferTestThread(group, "consumer2", object);
        TestThread producer1 = new BufferTestThread(group, "producer1", object);
        TestThread producer2 = new BufferTestThread(group, "producer2", object);

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
