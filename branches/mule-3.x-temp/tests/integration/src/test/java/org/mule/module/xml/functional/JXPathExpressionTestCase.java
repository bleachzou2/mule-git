/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.xml.functional;

import java.text.MessageFormat;
import java.util.Properties;


public class JXPathExpressionTestCase extends AbstractXmlPropertyExtractorTestCase
{

    public static final String MESSAGE = "<endpoint>{0}</endpoint>";

    public JXPathExpressionTestCase()
    {
        super(true);
    }

    protected Properties getStartUpProperties()
    {
        Properties p = new Properties();
        p.setProperty("selector.expression", "/endpoint");
        p.setProperty("selector.evaluator", "jxpath");

        return p;
    }

    protected Object getMatchMessage() throws Exception
    {
        return documentFor("matchingEndpoint1");
    }

    protected Object getErrorMessage() throws Exception
    {
        return documentFor("missingEndpoint");
    }

    protected String documentFor(String name) throws Exception
    {
        return MessageFormat.format(MESSAGE, name);
    }

}
