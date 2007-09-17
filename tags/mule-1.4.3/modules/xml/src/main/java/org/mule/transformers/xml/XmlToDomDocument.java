/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformers.xml;

import org.mule.umo.transformer.TransformerException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;

/**
 * <code>XmlToDomDocument</code> transforms a XML String to org.w3c.dom.Document.
 */
public class XmlToDomDocument extends AbstractXmlTransformer
{

    public XmlToDomDocument()
    {
        registerSourceType(String.class);
        registerSourceType(byte[].class);
    }

    public Object doTransform(Object src, String encoding) throws TransformerException
    {
        try
        {
            Source sourceDoc = getXmlSource(src);
            if (sourceDoc == null) return null;

            // If returnClass is not set, assume W3C DOM
            // This is the original behaviour
            ResultHolder holder = getResultHolder(returnClass);
            if (holder == null) holder = getResultHolder(Document.class);

            Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
            idTransformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            idTransformer.transform(sourceDoc, holder.getResult());

            return holder.getResultObject();
        }
        catch (Exception e)
        {
            throw new TransformerException(this, e);
        }
    }
}
