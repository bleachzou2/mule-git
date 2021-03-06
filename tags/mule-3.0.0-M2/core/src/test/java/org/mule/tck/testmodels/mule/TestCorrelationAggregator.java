/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.testmodels.mule;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.routing.AggregationException;
import org.mule.routing.inbound.AbstractCorrelationAggregator;
import org.mule.routing.inbound.EventGroup;

/**
 * <code>TestResponseAggregator</code> is a mock response Agrregator object used for
 * testing configuration
 */
public class TestCorrelationAggregator extends AbstractCorrelationAggregator
{
    private String testProperty;

    @Override
    protected MuleMessage aggregateEvents(EventGroup events) throws AggregationException
    {
        return new DefaultMuleMessage("test", muleContext);
    }

    public String getTestProperty()
    {
        return testProperty;
    }

    public void setTestProperty(String testProperty)
    {
        this.testProperty = testProperty;
    }
}
