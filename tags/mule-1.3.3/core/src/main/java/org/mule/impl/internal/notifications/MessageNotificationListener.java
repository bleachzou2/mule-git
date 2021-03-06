/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl.internal.notifications;

import org.mule.umo.manager.UMOServerNotificationListener;

/**
 * <code>MessageNotificationListener</code> is an observer interface that objects
 * can use to receive notifications about messages being sent and received from a
 * Mule Server
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public interface MessageNotificationListener extends UMOServerNotificationListener
{
    // no methods
}
