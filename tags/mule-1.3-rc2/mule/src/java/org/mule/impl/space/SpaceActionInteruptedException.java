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
package org.mule.impl.space;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;
import org.mule.umo.space.UMOSpaceException;

/**
 * Is thrown when a space action receives an <code>InterruptedException</code> from the underlying
 * space store
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class SpaceActionInteruptedException extends UMOSpaceException {

    public SpaceActionInteruptedException(String action, InterruptedException cause) {
        super(new Message(Messages.SPACE_ACTION_X_INTERRUPTED, action), cause);
    }
}
