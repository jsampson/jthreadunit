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

public class SynchronizedBlockTest extends TestCase
{
    private Object lock = new Object();

    public void testOneThread() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testOneThread");
        TestThread thread1 = new SynchronizedBlockTestThread(group, "thread1");
        thread1.start();

        thread1.performAction("enter");
        thread1.performAction("leave");

        thread1.kill();
    }

    public void testTwoThreads() throws Exception
    {
        ThreadGroup group = new ThreadGroup("testTwoThreads");
        TestThread thread1 = new SynchronizedBlockTestThread(group, "thread1");
        TestThread thread2 = new SynchronizedBlockTestThread(group, "thread2");
        thread1.start();
        thread2.start();

        thread1.performAction("enter");
        thread2.actionShouldBlock("enter");
        thread1.performAction("leave");
        thread2.completeBlockedAction();
        thread2.performAction("leave");

        thread1.kill();
        thread2.kill();
    }

    public class SynchronizedBlockTestThread extends TestThread
    {
        public SynchronizedBlockTestThread(ThreadGroup group, String name)
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
