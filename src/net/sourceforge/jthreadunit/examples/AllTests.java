package net.sourceforge.jthreadunit.examples;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
    public static Test suite()
    {
        TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(SemaphoreTest.class);
        testSuite.addTestSuite(BoundedBufferTest.class);
        testSuite.addTestSuite(ReadWriteLockTest.class);
        testSuite.addTestSuite(SynchronizedBlockTest.class);
        return testSuite;
    }
}
