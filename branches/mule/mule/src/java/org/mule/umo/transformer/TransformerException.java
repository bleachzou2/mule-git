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
package org.mule.umo.transformer;

import org.mule.umo.UMOException;

/**
 * <code>TransformerException</code> is a simple exception that is thrown by
 * transformers.
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */

public class TransformerException extends UMOException
{
    /**
     * @param message the exception message
     */
    public TransformerException(String message)
    {
        super(message);
    }

    /**
     * @param message the exception message
     * @param cause   the exception that caused this exception to be thrown
     */
    public TransformerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
