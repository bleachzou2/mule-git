/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.tcp;

import org.apache.commons.pool.KeyedPoolableObjectFactory;

/**
 * The types are insufficient to describe the interface here.  What is
 * important is that {@link #makeObject(Object)} return a Socket.
 */
public interface PooledSocketFactory extends KeyedPoolableObjectFactory
{

}
