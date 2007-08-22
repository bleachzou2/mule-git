/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.http.jetty;

import org.mule.providers.http.HttpConnector;

/**
 * <code>ServletConnector</code> is a channel adapter between Mule and a servlet
 * engine.
 */

public class JettyConnector extends HttpConnector
{

    public JettyConnector()
    {
        super();
        registerSupportedProtocol("http");
        registerSupportedProtocol("https");
        registerSupportedProtocol("rest");
    }

    public String getProtocol()
    {
        return "jetty";
    }

}
