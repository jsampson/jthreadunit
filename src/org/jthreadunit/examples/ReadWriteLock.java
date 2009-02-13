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

public class ReadWriteLock
{
    private int readerCount;
    private boolean writeLocked;
    private int waitingWriters;

    public synchronized void acquireRead() throws InterruptedException
    {
        while (writeLocked || waitingWriters > 0)
        {
            wait();
        }

        readerCount++;
    }

    public synchronized void releaseRead()
    {
        readerCount--;
        notifyAll();
    }

    public synchronized void acquireWrite() throws InterruptedException
    {
        waitingWriters++;

        try
        {
            while (writeLocked || readerCount > 0)
            {
                wait();
            }
        }
        catch (InterruptedException interrupted)
        {
            notifyAll();
            throw interrupted;
        }
        finally
        {
            waitingWriters--;
        }

        writeLocked = true;
    }

    public synchronized void releaseWrite()
    {
        writeLocked = false;
        notifyAll();
    }
}
