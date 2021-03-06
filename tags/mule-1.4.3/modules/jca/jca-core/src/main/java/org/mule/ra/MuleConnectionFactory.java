/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ra;

import java.io.Serializable;

import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

/**
 * <code>MuleConnectionFactory</code> defines the connection factory interface that
 * the RA clients will obtain a reference to.
 */
public interface MuleConnectionFactory extends Serializable, Referenceable
{
    MuleConnection createConnection() throws ResourceException;

    MuleConnection createConnection(MuleConnectionRequestInfo info) throws ResourceException;

    ConnectionManager getManager();

    void setManager(ConnectionManager manager);

    MuleManagedConnectionFactory getFactory();

    void setFactory(MuleManagedConnectionFactory factory);

    MuleConnectionRequestInfo getInfo();

    void setInfo(MuleConnectionRequestInfo info);
}
