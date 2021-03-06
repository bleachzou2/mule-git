/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl.space;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;
import org.mule.umo.space.UMOSpaceException;

/**
 * Is thrown if an exception is thrown during the creation of a space.
 */
public class CreateSpaceException extends UMOSpaceException
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 6138791159382128699L;

    public CreateSpaceException(Throwable cause)
    {
        super(new Message(Messages.SPACE_FAILED_TO_CREATE), cause);
    }
}
