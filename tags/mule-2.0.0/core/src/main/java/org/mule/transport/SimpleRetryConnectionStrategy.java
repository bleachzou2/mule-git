/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport;

import org.mule.api.transport.Connectable;
import org.mule.config.ExceptionHelper;
import org.mule.config.i18n.CoreMessages;
import org.mule.util.ObjectUtils;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple connection retry strategy where the a connection will be attempted X
 * number of retryCount every Y milliseconds. The <i>retryCount</i> and <i>frequency</i>
 * properties can be set to customise the behaviour.
 */

public class SimpleRetryConnectionStrategy extends AbstractConnectionStrategy
{
    public static final int DEFAULT_FREQUENCY = 2000;
    public static final int DEFAULT_RETRY_COUNT = 2;
    public static final int RETRY_COUNT_FOREVER = -1;

    protected static class RetryCounter extends ThreadLocal
    {
        public int countRetry()
        {
            return ((AtomicInteger) get()).incrementAndGet();
        }
        
        public void reset()
        {
            ((AtomicInteger) get()).set(0);
        }

        //@Override
        public AtomicInteger current()
        {
            return (AtomicInteger) get();
        }

        // @Override
        protected Object initialValue()
        {
            return new AtomicInteger(0);
        }
    }

    protected static final RetryCounter retryCounter = new RetryCounter();

    protected static final ThreadLocal called = new ThreadLocal();

    private volatile int retryCount = DEFAULT_RETRY_COUNT;
    private volatile long retryFrequency = DEFAULT_FREQUENCY;

    protected void doConnect(Connectable connectable) throws FatalConnectException
    {
        while (true)
        {
            final Boolean recursiveCallDetected = (Boolean) ObjectUtils.defaultIfNull(called.get(), Boolean.FALSE);
            if (!recursiveCallDetected.booleanValue())
            {
                retryCounter.countRetry();
            }
            called.set(Boolean.TRUE);

            try
            {
                connectable.connect();
                if (logger.isDebugEnabled())
                {
                    logger.debug("Successfully connected to " + getDescription(connectable));
                }
                break;
            }
            catch (InterruptedException ie)
            {
                // If we were interrupted it's probably because the server is
                // shutting down
                throw new FatalConnectException(
                    // TODO it's not only endpoint that is reconnected, connectors too
                    CoreMessages.reconnectStrategyFailed(this.getClass(), 
                        this.getDescription(connectable)), ie, connectable);
            }
            catch (Exception e)
            {
                if (e instanceof FatalConnectException)
                {
                    // rethrow
                    throw (FatalConnectException) e;
                }
                if (retryCount != RETRY_COUNT_FOREVER && retryCounter.current().get() >= retryCount)
                {
                    throw new FatalConnectException(
                        // TODO it's not only endpoint that is reconnected, connectors too
                        CoreMessages.reconnectStrategyFailed(this.getClass(),
                            this.getDescription(connectable)), e, connectable);
                }

                if (logger.isErrorEnabled())
                {
                    StringBuffer msg = new StringBuffer(512);
                    msg.append("Failed to connect/reconnect: ").append(
                            getDescription(connectable));
                    Throwable t = ExceptionHelper.getRootException(e);
                    msg.append(". Root Exception was: ").append(ExceptionHelper.writeException(t));
                    logger.error(msg.toString(), e);
                }

                if (logger.isInfoEnabled())
                {
                    logger.info("Waiting for " + retryFrequency + "ms before reconnecting. Failed attempt "
                                + retryCounter.current().get() + " of " +
                                (retryCount != RETRY_COUNT_FOREVER ? String.valueOf(retryCount) : "unlimited"));
                }

                try
                {
                    Thread.sleep(retryFrequency);
                }
                catch (InterruptedException e1)
                {
                    throw new FatalConnectException(
                        // TODO it's not only endpoint that is reconnected, connectors too
                        CoreMessages.reconnectStrategyFailed(this.getClass(), 
                            this.getDescription(connectable)), e, connectable);
                }
            }
            finally
            {
                called.set(Boolean.FALSE);
            }
        }
    }

    /**
     * Resets any state stored in the retry strategy
     */
    public synchronized void resetState()
    {
        retryCounter.reset();
    }

    public int getRetryCount()
    {
        return retryCount;
    }

    /**
     * How many times to retry. Set to -1 to retry forever.
     * @param retryCount number of retries
     */
    public void setRetryCount(int retryCount)
    {
        this.retryCount = retryCount;
    }

    public long getRetryFrequency()
    {
        return retryFrequency;
    }

    public void setRetryFrequency(long retryFrequency)
    {
        this.retryFrequency = retryFrequency;
    }
}
