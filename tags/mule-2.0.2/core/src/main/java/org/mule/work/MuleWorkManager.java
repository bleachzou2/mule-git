/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mule.work;

import org.mule.api.MuleException;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.context.WorkManager;
import org.mule.api.work.WorkExecutor;

import java.text.MessageFormat;
import java.util.List;

import javax.resource.spi.XATerminator;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkCompletedException;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;

import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>MuleWorkManager</code> is a JCA Work manager implementation used to manage
 * thread allocation for Mule components and connectors. This code has been adapted
 * from the Geronimo implementation.
 */
public class MuleWorkManager implements WorkManager
{
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(MuleWorkManager.class);

    /**
     * Graceful shutdown delay
     */
    private static final long SHUTDOWN_TIMEOUT = 5000L;

    /**
     * The ThreadingProfile used for creation of the underlying ExecutorService
     */
    private final ThreadingProfile threadingProfile;

    /**
     * The actual pool of threads used by this MuleWorkManager to process the Work
     * instances submitted via the (do,start,schedule)Work methods.
     */
    private volatile ExecutorService workExecutorService;
    private final String name;

    /**
     * Various policies used for work execution
     */
    private final WorkExecutor scheduleWorkExecutor = new ScheduleWorkExecutor();
    private final WorkExecutor startWorkExecutor = new StartWorkExecutor();
    private final WorkExecutor syncWorkExecutor = new SyncWorkExecutor();

    public MuleWorkManager(ThreadingProfile profile, String name)
    {
        super();

        if (name == null)
        {
            name = "WorkManager#" + hashCode();
        }

        this.threadingProfile = profile;
        this.name = name;
    }

    public synchronized void start() throws MuleException
    {
        if (workExecutorService == null)
        {
            workExecutorService = threadingProfile.createPool(name);
        }
    }

    public synchronized void dispose()
    {
        if (workExecutorService != null)
        {
            try
            {
                // Cancel currently executing tasks
                List outstanding = workExecutorService.shutdownNow();

                // Wait a while for existing tasks to terminate
                if (!workExecutorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS))
                {
                    if (logger.isWarnEnabled())
                    {
                        logger.warn(MessageFormat.format(
                                "Pool {0} did not terminate in time; {1} work items were cancelled.",
                                new Object[] {name, outstanding.isEmpty() ? "No" : Integer.toString(outstanding.size())}));
                    }
                }
            }
            catch (InterruptedException ie)
            {
                // (Re-)Cancel if current thread also interrupted
                workExecutorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
            finally
            {
                workExecutorService = null;
            }
        }
    }


    // TODO
    public XATerminator getXATerminator()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#doWork(javax.resource.spi.work.Work)
     */
    public void doWork(Work work) throws WorkException
    {
        executeWork(new WorkerContext(work), syncWorkExecutor);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#doWork(javax.resource.spi.work.Work,
     *      long, javax.resource.spi.work.ExecutionContext,
     *      javax.resource.spi.work.WorkListener)
     */
    public void doWork(Work work, long startTimeout, ExecutionContext execContext, WorkListener workListener)
        throws WorkException
    {
        WorkerContext workWrapper = new WorkerContext(work, startTimeout, execContext, workListener);
        workWrapper.setThreadPriority(Thread.currentThread().getPriority());
        executeWork(workWrapper, syncWorkExecutor);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#startWork(javax.resource.spi.work.Work)
     */
    public long startWork(Work work) throws WorkException
    {
        WorkerContext workWrapper = new WorkerContext(work);
        workWrapper.setThreadPriority(Thread.currentThread().getPriority());
        executeWork(workWrapper, startWorkExecutor);
        return System.currentTimeMillis() - workWrapper.getAcceptedTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#startWork(javax.resource.spi.work.Work,
     *      long, javax.resource.spi.work.ExecutionContext,
     *      javax.resource.spi.work.WorkListener)
     */
    public long startWork(Work work,
                          long startTimeout,
                          ExecutionContext execContext,
                          WorkListener workListener) throws WorkException
    {
        WorkerContext workWrapper = new WorkerContext(work, startTimeout, execContext, workListener);
        workWrapper.setThreadPriority(Thread.currentThread().getPriority());
        executeWork(workWrapper, startWorkExecutor);
        return System.currentTimeMillis() - workWrapper.getAcceptedTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#scheduleWork(javax.resource.spi.work.Work)
     */
    public void scheduleWork(Work work) throws WorkException
    {
        WorkerContext workWrapper = new WorkerContext(work);
        workWrapper.setThreadPriority(Thread.currentThread().getPriority());
        executeWork(workWrapper, scheduleWorkExecutor);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.resource.spi.work.MuleWorkManager#scheduleWork(javax.resource.spi.work.Work,
     *      long, javax.resource.spi.work.ExecutionContext,
     *      javax.resource.spi.work.WorkListener)
     */
    public void scheduleWork(Work work,
                             long startTimeout,
                             ExecutionContext execContext,
                             WorkListener workListener) throws WorkException
    {
        WorkerContext workWrapper = new WorkerContext(work, startTimeout, execContext, workListener);
        workWrapper.setThreadPriority(Thread.currentThread().getPriority());
        executeWork(workWrapper, scheduleWorkExecutor);
    }

    /**
     * @see Executor#execute(Runnable)
     */
    public void execute(Runnable work)
    {
        if (workExecutorService == null)
        {
            throw new IllegalStateException("This MuleWorkManager '" + name + "' was never started");
        }
        else if (workExecutorService.isShutdown())
        {
            throw new IllegalStateException("This MuleWorkManager '" + name + "' is stopped");
        }

        workExecutorService.execute(work);
    }

    /**
     * Execute the specified Work.
     *
     * @param work Work to be executed.
     * @exception WorkException Indicates that the Work execution has been
     *                unsuccessful.
     */
    private void executeWork(WorkerContext work, WorkExecutor workExecutor) throws WorkException
    {
        if (workExecutorService == null || workExecutorService.isShutdown())
        {
            throw new IllegalStateException("This MuleWorkManager '" + name + "' is stopped");
        }

        try
        {
            work.workAccepted(this);
            workExecutor.doExecute(work, workExecutorService);
            WorkException exception = work.getWorkException();
            if (null != exception)
            {
                throw exception;
            }
        }
        catch (InterruptedException e)
        {
            WorkCompletedException wcj = new WorkCompletedException("The execution has been interrupted for WorkManager: " + name, e);
            wcj.setErrorCode(WorkException.INTERNAL);
            throw wcj;
        }
    }

}
