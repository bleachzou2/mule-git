/* 
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 * 
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk 
 * 
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file. 
 *
 */

package org.mule;

import org.mule.umo.UMOException;

/**
 * <code>InitialisationException</code> TODO (document class)
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class InitialisationException extends UMOException
{

    /**
     * @param message
     */
    public InitialisationException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InitialisationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
