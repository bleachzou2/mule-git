/* 
 * $Id$
 * ------------------------------------------------------------------------------------------------------
 * 
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 * 
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file. 
 *
 */

package org.mule.test.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.mule.util.concurrent.Latch;
import org.mule.util.concurrent.WaitPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Holger Hoffstaette
 */

public class WaitPolicyTestCase extends TestCase
{
    private ExceptionCollectingThreadGroup _asyncGroup;
    private ThreadPoolExecutor _executor;

    public WaitPolicyTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        // allow 1 active & 1 queued Thread
        _executor = new ThreadPoolExecutor(1, 1, 10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(1));

        _asyncGroup = new ExceptionCollectingThreadGroup();
        SleepyTask.activeTasks = new AtomicInteger(0);
    }

    protected void tearDown() throws Exception
    {
        _executor.shutdown();
        _asyncGroup.destroy();
        super.tearDown();
    }

    // submit the given Runnable to the given ExecutorService, but
    // do so in a separate thread to avoid blocking the test case
    // when the Executor's queue is full.
    // Returns control to the caller when the thread has been started in
    // order to avoid OS-dependent delays in the control flow.
    // At the time of return the Runnable may or may not be in the queue,
    // rejected, running or already finished. :-)
    private Thread execute(final ExecutorService e, final Runnable r) throws InterruptedException
    {
        final Latch submitterIsRunning = new Latch();

        Runnable asyncRunnable = new Runnable()
        {
            public void run()
            {
                // the synchronized block is REALLY important because otherwise
                // two asynchronously started submitters might stumble over each
                // other, submitting their runnables out-of-order and thus
                // causing test failures.
                synchronized (e)
                {
                    submitterIsRunning.countDown();
                    e.execute(r);
                }
            }
        };

        Thread t = new Thread(_asyncGroup, asyncRunnable);
        t.setDaemon(true);
        t.start();

        submitterIsRunning.await();
        return t;
    }

    public void testWaitPolicyWithShutdownExecutor() throws Exception
    {
        assertEquals(0, SleepyTask.activeTasks.get());

        // wants to wait forever, but will fail immediately
        _executor.setRejectedExecutionHandler(new LastRejectedWaitPolicy());
        _executor.shutdown();

        // must fail immediately
        Thread failedThread = this.execute(_executor, new SleepyTask("boo", 1000));
        Thread.sleep(500);

        List exceptions = _asyncGroup.collectedExceptions();
        assertEquals(1, exceptions.size());

        Map.Entry threadFailure = (Map.Entry)((Map)exceptions.get(0)).entrySet().iterator().next();
        assertEquals(failedThread, threadFailure.getKey());
        assertEquals(RejectedExecutionException.class, threadFailure.getValue().getClass());
        assertEquals(0, SleepyTask.activeTasks.get());
    }

    public void testWaitPolicyForever() throws Exception
    {
        assertEquals(0, SleepyTask.activeTasks.get());

        // wait forever
        LastRejectedWaitPolicy policy = new LastRejectedWaitPolicy(-1, TimeUnit.SECONDS);
        _executor.setRejectedExecutionHandler(policy);

        // task 1 runs immediately
        this.execute(_executor, new SleepyTask("hans", 1000));

        // task 2 is queued
        this.execute(_executor, new SleepyTask("franz", 1000));

        // task 3 is initially rejected but waits forever
        Runnable s3 = new SleepyTask("beavis", 1000);
        this.execute(_executor, s3);

        // at least one task should have been queued
        assertFalse(_executor.awaitTermination(4000, TimeUnit.MILLISECONDS));
        assertEquals(s3, policy.lastRejectedRunnable());
        assertEquals(0, SleepyTask.activeTasks.get());

        // shutdown & try again
        _executor.shutdown();
        assertTrue(_executor.awaitTermination(4000, TimeUnit.MILLISECONDS));
        assertEquals(s3, policy.lastRejectedRunnable());
        assertEquals(0, SleepyTask.activeTasks.get());
    }

    public void testWaitPolicyWithTimeout() throws Exception
    {
        assertEquals(0, SleepyTask.activeTasks.get());

        // set a reasonable retry interval
        LastRejectedWaitPolicy policy = new LastRejectedWaitPolicy(2500, TimeUnit.MILLISECONDS);
        _executor.setRejectedExecutionHandler(policy);

        // task 1 runs immediately
        this.execute(_executor, new SleepyTask("hans", 1000));

        // 2 is queued
        this.execute(_executor, new SleepyTask("franz", 1000));

        // 3 is initially rejected but will eventually succeed
        Runnable s3 = new SleepyTask("tweety", 1000);
        this.execute(_executor, s3);

        assertFalse(_executor.awaitTermination(4000, TimeUnit.MILLISECONDS));
        assertEquals(s3, policy.lastRejectedRunnable());
        assertEquals(0, SleepyTask.activeTasks.get());
    }

    public void testWaitPolicyWithTimeoutFailure() throws Exception
    {
        assertEquals(0, SleepyTask.activeTasks.get());

        // set a really short wait interval
        long failureInterval = 100;
        LastRejectedWaitPolicy policy = new LastRejectedWaitPolicy(failureInterval, TimeUnit.MILLISECONDS);
        _executor.setRejectedExecutionHandler(policy);

        // task 1 runs immediately
        this.execute(_executor, new SleepyTask("hans", 1000));

        // 2 is queued
        this.execute(_executor, new SleepyTask("franz", 1000));

        // 3 is initially rejected & will retry but should fail quickly
        Runnable s3 = new SleepyTask("butthead", 1000);
        Thread failedThread = this.execute(_executor, s3);
        // give failure a chance
        Thread.sleep(failureInterval * 2);

        List exceptions = _asyncGroup.collectedExceptions();
        assertEquals(1, exceptions.size());

        Map.Entry threadFailure = (Map.Entry)((Map)exceptions.get(0)).entrySet().iterator().next();
        assertEquals(failedThread, threadFailure.getKey());
        assertEquals(RejectedExecutionException.class, threadFailure.getValue().getClass());

        _executor.shutdown();
        assertTrue(_executor.awaitTermination(2500, TimeUnit.MILLISECONDS));
        assertEquals(s3, policy.lastRejectedRunnable());
        assertEquals(0, SleepyTask.activeTasks.get());
    }

}


