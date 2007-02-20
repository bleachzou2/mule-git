/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.management.mbeans;

import org.mule.umo.UMOException;
import org.mule.umo.UMOManagementContext;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.ObjectNameHelper;

import java.beans.ExceptionListener;

public class ConnectorService implements ConnectorServiceMBean
{
    private final UMOConnector connector;
    private final String name;

    public ConnectorService(final UMOConnector connector)
    {
        this.connector = connector;
        name = ObjectNameHelper.getConnectorName(connector);
    }

    public boolean isStarted()
    {
        return connector.isStarted();
    }

    public boolean isDisposed()
    {
        return connector.isDisposed();
    }

    public boolean isDisposing()
    {
        return connector.isDisposing();
    }

    public String getName()
    {
        return name;
    }

    public String getProtocol()
    {
        return connector.getProtocol();
    }

    public ExceptionListener getExceptionListener()
    {
        return connector.getExceptionListener();
    }

    public void startConnector() throws UMOException
    {
        connector.start();
    }

    public void stopConnector() throws UMOException
    {
        connector.stop();
    }

    public void dispose()
    {
        connector.dispose();
    }


    public void initialise(UMOManagementContext managementContext) throws InitialisationException
    {
        connector.initialise(managementContext);
    }
}
