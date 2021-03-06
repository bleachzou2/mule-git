/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.xmpp.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.transport.xmpp.XmppsConnector;

/**
 * Registers a Bean Definition Parser for handling <code><xmpp:connector></code> elements.
 */
public class XmppsNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
//        registerStandardTransportEndpoints(XmppsConnector.XMPPS, XmppNamespaceHandler.REQUIRED_ADDRESS_ATTRIBUTES).addAlias(XmppNamespaceHandler.RECIPIENT, URIBuilder.PATH).registerPreProcessor(new XmppNamespaceHandler.RequireNickname());
        this.registerConnectorDefinitionParser(XmppsConnector.class);
    }

}