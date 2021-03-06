/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.spring.parsers.specific;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

/**
 * Generic router definition parser for parsing all Router elements.
 */
public class RouterDefinitionParser extends ChildDefinitionParser
{

    public RouterDefinitionParser(String setterMethod, Class clazz)
    {
        super(setterMethod, clazz);
        addMapping("enableCorrelation", "IF_NOT_SET=0,ALWAYS=1,NEVER=2");
        addAlias("transformers", "transformer");
    }

}
