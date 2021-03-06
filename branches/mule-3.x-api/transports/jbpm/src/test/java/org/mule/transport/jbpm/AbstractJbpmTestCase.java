/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.jbpm;

import org.mule.tck.util.MuleDerbyTestUtils;
import org.mule.transport.bpm.tests.AbstractBpmTestCase;

public abstract class AbstractJbpmTestCase extends AbstractBpmTestCase
{
    private static boolean derbySetupDone = false;

    protected void suitePreSetUp() throws Exception
    {
        if (!derbySetupDone)
        {
            String dbName = MuleDerbyTestUtils.loadDatabaseName("derby.properties", "database.name");
            System.getProperties().put("hibernate.dbURL", "jdbc:derby:" + dbName + ";sql.enforce_strict_size=true");
            
            MuleDerbyTestUtils.defaultDerbyCleanAndInit("derby.properties", "database.name");
            derbySetupDone = true;
        }

        super.suitePreSetUp();
    }

}
