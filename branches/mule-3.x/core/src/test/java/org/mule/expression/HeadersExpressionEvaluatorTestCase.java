/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.expression;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.expression.ExpressionRuntimeException;
import org.mule.routing.CorrelationPropertiesExpressionEvaluator;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.UUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadersExpressionEvaluatorTestCase extends AbstractMuleTestCase
{
    private Map props;

    @Override
    public void doSetUp()
    {
        props = new HashMap(3);
        props.put("foo", "moo");
        props.put("bar", "mar");
        props.put("baz", "maz");
    }

    public void testSingleHeader() throws Exception
    {
        MessageHeaderExpressionEvaluator eval = new MessageHeaderExpressionEvaluator();
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = eval.evaluate("foo", message);
        assertNotNull(result);
        assertEquals("moo", result);

        // Value not required + found
        result = eval.evaluate("foo?", message);
        assertNotNull(result);
        assertEquals("moo", result);

        // Value not required + not found
        result = eval.evaluate("fool?", message);
        assertNull(result);

        // Value required + not found (throws exception)
        try
        {
            result = eval.evaluate("fool", message);
            fail("required value");
        }
        catch (Exception e)
        {
            //Expected
        }
    }

    public void testMapHeaders() throws Exception
    {
        MessageHeadersExpressionEvaluator eval = new MessageHeadersExpressionEvaluator();

        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = eval.evaluate("foo, baz", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(2, ((Map)result).size());
        assertTrue(((Map)result).values().contains("moo"));
        assertTrue(((Map)result).values().contains("maz"));
        assertFalse(((Map)result).values().contains("mar"));

        // Value not required + found
        result = eval.evaluate("foo?, baz", message);
        assertNotNull(result);        
        assertTrue(result instanceof Map);
        assertEquals(2, ((Map)result).size());
        assertTrue(((Map)result).values().contains("moo"));
        assertTrue(((Map)result).values().contains("maz"));
        assertFalse(((Map)result).values().contains("mar"));
        
        // Value not required + not found
        result = eval.evaluate("fool?", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(0, ((Map)result).size());

        // Value required + not found (throws exception)
        try
        {
            result = eval.evaluate("fool", message);
            fail("required value");
        }
        catch (Exception e)
        {
            //Expected
        }

        //Test count
        assertEquals(3, eval.evaluate("{count}", message));

        //Test all
        result = eval.evaluate("*", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(3, ((Map)result).size());
        assertTrue(((Map)result).values().contains("moo"));
        assertTrue(((Map)result).values().contains("maz"));
        assertTrue(((Map)result).values().contains("mar"));
    }

    public void testListHeaders() throws Exception
    {
        MessageHeadersListExpressionEvaluator eval = new MessageHeadersListExpressionEvaluator();
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = eval.evaluate("foo, baz", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(2, ((List)result).size());
        assertTrue(((List)result).contains("moo"));
        assertTrue(((List)result).contains("maz"));
        // redundant since we already know that the map is of size 2
        assertFalse(((List)result).contains("mar"));

        // Value not required + found
        result = eval.evaluate("foo?, baz", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(2, ((List)result).size());
        assertTrue(((List)result).contains("moo"));
        assertTrue(((List)result).contains("maz"));
        // redundant since we already know that the map is of size 2
        assertFalse(((List)result).contains("mar"));        

        // Value not required + not found
        result = eval.evaluate("fool?", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(0, ((List)result).size());

        // Value required + not found (throws exception)
        try
        {
            result = eval.evaluate("fool", message);
            fail("required value");
        }
        catch (Exception e)
        {
            //Expected
        }
        
        //Test All
        result = eval.evaluate("*", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(3, ((List)result).size());
        assertTrue(((List)result).contains("moo"));
        assertTrue(((List)result).contains("maz"));
        assertTrue(((List)result).contains("mar"));
    }


    public void testSingleHeaderUsingManager() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = muleContext.getExpressionManager().evaluate("#[header:foo]", message);
        assertNotNull(result);
        assertEquals("moo", result);

        // Value not required + found
        result = muleContext.getExpressionManager().evaluate("#[header:foo?]", message);
        assertNotNull(result);
        assertEquals("moo", result);

        // Value not required + not found
        result = muleContext.getExpressionManager().evaluate("#[header:fool?]", message);
        assertNull(result);

        // Value required + not found (throws exception)
        try
        {
            result = muleContext.getExpressionManager().evaluate("#[header:fool]", message);
            fail("Required value");
        }
        catch (ExpressionRuntimeException e)
        {
            //expected
        }

    }

    public void testMapHeadersUsingManager() throws Exception
    {

        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = muleContext.getExpressionManager().evaluate("#[headers:foo, baz]", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(2, ((Map)result).size());
        assertTrue(((Map)result).values().contains("moo"));
        assertTrue(((Map)result).values().contains("maz"));
        assertFalse(((Map)result).values().contains("mar"));

        // Value not required + found
        result = muleContext.getExpressionManager().evaluate("#[headers:foo?, baz]", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(2, ((Map)result).size());
        assertTrue(((Map)result).values().contains("moo"));
        assertTrue(((Map)result).values().contains("maz"));
        assertFalse(((Map)result).values().contains("mar"));
        
        // Value not required + not found
        result = muleContext.getExpressionManager().evaluate("#[headers:fool?]", message);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        assertEquals(0, ((Map)result).size());

        // Value required + not found (throws exception)
        try
        {
            result = muleContext.getExpressionManager().evaluate("#[headers:fool]", message);
            fail("Required value");
        }
        catch (ExpressionRuntimeException e)
        {
            //expected
        }

        assertEquals(3, muleContext.getExpressionManager().evaluate("#[headers:{count}]", message));
    }

    public void testListHeadersUsingManager() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);

        // Value required + found
        Object result = muleContext.getExpressionManager().evaluate("#[headers-list:foo, baz]", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(2, ((List)result).size());
        assertTrue(((List)result).contains("moo"));
        assertTrue(((List)result).contains("maz"));
        assertFalse(((List)result).contains("mar"));

        // Value not required + found
        result = muleContext.getExpressionManager().evaluate("#[headers-list:foo?, baz]", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(2, ((List)result).size());
        assertTrue(((List)result).contains("moo"));
        assertTrue(((List)result).contains("maz"));
        assertFalse(((List)result).contains("mar"));        

        // Value not required + not found
        result = muleContext.getExpressionManager().evaluate("#[headers-list:fool?]", message);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(0, ((List)result).size());

        // Value required + not found (throws exception)
        try
        {
            result = muleContext.getExpressionManager().evaluate("#[headers-list:fool]", message);
            fail("Required value");
        }
        catch (ExpressionRuntimeException e)
        {
            //expected
        }
    }
    
    public void testCorrelationManagerCorrelationId()
    {
        CorrelationPropertiesExpressionEvaluator evaluator = new CorrelationPropertiesExpressionEvaluator();
        String correlationId = UUID.getUUID();
        
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);
        message.setCorrelationId(correlationId);
        
        Object result = evaluator.evaluate(MuleProperties.MULE_CORRELATION_ID_PROPERTY, message);
        assertNotNull(result);
        assertEquals(correlationId, result);
    }
    
    public void testCorrelationManagerNullResult()
    {
        CorrelationPropertiesExpressionEvaluator evaluator = new CorrelationPropertiesExpressionEvaluator();
        
        DefaultMuleMessage message = new DefaultMuleMessage("test", props, muleContext);
        message.setUniqueId(null);
        
        try
        {
            evaluator.evaluate(MuleProperties.MULE_CORRELATION_ID_PROPERTY, message);
            fail("Null result on CorrelationPropertiesExpressionEvaluator must throw");
        }
        catch (IllegalArgumentException iae)
        {
            // this one was expected
        }
    }    
    
    public void testCorrelationManagerUniqueId()
    {
        CorrelationPropertiesExpressionEvaluator evaluator = new CorrelationPropertiesExpressionEvaluator();
        
        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);        
        Object result = evaluator.evaluate(MuleProperties.MULE_MESSAGE_ID_PROPERTY, message);
        assertNotNull(result);
        assertEquals(message.getUniqueId(), result);
    }
    
//    public void testCorrelationManagerNullInput()
//    {
//        CorrelationPropertiesExpressionEvaluator evaluator = new CorrelationPropertiesExpressionEvaluator();
//        evaluator.evaluate(MuleProperties.MULE_CORRELATION_ID_PROPERTY, null);
//    }
    
    public void testCorrelationManagerInvalidKey()
    {
        CorrelationPropertiesExpressionEvaluator evaluator = new CorrelationPropertiesExpressionEvaluator();

        MuleMessage message = new DefaultMuleMessage("test", props, muleContext);
        try
        {
            evaluator.evaluate("invalid-key", message);
            fail("invalid key on CorrelationPropertiesExpressionEvaluator must fail");
        }
        catch (IllegalArgumentException iax)
        {
            // this one was expected
        }
    }
}
