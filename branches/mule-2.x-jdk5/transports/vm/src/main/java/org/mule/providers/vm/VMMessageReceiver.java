/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.vm;

import org.mule.MuleException;
import org.mule.config.i18n.CoreMessages;
import org.mule.impl.MuleEvent;
import org.mule.impl.MuleMessage;
import org.mule.providers.TransactedPollingMessageReceiver;
import org.mule.transaction.TransactionCoordination;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOTransaction;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.lifecycle.CreateException;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.queue.Queue;
import org.mule.util.queue.QueueSession;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.util.List;

/**
 * <code>VMMessageReceiver</code> is a listener for events from a Mule component
 * which then simply passes the events on to the target component.
 */
public class VMMessageReceiver extends TransactedPollingMessageReceiver
{
    public static final long DEFAULT_VM_POLL_FREQUENCY = 10;
    public static final TimeUnit DEFAULT_VM_POLL_TIMEUNIT = TimeUnit.MILLISECONDS;

    private VMConnector connector;
    private final Object lock = new Object();

    public VMMessageReceiver(UMOConnector connector, UMOComponent component, UMOImmutableEndpoint endpoint)
            throws CreateException
    {
        super(connector, component, endpoint);
        this.setReceiveMessagesInTransaction(endpoint.getTransactionConfig().isTransacted());
        this.setFrequency(DEFAULT_VM_POLL_FREQUENCY);
        this.setTimeUnit(DEFAULT_VM_POLL_TIMEUNIT);
        this.connector = (VMConnector) connector;
    }

    protected void doDispose()
    {
        // template method
    }

    protected void doConnect() throws Exception
    {
        if (connector.isQueueEvents())
        {
            // Ensure we can create a vm queue
            QueueSession queueSession = connector.getQueueSession();
            Queue q = queueSession.getQueue(endpoint.getEndpointURI().getAddress());
            if (logger.isDebugEnabled())
            {
                logger.debug("Current queue depth for queue: " + endpoint.getEndpointURI().getAddress()
                        + " is: " + q.size());
            }
        }
    }

    protected void doDisconnect() throws Exception
    {
        // template method
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.UMOEventListener#onEvent(org.mule.umo.UMOEvent)
     */
    public void onEvent(UMOEvent event) throws UMOException
    {
        //Rewrite the event to treat it as a new event        
        event = new MuleEvent(new MuleMessage(event.getMessage().getPayload(), event.getMessage()),
                endpoint, component, event);

        if (connector.isQueueEvents())
        {
            QueueSession queueSession = connector.getQueueSession();
            Queue queue = queueSession.getQueue(endpoint.getEndpointURI().getAddress());
            try
            {
                queue.put(event);
            }
            catch (InterruptedException e)
            {
                throw new MuleException(CoreMessages.interruptedQueuingEventFor(this.endpoint
                        .getEndpointURI()), e);
            }
        }
        else
        {
            UMOMessage msg = event.getMessage();
            synchronized (lock)
            {
                routeMessage(msg);
            }
        }
    }

    public Object onCall(UMOEvent event) throws UMOException
    {
        //Rewrite the event to treat it as a new event
        event = new MuleEvent(new MuleMessage(event.getMessage().getPayload(), event.getMessage()),
                endpoint, component, event);

        UMOTransaction tx = TransactionCoordination.getInstance().getTransaction();
        if (tx == null)
        {
            return routeMessage(event.getMessage(), event.isSynchronous());
        }
        else
        {
            return routeMessage(event.getMessage(), tx, true);
        }
    }

    protected List getMessages() throws Exception
    {
        QueueSession qs = connector.getQueueSession();
        Queue queue = qs.getQueue(endpoint.getEndpointURI().getAddress());
        UMOEvent event = (UMOEvent) queue.poll(connector.getQueueTimeout());
        if (event != null)
        {
            //Rewrite the message to treat it as a new message
            UMOMessage msg = new MuleMessage(new MuleMessage(event.getMessage().getPayload(), event.getMessage()));
            routeMessage(msg);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.TransactionEnabledPollingMessageReceiver#processMessage(java.lang.Object)
     */
    protected void processMessage(Object msg) throws Exception
    {
        // This method is never called as the message is processed when received
    }

}
