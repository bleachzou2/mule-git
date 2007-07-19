/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.umo;

import org.mule.transaction.constraints.ConstraintFilter;

/**
 * <code>UMOTransactionConfig</code> defines transaction configuration for a
 * transactional endpoint.
 */
public interface UMOTransactionConfig
{
    byte ACTION_NONE = 0;
    byte ACTION_ALWAYS_BEGIN = 1;
    byte ACTION_BEGIN_OR_JOIN = 2;
    byte ACTION_ALWAYS_JOIN = 3;
    byte ACTION_JOIN_IF_POSSIBLE = 4;

    UMOTransactionFactory getFactory();

    void setFactory(UMOTransactionFactory factory);

    byte getAction();

    void setAction(byte action);

    boolean isTransacted();

    ConstraintFilter getConstraint();

    void setConstraint(ConstraintFilter constraint);

    void setTimeout(int timeout);

    int getTimeout();
}
