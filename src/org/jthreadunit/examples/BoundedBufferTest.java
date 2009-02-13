// Copyright 2004 by Justin T. Sampson
//
// This file is part of JThreadUnit.
//
// JThreadUnit is free software; you can redistribute it and/or modify it under
// the terms of the GNU Lesser General Public License as published by the Free
// Software Foundation; either version 2.1 of the License, or (at your option)
// any later version.
//
// JThreadUnit is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with JThreadUnit; if not, write to the Free Software Foundation, Inc.,
// 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package org.jthreadunit.examples;

import org.jthreadunit.TestThread;

import junit.framework.TestCase;


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
        ThreadGroup group = new ThreadGroup("testTwoThreads");
        TestThread thread1 = new BufferTestThread(group, "thread1", object);
        TestThread thread2 = new BufferTestThread(group, "thread2", object);
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
        ThreadGroup group = new ThreadGroup("testTakeTwoPutTwo");
        TestThread thread1 = new BufferTestThread(group, "thread1", object1);
        TestThread thread2 = new BufferTestThread(group, "thread2", object2);
        TestThread thread3 = new BufferTestThread(group, "thread3", object1);
        TestThread thread4 = new BufferTestThread(group, "thread4", object2);
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

        ThreadGroup group = new ThreadGroup("testPutSix");
        TestThread thread1 = new BufferTestThread(group, "thread1", object);
        TestThread thread2 = new BufferTestThread(group, "thread2", object);
        TestThread thread3 = new BufferTestThread(group, "thread3", object);

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

    public class BufferTestThread extends TestThread
    {
        private Object object;

        public BufferTestThread(ThreadGroup group, String name, Object object)
        {
            super(group, name);
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
