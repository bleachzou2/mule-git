/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.rmi;

import org.mule.providers.AbstractFunctionalTestCase;

public class RmiFunctionalTestCase extends AbstractFunctionalTestCase
{

    public RmiFunctionalTestCase()
    {
        super("rmi", "rmi-functional-test.xml");
    }

}
