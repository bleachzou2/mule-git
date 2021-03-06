/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.lang.SystemUtils;
import org.mule.util.CollectionUtils;
import org.mule.util.StringUtils;

public class CollectionUtilsTestCase extends TestCase
{

    public void testToStringNull() throws Exception
    {
        Collection c = null;
        assertEquals("[]", CollectionUtils.toString(c, false));
        assertEquals("[]", CollectionUtils.toString(c, true));
    }

    public void testToStringEmpty() throws Exception
    {
        Collection c = new ArrayList();
        assertEquals("[]", CollectionUtils.toString(c, false));
        assertEquals("[]", CollectionUtils.toString(c, true));
    }

    public void testToStringSingleElement() throws Exception
    {
        Collection c = Arrays.asList(new Object[]{"foo"});

        assertEquals("[foo]", CollectionUtils.toString(c, false));
        assertEquals("[" + SystemUtils.LINE_SEPARATOR + "foo" + SystemUtils.LINE_SEPARATOR + "]",
            CollectionUtils.toString(c, true));
    }

    public void testToStringMultipleElements() throws Exception
    {
        Collection c = Arrays.asList(new Object[]{"foo", this.getClass()});

        assertEquals("[foo, " + this.getClass().getName() + "]", CollectionUtils.toString(c, false));

        assertEquals("[" + SystemUtils.LINE_SEPARATOR + "foo" + SystemUtils.LINE_SEPARATOR
                     + this.getClass().getName() + SystemUtils.LINE_SEPARATOR + "]",
            CollectionUtils.toString(c, true));
    }

    public void testToStringTooManyElements()
    {
        Collection test = new ArrayList(100);
        for (int i = 0; i < 100; i++)
        {
            test.add(new Integer(i));
        }

        // the String will contain not more than exactly MAX_ARRAY_LENGTH elements
        String result = CollectionUtils.toString(test, 10);
        assertTrue(result.endsWith("[..]]"));
        assertEquals(9, StringUtils.countMatches(result, ","));
    }

}
