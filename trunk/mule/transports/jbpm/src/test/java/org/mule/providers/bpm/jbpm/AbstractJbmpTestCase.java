/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.bpm.jbpm;

import org.mule.providers.bpm.tests.AbstractBpmTestCase;
import org.mule.util.MuleDerbyTestUtils;

import java.io.File;

public abstract class AbstractJbmpTestCase extends AbstractBpmTestCase
{
    private static boolean derbySetupDone = false;

    protected void suitePreSetUp() throws Exception
    {
        if (!derbySetupDone)
        {
            String propertiesFileLocation = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "derby.properties";
            String dbName = MuleDerbyTestUtils.loadDatabaseName(propertiesFileLocation, "database.name");
            System.getProperties().put("hibernate.dbURL", "jdbc:derby:" + dbName + ";sql.enforce_strict_size=true");
            MuleDerbyTestUtils.defaultDerbyCleanAndInit(propertiesFileLocation, "database.name");
            derbySetupDone = true;
        }

        super.suitePreSetUp();
    }

}
