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
 */
package org.mule.impl;

import org.mule.impl.message.ExceptionPayload;
import org.mule.impl.model.AbstractComponent;
import org.mule.management.stats.ComponentStatistics;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

/**
 * <code>DefaultComponentExceptionStrategy</code> is the default exception
 * handler for components.
 * 
 * The handler logs errors and will forward the message and exception to an
 * exception endpointUri if one is set on this Exception strategy
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class DefaultComponentExceptionStrategy extends DefaultExceptionStrategy
{
    /**
     * The component to which the Exception handler belongs
     */
    protected UMOComponent component;

    protected ComponentStatistics statistics;

    public DefaultComponentExceptionStrategy()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param component the owner of this exception strategy
     * @see DefaultLifecycleAdapter
     */
    public DefaultComponentExceptionStrategy(UMOComponent component)
    {
        super();
        setComponent(component);
    }

    /**
     * @return the UniversalMessageObject to which this handler is attached
     */
    public UMOComponent getComponent()
    {
        return component;
    }

    protected void defaultHandler(Throwable t)
    {
        logException(t);
        // Lazzy initialisation of the component
        // This strategy should be associated with only one component
        // and thus there is no concurrency problem
        if (component == null) {
            UMOEvent event = RequestContext.getEvent();
            if (event == null) { // very bad should not happen
                logger.fatal("The Default Component Exception Strategy has been invoked but there is no current event on the context");
                logger.fatal("The error is: " + t.getMessage(), t);
            } else {
                setComponent(event.getComponent());
            }
        }

        if (statistics != null) {
            statistics.incExecutionError();
        }

        if (component != null) {
            logger.error("Caught exception in Exception Strategy for: " + component.getDescriptor().getName() + ": "
                    + t, t);
            //Users can overide this method if they do not want the transaction to roll back
            markTransactionForRollback();
        }
        if (RequestContext.getEvent() != null) {
            RequestContext.setExceptionPayload(new ExceptionPayload(t));
        }
    }

    protected void logFatal(UMOMessage message, Throwable t)
    {
        super.logFatal(message, t);
        if (statistics != null) {
            statistics.incFatalError();
        }
    }

    protected void routeException(UMOMessage message, UMOImmutableEndpoint failedEndpoint, Throwable t)
    {
        UMOEndpoint ep = getEndpoint(t);
        if (ep != null) {
            super.routeException(message, failedEndpoint, t);
            statistics.getOutboundRouterStat().incrementRoutedMessage(ep);
        }
    }

    public void setComponent(UMOComponent component)
    {
        this.component = component;
        if (component instanceof AbstractComponent) {
            this.statistics = ((AbstractComponent) component).getStatistics();
        }
    }
}
