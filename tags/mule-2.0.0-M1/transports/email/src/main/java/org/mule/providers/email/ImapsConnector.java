/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.email;

/**
 * Creates a secure IMAP connection
 */
public class ImapsConnector extends AbstractTlsRetrieveMailConnector
{
    public static final int DEFAULT_IMAPS_PORT = 993;

    public ImapsConnector()
    {
        super(DEFAULT_IMAPS_PORT, ImapsSocketFactory.MULE_IMAPS_NAMESPACE, ImapsSocketFactory.class);
    }
  
    public String getProtocol()
    {
        return "imaps";
    }
    
    public String getBaseProtocol()
    {
        return "imap";
    }

}
