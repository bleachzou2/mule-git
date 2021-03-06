/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.jms.weblogic;

import org.mule.providers.jms.DefaultJmsTopicResolver;
import org.mule.providers.jms.Jms102bSupport;
import org.mule.providers.jms.JmsConnector;
import org.mule.util.ClassUtils;

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Weblogic-specific JMS topic resolver. Will use reflection and
 * a vendor API to detect topics.
 */
public class WeblogicJmsTopicResolver extends DefaultJmsTopicResolver
{
    /**
     * Cached empty class array, used in the no-args reflective method call.
     */
    protected static final Class[] PARAMETER_TYPES_NONE = new Class[0];

    /**
     * Create an instance of the resolver.
     *
     * @param connector owning connector
     */
    public WeblogicJmsTopicResolver(final JmsConnector connector)
    {
        super(connector);
    }


    /**
     * For Weblogic 8.x (JMS 1.0.2b) will use Weblogic-specific API call to test for topic.
     * For Weblogic 9.x and later (JMS 1.1) this call is not required due to the unified
     * messaging domains.
     *
     * @param destination a jms destination to test
     * @return {@code true} if the destination is a topic
     */
    public boolean isTopic(final Destination destination)
    {
        // don't check the invariants, we already handle Weblogic's case here

        boolean topic = destination instanceof Topic;

        if (topic && destination instanceof Queue &&
            getConnector().getJmsSupport() instanceof Jms102bSupport)
        {
            try
            {
                Method topicMethod = ClassUtils.getPublicMethod(destination.getClass(), "isTopic", PARAMETER_TYPES_NONE);

                topic = ((Boolean) topicMethod.invoke(destination, ClassUtils.NO_ARGS)).booleanValue();
            }
            catch (Exception e)
            {
                logger.warn(e.getMessage()); 
            }
        }
        return topic;
    }

}
