/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl;

import org.mule.config.MuleProperties;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOExceptionPayload;
import org.mule.umo.UMOMessage;

import java.util.Iterator;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingDeque;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>RequestContext</code> is a thread context where components can get the
 * current event or set response properties that will be sent on the outgoing
 * message.
 */
public final class RequestContext
{
    private static final Log logger = LogFactory.getLog(RequestContext.class);
    private static final ThreadLocal currentEvent = new ThreadLocal();

    public static LinkedBlockingDeque history = new LinkedBlockingDeque();

    /** Do not instanciate. */
    private RequestContext()
    {
        // no-op
    }

    public static UMOEventContext getEventContext()
    {
        UMOEvent event = getEvent();
        if (event != null)
        {
            return new MuleEventContext(event);
        }
        else
        {
            return null;
        }
    }

    public static UMOEvent getEvent()
    {
        return (UMOEvent) currentEvent.get();
    }

    public static UMOEvent copyAndSetEvent(UMOEvent event)
    {
        // RequestContext seems to be used to allow thread local mutation of events that
        // are not otherwise available in the scope.  so this is a good place to create a new
        // thread local copy - it will be read because supporting code is expecting mutation.
        if (event instanceof SafeThreadAccess)
        {
            event = (UMOEvent) ((SafeThreadAccess)event).newCopy();
        }
        currentEvent.set(event);
        return event;
    }

    /**
     * Sets a new message payload in the RequestContext but maintains all other
     * properties (session, endpoint, synchronous, etc.) from the previous event.
     * 
     * @param message - current message payload
     */
    public static void rewriteEvent(UMOMessage message)
    {
        if (message != null)
        {
            UMOEvent event = getEvent();
            if (event != null)
            {
                event = new MuleEvent(message, event);
                copyAndSetEvent(event);
            }
        }
    }

    public static void writeResponse(UMOMessage message)
    {
        if (message != null)
        {
            UMOEvent event = getEvent();
            if (event != null)
            {
                for (Iterator iterator = event.getMessage().getPropertyNames().iterator(); iterator.hasNext();)
                {
                    String key = (String) iterator.next();
                    if (key == null)
                    {
                        logger.warn("Message property key is null: please report the following stack trace to dev@mule.codehaus.org.",
                            new IllegalArgumentException());
                    }
                    else
                    {
                        if (key.startsWith(MuleProperties.PROPERTY_PREFIX))
                        {
                            Object newValue = message.getProperty(key);
                            Object oldValue = event.getMessage().getProperty(key);
                            if (newValue == null)
                            {
                                message.setProperty(key, oldValue);
                            }
                            else if (logger.isInfoEnabled() && !newValue.equals(oldValue))
                            {
                                logger.info("Message already contains property " + key + "=" + newValue
                                            + " not replacing old value: " + oldValue);
                            }
                        }
                    }
                }

                event = new MuleEvent(message, event.getEndpoint(), event.getSession(), event.isSynchronous());
                copyAndSetEvent(event);
            }
        }
    }

    /**
     * Resets the current request context (clears all information).
     */
    public static void clear()
    {
        copyAndSetEvent(null);
    }

    public static void setExceptionPayload(UMOExceptionPayload exceptionPayload)
    {
        getEvent().getMessage().setExceptionPayload(exceptionPayload);
    }

    public static UMOExceptionPayload getExceptionPayload()
    {
        return getEvent().getMessage().getExceptionPayload();
    }

    public static final class TraceHolder
    {
        public final Throwable throwable;
        public final long timestamp;
        public final String tag;
        public final String threadName;
        public final String messageName;

        public TraceHolder(final Throwable throwable, final String threadName, final long timestamp, final String tag, String messageName)
        {
            this.throwable = throwable;
            this.threadName = threadName;
            this.timestamp = timestamp;
            this.tag = tag;
            this.messageName = messageName;
        }
    }
}
