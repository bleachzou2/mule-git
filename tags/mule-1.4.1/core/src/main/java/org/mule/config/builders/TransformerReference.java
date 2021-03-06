/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.builders;

import org.mule.config.i18n.CoreMessages;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.transformer.UMOTransformer;
import org.mule.util.MuleObjectHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TransformerReference</code> maintains a transformer reference.
 * Transformers are clones when they are looked up, if there are container properties
 * set on the transformer the clone will have an inconsistent state if container
 * properties have not been resolved. This class holds the refernece and is invoked
 * after the container properties are resolved.
 */
public class TransformerReference
{
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(TransformerReference.class);

    private final String propertyName;
    private final String transformerName;
    private final Object object;

    public TransformerReference(String propertyName, String transformerName, Object object)
    {
        this.propertyName = propertyName;
        this.transformerName = transformerName;
        this.object = object;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getTransformerName()
    {
        return transformerName;
    }

    public Object getObject()
    {
        return object;
    }

    public void resolveTransformer() throws InitialisationException
    {
        UMOTransformer trans = null;
        try
        {
            trans = MuleObjectHelper.getTransformer(transformerName, " ");
            if (trans == null)
            {
                throw new InitialisationException(
                    CoreMessages.objectNotRegisteredWithManager("Transformer '" + transformerName + "'"), object);
            }
            logger.info("Setting transformer: " + transformerName + " on " + object.getClass().getName()
                        + "." + propertyName);

            BeanUtils.setProperty(object, propertyName, trans);
        }
        catch (InitialisationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new InitialisationException(
                CoreMessages.cannotSetPropertyOnObjectWithParamType(propertyName, 
                    object.getClass(), trans.getClass()), e);
        }
    }
}
