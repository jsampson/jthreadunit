package net.sourceforge.jthreadunit.examples;

public interface BoundedBuffer
{
    public void put(Object x) throws InterruptedException;

    public Object take() throws InterruptedException;
}
