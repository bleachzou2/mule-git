/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.samples.voipservice;

import org.mule.tck.FunctionalTestCase;

public class VoipConfigurationTestCase extends FunctionalTestCase
{

    protected String getConfigResources()
    {
        return "voip-broker-sync-config.xml";
    }

    public void testParse()
    {
        // no-op
    }

}
