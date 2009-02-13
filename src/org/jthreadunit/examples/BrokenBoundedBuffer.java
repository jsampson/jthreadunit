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

public class BrokenBoundedBuffer implements BoundedBuffer
{
    private Object[] buffer;
    private int putAt, takeAt, occupied;

    public BrokenBoundedBuffer()
    {
        this(4);
    }

    public BrokenBoundedBuffer(int capacity)
    {
        buffer = new Object[capacity];
    }

    public synchronized void put(Object x) throws InterruptedException
    {
        while (occupied == buffer.length)
        {
            wait();
        }
        TestThread.checkpoint("putNotify");
        notify();
        ++occupied;
        putAt %= buffer.length;
        buffer[putAt++] = x;
    }

    public synchronized Object take() throws InterruptedException
    {
        while (occupied == 0)
        {
            wait();
        }
        TestThread.checkpoint("takeNotify");
        notify();
        --occupied;
        takeAt %= buffer.length;
        return buffer[takeAt++];
    }
}
