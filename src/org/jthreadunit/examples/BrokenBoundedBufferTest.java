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
