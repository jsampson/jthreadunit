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

package com.krasama.jthreadunit.examples;

import junit.framework.TestCase;

import com.krasama.jthreadunit.TestThread;

public class SemaphoreTest extends TestCase
{
    private Semaphore semaphore;
    private SemaphoreTestThread thread1;
    private SemaphoreTestThread thread2;
    private SemaphoreTestThread thread3;

    public void setUp(int initialState)
    {
        semaphore = new Semaphore(initialState);

        ThreadGroup group = new ThreadGroup(getName());
        thread1 = new SemaphoreTestThread(group, "thread1");
        thread2 = new SemaphoreTestThread(group, "thread2");
        thread3 = new SemaphoreTestThread(group, "thread3");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    public void tearDown()
    {
        if (semaphore != null)
        {
            thread1.kill();
            thread2.kill();
            thread3.kill();
        }
    }

    public void testOneThread() throws Exception
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread1.performActions("up", "notify");
        assertEquals(1, semaphore.state());
    }

    public void testTwoThreads() throws Exception
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread2.actionShouldBlock("down");
        thread1.performActions("up", "notify");
        thread2.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread2.performActions("up", "notify");
        assertEquals(1, semaphore.state());
    }

    public void testThreeThreadsTwoResources() throws Exception
    {
        setUp(2);

        assertEquals(2, semaphore.state());

        thread1.performAction("down");
        assertEquals(1, semaphore.state());

        thread2.performAction("down");
        assertEquals(0, semaphore.state());

        thread3.actionShouldBlock("down");
        thread1.performActions("up", "notify");
        thread3.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread2.performActions("up", "notify");
        assertEquals(1, semaphore.state());

        thread3.performActions("up", "notify");
        assertEquals(2, semaphore.state());
    }

    public void testThreeThreadsOneResource() throws Exception
    {
        setUp(1);

        assertEquals(1, semaphore.state());

        thread1.performAction("down");
        assertEquals(0, semaphore.state());

        thread2.actionShouldBlock("down");
        thread3.actionShouldBlock("down");
        thread1.performActions("up", "notify");

        thread2.completeBlockedAction();
        thread3.assertStillBlocked();
        assertEquals(0, semaphore.state());

        thread2.performActions("up", "notify");
        thread3.completeBlockedAction();
        assertEquals(0, semaphore.state());

        thread3.performActions("up", "notify");
        assertEquals(1, semaphore.state());
    }

    public void testInitialZeroDownFirst() throws Exception
    {
        setUp(0);

        assertEquals(0, semaphore.state());

        thread1.actionShouldBlock("down");

        assertEquals(0, semaphore.state());

        thread2.performActions("up", "notify");
        thread1.completeBlockedAction();

        assertEquals(0, semaphore.state());
    }

    public void testInitialZeroUpFirst() throws Exception
    {
        setUp(0);

        assertEquals(0, semaphore.state());

        thread1.performActions("up", "notify");

        assertEquals(1, semaphore.state());

        thread2.performAction("down");

        assertEquals(0, semaphore.state());
    }

    public void testInterrupt() throws Exception
    {
        setUp(1);

        thread1.performAction("down");
        thread2.actionShouldBlock("down");
        thread2.interrupt();
        thread1.performActions("up", "notify");
        thread3.performAction("down");
        thread1.actionShouldBlock("down");
        thread2.actionShouldBlock("down");
        thread1.interrupt();
        thread3.performActions("up", "notify");
        thread2.completeBlockedAction();
    }

    public void testRaceRequiresWhile() throws Exception
    {
        setUp(0);

        thread1.actionShouldBlock("down");
        thread2.performAction("up");
        thread3.actionShouldBlock("down");
        thread2.performAction("notify");
        thread3.completeBlockedAction();
        thread1.assertStillBlocked();

        assertEquals(0, semaphore.state());
    }

    public void testInvalidState() throws Exception
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

    public class SemaphoreTestThread extends TestThread
    {
        public SemaphoreTestThread(ThreadGroup group, String name)
        {
            super(group, name);
        }

        public void doDown() throws InterruptedException
        {
            semaphore.down();
        }

        public void doUp() throws InterruptedException
        {
            semaphore.up();
        }
    }
}
