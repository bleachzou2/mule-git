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
package org.mule.routing.filters.logic;

import org.mule.umo.UMOFilter;

/**
 * <code>NotFilter</code> accepts if the filter does not accept
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class NotFilter implements UMOFilter
{
    private UMOFilter filter;

    public NotFilter()
    {
    }

    public NotFilter(UMOFilter filter)
    {
        this.filter = filter;
    }


    public UMOFilter getFilter()
    {
        return filter;
    }

    public void setFilter(UMOFilter filter)
    {
        this.filter = filter;
    }


    public boolean accept(Object object)
    {
        return !filter.accept(object);
    }
}
