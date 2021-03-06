/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.testmodels.fruit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.Callable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orange implements Fruit, Callable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 2556604671068150589L;

    /**
     * logger used by this class
     */
    private static Log logger = LogFactory.getLog(Orange.class);

    private boolean bitten = false;
    private Integer segments = new Integer(10);
    private Double radius = new Double(4.34);
    private String brand;

    private Map mapProperties;

    private List listProperties;

    private List arrayProperties;

    public Orange()
    {
        super();
    }

    public Orange(Integer segments, Double radius, String brand)
    {
        super();
        this.segments = segments;
        this.radius = radius;
        this.brand = brand;
    }

    public Orange(HashMap props) throws UMOException
    {
        setBrand((String)props.get("brand"));
        setRadius((Double)props.get("radius"));
        setSegments((Integer)props.get("segments"));
    }

    public void bite()
    {
        bitten = true;
    }

    public boolean isBitten()
    {
        return bitten;
    }

    public Object onCall(UMOEventContext context) throws UMOException
    {
        logger.debug("Orange received an event in UMOCallable.onEvent! Event says: "
                     + context.getMessageAsString());
        bite();
        return null;
    }

    /**
     * @return
     */
    public String getBrand()
    {
        return brand;
    }

    /**
     * @return
     */
    public Integer getSegments()
    {
        return segments;
    }

    /**
     * @return
     */
    public Double getRadius()
    {
        return radius;
    }

    /**
     * @param string
     */
    public void setBrand(String string)
    {
        brand = string;
    }

    /**
     * @param integer
     */
    public void setSegments(Integer integer)
    {
        segments = integer;
    }

    /**
     * @param double1
     */
    public void setRadius(Double double1)
    {
        radius = double1;
    }

    /**
     * @return Returns the listProperties.
     */
    public List getListProperties()
    {
        return listProperties;
    }

    /**
     * @param listProperties The listProperties to set.
     */
    public void setListProperties(List listProperties)
    {
        this.listProperties = listProperties;
    }

    /**
     * @return Returns the mapProperties.
     */
    public Map getMapProperties()
    {
        return mapProperties;
    }

    /**
     * @param mapProperties The mapProperties to set.
     */
    public void setMapProperties(Map mapProperties)
    {
        this.mapProperties = mapProperties;
    }

    /**
     * @return Returns the arrayProperties.
     */
    public List getArrayProperties()
    {
        return arrayProperties;
    }

    /**
     * @param arrayProperties The arrayProperties to set.
     */
    public void setArrayProperties(List arrayProperties)
    {
        this.arrayProperties = arrayProperties;
    }

    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (bitten ? 1231 : 1237);
        result = PRIME * result + ((brand == null) ? 0 : brand.hashCode());
        result = PRIME * result + ((radius == null) ? 0 : radius.hashCode());
        result = PRIME * result + ((segments == null) ? 0 : segments.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Orange other = (Orange)obj;
        if (bitten != other.bitten) return false;
        if (brand == null)
        {
            if (other.brand != null) return false;
        }
        else if (!brand.equals(other.brand)) return false;
        if (radius == null)
        {
            if (other.radius != null) return false;
        }
        else if (!radius.equals(other.radius)) return false;
        if (segments == null)
        {
            if (other.segments != null) return false;
        }
        else if (!segments.equals(other.segments)) return false;
        return true;
    }

}
