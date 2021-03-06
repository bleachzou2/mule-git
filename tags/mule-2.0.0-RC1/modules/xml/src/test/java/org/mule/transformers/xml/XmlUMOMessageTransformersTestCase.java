/*
 * $Id:XmlUMOMessageTransformersTestCase.java 5937 2007-04-09 22:35:04Z rossmason $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformers.xml;

import org.mule.impl.MuleEvent;
import org.mule.impl.MuleMessage;
import org.mule.impl.RequestContext;
import org.mule.tck.MuleTestUtils;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.umo.UMOMessage;
import org.mule.umo.transformer.UMOTransformer;

import java.util.HashMap;
import java.util.Map;

import org.custommonkey.xmlunit.XMLAssert;

public class XmlUMOMessageTransformersTestCase extends AbstractXmlTransformerTestCase
{
    private UMOMessage testObject = null;

    // @Override
    protected void doSetUp() throws Exception
    {
        RequestContext.setEvent(new MuleEvent(testObject, getTestEndpoint("test", "sender"), MuleTestUtils
            .getTestSession(), true));
    }

    // @Override
    protected void doTearDown() throws Exception
    {
        RequestContext.clear();
    }

    public XmlUMOMessageTransformersTestCase()
    {
        Map props = new HashMap();
        props.put("object", new Apple());
        props.put("number", new Integer(1));
        props.put("string", "hello");
        testObject = new MuleMessage("test", props);
    }

    public UMOTransformer getTransformer() throws Exception
    {
        ObjectToXml t = new ObjectToXml();
        t.setAcceptUMOMessage(true);
        return t;
    }

    public UMOTransformer getRoundTripTransformer() throws Exception
    {
        return new XmlToObject();
    }

    public Object getTestData()
    {
        return testObject;
    }

    public Object getResultData()
    {
        return "<org.mule.impl.MuleMessage>\n"
               + " <adapter class=\"org.mule.providers.DefaultMessageAdapter\">\n"
               + " <message class=\"string\">test</message>\n"
               + "   <properties>\n"
               + "     <scopedMap class=\"tree-map\">\n"
               + "       <comparator class=\"org.mule.umo.provider.PropertyScope$ScopeComarator\"/>\n"
               + "       <entry>\n"
               + "         <org.mule.umo.provider.PropertyScope>\n"
               + "           <scope>invocation</scope>\n"
               + "           <order>0</order>\n"
               + "         </org.mule.umo.provider.PropertyScope>\n"
               + "         <map/>\n"
               + "       </entry>\n"
               + "       <entry>\n"
               + "         <org.mule.umo.provider.PropertyScope>\n"
               + "           <scope>inbound</scope>\n"
               + "           <order>1</order>\n"
               + "         </org.mule.umo.provider.PropertyScope>\n"
               + "         <map/>\n"
               + "       </entry>\n"
               + "       <entry>\n"
               + "         <org.mule.umo.provider.PropertyScope>\n"
               + "           <scope>outbound</scope>\n"
               + "           <order>2</order>\n"
               + "         </org.mule.umo.provider.PropertyScope>\n"
               + "         <map>\n"
               + "           <entry>\n"
               + "             <string>object</string>\n"
               + "             <org.mule.tck.testmodels.fruit.Apple>\n"
               + "               <bitten>false</bitten>\n"
               + "               <washed>false</washed>\n"
               + "             </org.mule.tck.testmodels.fruit.Apple>\n"
               + "           </entry>\n"
               + "           <entry>\n"
               + "             <string>string</string>\n"
               + "             <string>hello</string>\n"
               + "           </entry>\n"
               + "           <entry>\n"
               + "             <string>number</string>\n"
               + "             <int>1</int>\n"
               + "           </entry>\n"
               + "         </map>\n"
               + "       </entry>\n"
               + "       <entry>\n"
               + "         <org.mule.umo.provider.PropertyScope>\n"
               + "           <scope>session</scope>\n"
               + "           <order>3</order>\n"
               + "         </org.mule.umo.provider.PropertyScope>\n"
               + "         <map/>\n"
               + "       </entry>\n"
               + "     </scopedMap>\n"
               + "     <keySet class=\"tree-set\">\n"
               + "       <no-comparator/>\n"
               + "       <string>number</string>\n"
               + "       <string>object</string>\n"
               + "       <string>string</string>\n"
               + "     </keySet>\n"
               + "     <applicationProperties class=\"edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap\"/>\n"
               + "     <defaultScope reference=\"../scopedMap/entry[3]/org.mule.umo.provider.PropertyScope\"/>\n"
               + "     <fallbackToRegistry>false</fallbackToRegistry>\n" + "   </properties>\n"
               + "   <attachments class=\"edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap\"/>\n"
               + "   <encoding>UTF-8</encoding>\n" + "   <id>3be5fe5a-87f8-11dc-a153-0b6db396665f</id>\n"
               + " </adapter>\n" + " </org.mule.impl.MuleMessage>\n";
    }

    /**
     * Different JVMs serialize fields to XML in a different order, in which case we
     * need to check for the actual contents. We reconstruct the UMOMessages from the
     * generated XML and compare them as objects.
     */
    // @Override
    public boolean compareResults(Object expected, Object result)
    {
        if (!super.compareResults(expected, result))
        {
            // apparently the generic XML comparison did not work, so check again;
            // this is currently the case when running on Mustang
            try
            {
                XMLAssert.assertXpathEvaluatesTo("3", "count(//adapter/properties/scopedMap/entry/map/entry)", (String)result);
                // depending on your VM and the phase of the moon this is either "number" or "object"
                XMLAssert.assertXpathEvaluatesTo("number", "//adapter/properties/scopedMap/entry/map/entry/string/text()", (String)result);
                XMLAssert.assertXpathEvaluatesTo("false", "//adapter/properties/scopedMap/entry/map/entry/org.mule.tck.testmodels.fruit.Apple/bitten", (String)result);
            }
            catch (Exception ex)
            {
                fail(ex.getMessage());
            }
        }

        return true;
    }

    // @Override
    public boolean compareRoundtripResults(Object expected, Object result)
    {
        if (expected == null && result == null)
        {
            return true;
        }

        if (expected == null || result == null)
        {
            return false;
        }

        if (expected instanceof UMOMessage && result instanceof UMOMessage)
        {
            return ((UMOMessage)expected).getPayload().equals(((UMOMessage)result).getPayload())
                            && ((UMOMessage)expected).getProperty("object").equals(
                                ((UMOMessage)result).getProperty("object"))
                            && ((UMOMessage)expected).getProperty("string").equals(
                                ((UMOMessage)result).getProperty("string"))
                            && ((UMOMessage)expected).getIntProperty("number", -1) == ((UMOMessage)result)
                                .getIntProperty("number", -2);
        }
        else
        {
            return false;
        }
    }

}
