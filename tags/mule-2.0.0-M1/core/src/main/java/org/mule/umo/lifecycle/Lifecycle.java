/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.umo.lifecycle;

/**
 * <code>LifecyclePhase</code> adds lifecycle methods <code>start</code>,
 * <code>stop</code> and <code>dispose</code>.
 */
public interface Lifecycle extends Startable, Stoppable, Disposable
{
    // no additional methods - see super interfaces
}
