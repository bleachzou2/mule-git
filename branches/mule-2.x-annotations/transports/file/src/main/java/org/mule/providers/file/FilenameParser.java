/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.file;

import org.mule.umo.provider.UMOMessageAdapter;

/**
 * <code>FilenameParser</code> is a simple expression parser interface for
 * processing filenames
 */
public interface FilenameParser
{
    public String getFilename(UMOMessageAdapter adapter, String pattern);
}
