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


public class DeterminismTest extends TestCase
{
    private static final int THREAD_COUNT = 13;
    private static final int REPETITIONS = 10000;

    public static class CrazyThread extends Thread
    {
        ReadWriteLock lock = new ReadWriteLock();
        Throwable bad;
        int i;

        public CrazyThread(int i)
        {
            this.i = i;
        }

        public void run()
        {
            ThreadGroup group = new ThreadGroup("testCrazy-" + i);
            CrazyTestThread thread1 = new CrazyTestThread(group, "thread1");
            CrazyTestThread thread2 = new CrazyTestThread(group, "thread2");

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
            public CrazyTestThread(ThreadGroup group, String name)
            {
                super(group, name);
            }

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
            crazies[i] = new CrazyThread(i);
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
