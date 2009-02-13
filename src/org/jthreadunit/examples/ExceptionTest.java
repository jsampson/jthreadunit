package org.jthreadunit.examples;

import org.jthreadunit.TestThread;

import junit.framework.TestCase;


public class ExceptionTest extends TestCase
{
    public void testExceptionInAction() throws InterruptedException
    {
        ThreadGroup group = new ThreadGroup("ThrowException");
        ExceptionThread thread = new ExceptionThread(group, "thread1");
        thread.start();
        thread.performAction("ThrowException");
        thread.kill();
    }

    public static class ExceptionThread extends TestThread
    {
        public ExceptionThread(ThreadGroup group, String name)
        {
            super(group, name);
        }

        public void doThrowException() throws Exception
        {
            throw new Exception("Test Exception");
        }
    }
}
