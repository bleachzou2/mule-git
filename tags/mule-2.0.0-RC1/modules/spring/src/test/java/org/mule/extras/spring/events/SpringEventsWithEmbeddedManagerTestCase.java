/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extras.spring.events;

public class SpringEventsWithEmbeddedManagerTestCase extends SpringEventsTestCase
{

    protected String getConfigResources()
    {
        return "../../../../../../../../../../tests/functional/src/test/resources/org/mule/test/spring/mule-events-app-with-embedded-manager.xml";
    }

    public void testCorrectManagerLoaded()
    {
        assertNotNull(managementContext.getRegistry().lookupObject("embeddedManager"));
    }
}
