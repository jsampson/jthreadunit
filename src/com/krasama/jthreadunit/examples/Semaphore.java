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

import com.krasama.jthreadunit.TestThread;

public class Semaphore
{
    private int currentState;

    public Semaphore(int initialState)
    {
        if (initialState < 0)
        {
            throw new IllegalArgumentException(
                    "Semaphore initial state must not be negative");
        }

        this.currentState = initialState;
    }

    public synchronized void down() throws InterruptedException
    {
        while (currentState == 0)
        {
            wait();
        }

        currentState--;
    }

    public synchronized void up() throws InterruptedException
    {
        currentState++;
        TestThread.checkpoint("notify");
        notify();
    }

    public synchronized int state()
    {
        return currentState;
    }
}
