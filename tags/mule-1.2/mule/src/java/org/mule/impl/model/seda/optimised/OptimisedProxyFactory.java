/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.impl.model.seda.optimised;

import org.mule.config.pool.CommonsPoolProxyPool;
import org.mule.config.pool.AbstractProxyFactory;
import org.mule.config.pool.CommonsPoolProxyFactory;
import org.mule.impl.MuleDescriptor;
import org.mule.util.ObjectPool;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.Callable;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Creates an optimised Mule proxy for pooling which does away with the
 * replection and inteception on objects
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class OptimisedProxyFactory extends CommonsPoolProxyFactory
 {
    public OptimisedProxyFactory(MuleDescriptor descriptor) {
        super(descriptor);
    }

    protected Object createProxy(Object component) throws UMOException {
        if(!(component instanceof Callable)) {
            throw new IllegalArgumentException("Components for the Optimised Mule proxy must implement: " + Callable.class.getName());
        }
        return new OptimisedMuleProxy((Callable)component, descriptor, pool);
    }
}
