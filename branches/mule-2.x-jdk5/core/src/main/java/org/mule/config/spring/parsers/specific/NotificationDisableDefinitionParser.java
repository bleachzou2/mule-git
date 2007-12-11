 /*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers.specific;

import org.mule.config.spring.parsers.collection.ChildListEntryDefinitionParser;
import org.mule.config.spring.parsers.processors.CheckExclusiveAttributes;
import org.mule.config.spring.parsers.processors.CheckRequiredAttributes;
import org.mule.config.spring.parsers.delegate.AttributeSelectionDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

public class NotificationDisableDefinitionParser extends AttributeSelectionDefinitionParser
{

    public static final String DISABLED_EVENT = "disabledEvent";
    public static final String DISABLED_INTERFACE = "disabledInterface";

    public NotificationDisableDefinitionParser()
    {
        super(NotificationDefinitionDefinitionParser.EVENT,
                new ChildListEntryDefinitionParser(DISABLED_EVENT,
                        NotificationDefinitionDefinitionParser.EVENT)
                        .addMapping(NotificationDefinitionDefinitionParser.EVENT,
                        NotificationDefinitionDefinitionParser.EVENT_MAP));
        addDelegate(NotificationDefinitionDefinitionParser.EVENT_CLASS,
                new ChildListEntryDefinitionParser(DISABLED_EVENT,
                        NotificationDefinitionDefinitionParser.EVENT_CLASS));
        addDelegate(NotificationDefinitionDefinitionParser.INTERFACE,
                new ChildListEntryDefinitionParser(DISABLED_INTERFACE,
                        NotificationDefinitionDefinitionParser.INTERFACE)
                        .addMapping(NotificationDefinitionDefinitionParser.INTERFACE,
                        NotificationDefinitionDefinitionParser.INTERFACE_MAP));
        addDelegate(NotificationDefinitionDefinitionParser.INTERFACE_CLASS,
                new ChildListEntryDefinitionParser(DISABLED_INTERFACE,
                        NotificationDefinitionDefinitionParser.INTERFACE_CLASS));
        registerPreProcessor(new CheckExclusiveAttributes(NotificationDefinitionDefinitionParser.ALL_ATTRIBUTES));
        registerPreProcessor(new CheckRequiredAttributes(NotificationDefinitionDefinitionParser.ALL_ATTRIBUTES));
    }

}