class LastRejectedWaitPolicy extends WaitPolicy
{
    // needed to hand the rejected Runnable back to the TestCase
    private Runnable _lastRejected;

    public LastRejectedWaitPolicy()
    {
        super();
    }

    public LastRejectedWaitPolicy(long time, TimeUnit timeUnit)
    {
        super(time, timeUnit);
    }

    public Runnable lastRejectedRunnable()
    {
        return _lastRejected;
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor e)
    {
        _lastRejected = r;
        super.rejectedExecution(r, e);
    }

}


// task to execute - just sleeps for the given interval
class SleepyTask extends Object implements Runnable
{
    public static AtomicInteger activeTasks;

    private String _name;
    private long _sleepTime;

    public SleepyTask(String name, long sleepTime)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("SleepyTask needs a name!");
        }
    
        _name = name;
        _sleepTime = sleepTime;
    }

    public String toString()
    {
        return this.getClass().getName() + "{" + _name + ", " + _sleepTime + "}";
    }

    public void run()
    {
        activeTasks.incrementAndGet();

        try
        {
            Thread.sleep(_sleepTime);
        }
        catch (InterruptedException iex)
        {
            // ignore
        }

        activeTasks.decrementAndGet();
    }

}


// ThreadGroup wrapper that collects uncaught exceptions
class ExceptionCollectingThreadGroup extends ThreadGroup
{
    private List _exceptions;

    public ExceptionCollectingThreadGroup()
    {
        super("asyncGroup");
        _exceptions = new ArrayList();
    }

    // collected Map(Thread, Throwable) associations
    public List collectedExceptions()
    {
        return _exceptions;
    }

    // all uncaught Thread exceptions end up here
    public void uncaughtException(Thread t, Throwable e)
    {
        _exceptions.add(Collections.singletonMap(t, e));
    }

}

