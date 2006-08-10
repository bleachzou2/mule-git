/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extras.spring.events;

import org.mule.tck.functional.EventCallback;
import org.mule.util.StringMessageUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * <code>TestMuleEventBean</code> is a MuleEventBean for testing with the
 * MuleEventMulticaster.
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class TestAllEventBean implements MuleEventListener, ApplicationListener
{
    private EventCallback eventCallback;

    public void onApplicationEvent(ApplicationEvent event)
    {
        System.out.println(StringMessageUtils.getBoilerPlate("Received message  " + event));

        if (eventCallback != null) {
            try {
                eventCallback.eventReceived(null, event);
            } catch (Exception e1) {
                throw new RuntimeException("Callback failed: " + e1.getMessage(), e1);
            }
        }
    }

    public EventCallback getEventCallback()
    {
        return eventCallback;
    }

    public void setEventCallback(EventCallback eventCallback)
    {
        this.eventCallback = eventCallback;
    }
}
