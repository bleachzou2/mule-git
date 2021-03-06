/*
 * $Id: BenchmarkComponent.java 2668 2006-08-10 03:34:44Z holger $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.benchmark;

import org.mule.umo.lifecycle.Callable;
import org.mule.umo.UMOEventContext;

/**
 * <code>BenchmarkComponent</code> is a simple component used for
 * processing requests during benchmark tests
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision: 2668 $
 */
public class BenchmarkComponent implements Callable
{
    private long executionTime = 0;

    public Object onCall(UMOEventContext eventContext) throws Exception
    {
        //System.out.println(" Benchmark component Received on: " + eventContext.getEndpointURI().toString());        
        if(getExecutionTime()> 0) {
            Thread.sleep(getExecutionTime());
        }
        return eventContext.getTransformedMessage();
    }

    public long getExecutionTime()
    {
        return executionTime;
    }

    public void setExecutionTime(long executionTime)
    {
        this.executionTime = executionTime;
    }

}
