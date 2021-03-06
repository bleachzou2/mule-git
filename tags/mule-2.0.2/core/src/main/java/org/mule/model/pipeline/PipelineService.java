/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.model.pipeline;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.component.Component;
import org.mule.api.transport.DispatchException;
import org.mule.component.SimpleCallableJavaComponent;
import org.mule.config.i18n.CoreMessages;
import org.mule.model.direct.DirectService;

public class PipelineService extends DirectService
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -2788210157354765190L;

    public PipelineService()
    {
        super();
    }

    protected MuleMessage doSend(MuleEvent event) throws MuleException
    {
        try
        {
            MuleMessage result = component.onCall(event);
            MuleMessage returnMessage = null;
            returnMessage = (MuleMessage) result;

            if (!event.isStopFurtherProcessing())
            {
                // // TODO what about this code?
                // Map context = RequestContext.clearProperties();
                // if (context != null) {
                // returnMessage.addProperties(context);
                // }
                if (outboundRouter.hasEndpoints())
                {
                    MuleMessage outboundReturnMessage = outboundRouter.route(returnMessage, event.getSession(),
                        event.isSynchronous());
                    if (outboundReturnMessage != null)
                    {
                        returnMessage = outboundReturnMessage;
                    }
                }
                else
                {
                    logger.debug("Outbound router on service '" + name + "' doesn't have any endpoints configured.");
                }
            }

            return returnMessage;
        }
        catch (Exception e)
        {
            throw new DispatchException(event.getMessage(), event.getEndpoint(), e);
        }
    }

    protected void doDispatch(MuleEvent event) throws MuleException
    {
        sendEvent(event);
    }

    // @Override
    public void setComponent(Component component)
    {
        if (!(component instanceof SimpleCallableJavaComponent))
        {
            throw new MuleRuntimeException(CoreMessages.objectNotOfCorrectType(component.getClass(),
                SimpleCallableJavaComponent.class));
        }
        super.setComponent(component);
    }

}
