package net.sourceforge.jthreadunit;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMBean;
import java.lang.management.ThreadState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * A thread that runs in an endless loop performing actions. A controlling
 * thread should call the various methods of this class to control the
 * actions performed, and finally call {@link #stop()} when there are no
 * more actions to be performed. Calling {@link #interrupt()} will not
 * kill the thread, but merely reset it to the state of waiting for a command.
 * That is, if the thread is blocked, {@link #interrupt()} will unblock it
 * and make it ready to respond to {@link #performAction(String)}.
 * <p>
 * Each action must be implemented in a subclass by a parameterless method
 * with void return type named by prepending <tt>do</tt> to the capitalized
 * action name. For example, <tt>performAction("foo")</tt> results in a call
 * to the method with signature <tt>public void doFoo()</tt>.
 */
public abstract class TestThread extends Thread
{
    private String initiatedAction;

    public TestThread()
    {
        ThreadMBean threads = ManagementFactory.getThreadMBean();
        threads.setThreadContentionMonitoringEnabled(true);
    }

    /**
     * Called to indicate that this thread should perform the named action
     * completely without blocking.
     * <p>
     * @throws AssertionFailedError If this thread blocks before completing the
     *         action.
     */
    public void performAction(String actionName)
    {
        initiateAction(actionName);
        completeBlockedAction();
    }

    /**
     * Called to indicate that this thread should initiate the named action
     * but expect to block before completing it.
     * <p>
     * @throws AssertionFailedError If this thread completes the action without
     *         blocking.
     */
    public void actionShouldBlock(String actionName)
    {
        initiateAction(actionName);
        assertStillBlocked();
    }

    /**
     * Called to indicate that this thread should finish a previously
     * blocked action.
     * <p>
     * @throws AssertionFailedError If this thread is still blocked without
     *         having completed the action.
     */
    public void completeBlockedAction()
    {
        letRun();
        assertComplete();
    }

    /**
     * Called to indicate that a previously blocked action should still be
     * blocked.
     * <p>
     * @throws AssertionFailedError If this thread has completed the action
     *         without blocking.
     */
    public void assertStillBlocked()
    {
        letRun();
        assertNotComplete();
    }

    /**
     * Called from within an action method to mark the current action as
     * complete and wait for the next action without returning from the first
     * action method.
     */
    protected void expectAction(String actionName) throws InterruptedException
    {
        checkActionName(actionName);

        clearAction();
        String actualAction = waitForAction();

        if (!actualAction.equals(actionName))
        {
            throw new IllegalStateException(
                    "Expected action " + actionName
                    + " but was " + actualAction);
        }
    }

    public void run()
    {
        try
        {
            while (true)
            {
                try
                {
                    action();
                }
                catch (InterruptedException okay)
                {
                }
            }
        }
        catch (Error error)
        {
            throw error;
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private synchronized void initiateAction(String actionName)
    {
        checkActionName(actionName);

        if (initiatedAction != null)
        {
            throw new IllegalStateException("Previous action not complete");
        }

        initiatedAction = actionName;
        notify();
    }

    private void letRun()
    {
        ThreadMBean threads = ManagementFactory.getThreadMBean();
        assert threads.isThreadContentionMonitoringEnabled();
        ThreadInfo info;
        do
        {
            Thread.yield();
            info = threads.getThreadInfo(this.getId());
        }
        while (info != null
                && (info.getThreadState() == ThreadState.NEW
                    || info.getThreadState() == ThreadState.RUNNING));
    }

    private synchronized void assertComplete()
    {
        Assert.assertNull("Action should not be blocked", initiatedAction);
    }

    private synchronized void assertNotComplete()
    {
        Assert.assertNotNull("Action should be blocked", initiatedAction);
    }

    private void action() throws Throwable
    {
        String actionName = waitForAction();

        try
        {
            getActionMethod(actionName).invoke(this, new Object[0]);
        }
        catch (InvocationTargetException exception)
        {
            throw (Throwable) exception.getTargetException();
        }
        finally
        {
            clearAction();
        }
    }

    private synchronized String waitForAction() throws InterruptedException
    {
        if (initiatedAction == null)
        {
            wait();
            assert initiatedAction != null;
        }
        return initiatedAction;
    }

    private synchronized void clearAction()
    {
        initiatedAction = null;
    }

    private void checkActionName(String actionName)
    {
        if (actionName == null || actionName.equals(""))
        {
            throw new IllegalArgumentException(
                    "Action name must be non-empty string");
        }
    }

    private Method getActionMethod(String actionName)
            throws NoSuchMethodException
    {
        String methodName = "do" + capitalize(actionName);

        Method method = this.getClass().getMethod(methodName, new Class[0]);

        if (method.getReturnType() != Void.TYPE)
        {
            throw new NoSuchMethodException(
                "Action method " + methodName + " should be void");
        }

        return method;
    }

    private static String capitalize(String string)
    {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
