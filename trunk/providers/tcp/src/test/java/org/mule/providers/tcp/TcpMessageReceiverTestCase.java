/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.providers.tcp;

import com.mockobjects.dynamic.Mock;
import org.mule.impl.endpoint.MuleEndpoint;
import org.mule.providers.AbstractConnector;
import org.mule.tck.providers.AbstractMessageReceiverTestCase;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMODescriptor;
import org.mule.umo.provider.UMOMessageReceiver;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class TcpMessageReceiverTestCase extends AbstractMessageReceiverTestCase
{
    
    public UMOMessageReceiver getMessageReceiver() throws Exception
    {
        endpoint = new MuleEndpoint("tcp://localhost:10101", true);
        endpoint.setConnector(new TcpConnector());
        Mock mockComponent = new Mock(UMOComponent.class);
		Mock mockDescriptor = new Mock(UMODescriptor.class);
		mockComponent.expectAndReturn("getDescriptor" , mockDescriptor.proxy());
		mockDescriptor.expectAndReturn("getResponseTransformer", null);
        return new TcpMessageReceiver((AbstractConnector) endpoint.getConnector(), (UMOComponent) mockComponent.proxy(), endpoint);
    }
}
