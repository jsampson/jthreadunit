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
import junit.framework.AssertionFailedError;

import com.krasama.jthreadunit.TestThread;

public class AssertionTest extends TestCase
{
    private Object lock = new Object();

    public void testAssertShouldNotBeBlocked() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testAssertShouldNotBeBlocked");
        TestThread thread1 = new AssertionTestThread(group, "thread1");
        TestThread thread2 = new AssertionTestThread(group, "thread2");
        thread1.start();
        thread2.start();

        thread1.performAction("enter");

        try
        {
            thread2.performAction("enter");
            fail();
        }
        catch (AssertionFailedError good)
        {
            assertEquals(
                    "thread2[" + thread2.getId()
                    + "] should not be blocked during \"enter\" "
                    + "<thread1[" + thread1.getId() + "]: Waiting on itself> "
                    + "<thread2[" + thread2.getId()
                    + "]: Blocked on java.lang.Object@"
                    + Integer.toHexString(lock.hashCode())
                    + " held by thread1[" + thread1.getId() + "]>",
                    good.getMessage());
        }

        thread1.kill();
        thread2.kill();
    }

    public void testAssertShouldBeBlocked() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testAssertShouldBeBlocked");
        TestThread thread = new AssertionTestThread(group, "thread");
        thread.start();

        try
        {
            thread.actionShouldBlock("enter");
            fail();
        }
        catch (AssertionFailedError good)
        {
            assertEquals(
                    "thread[" + thread.getId()
                    + "] should be blocked during \"enter\"",
                    good.getMessage());
        }

        thread.kill();
    }

    public class AssertionTestThread extends TestThread
    {
        public AssertionTestThread(ThreadGroup group, String name)
        {
            super(group, name);
        }

        public void doEnter() throws InterruptedException
        {
            synchronized (lock)
            {
                expectAction("leave");
            }
        }
    }
}

