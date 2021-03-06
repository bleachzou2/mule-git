/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.service;

import org.mule.api.MuleException;

/**
 * Adds a pause lifecycle method to an object.  This should only be used with {@link Resumable}
 */
public interface Pausable
{
    public static final String PHASE_NAME = "pause";

    void pause() throws MuleException;
}
