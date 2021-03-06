/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.umo.retry;

/**
 * A factory responsible for creating a policy.  Custom policies should
 * Implement this factory and provide a private {@link org.mule.umo.retry.UMOTemplatePolicy}
 * class within this class.  The factory is the object that actually gets configured then new
 * {@link org.mule.umo.retry.UMOTemplatePolicy} objects are create each time using the configuration
 * on the factory. 
 */
public interface UMOPolicyFactory
{
    UMOTemplatePolicy create();

    boolean isConnectAsynchronously();
}
