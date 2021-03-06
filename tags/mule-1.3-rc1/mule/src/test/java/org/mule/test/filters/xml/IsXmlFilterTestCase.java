package org.mule.test.filters.xml;

import org.mule.impl.MuleMessage;
import org.mule.routing.filters.xml.IsXmlFilter;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.Utility;

/**
 * @author <a href="mailto:carlson@hotpop.com">Travis Carlson</a>
 */
public class IsXmlFilterTestCase extends AbstractMuleTestCase {

	IsXmlFilter filter;

    protected void doSetUp() throws Exception {
    	filter = new IsXmlFilter();
    }

    public void testFilterFalse() throws Exception {
        assertFalse(filter.accept(new MuleMessage("This is definitely not XML.")));
    }

    public void testFilterFalse2() throws Exception {
        assertFalse(filter.accept(new MuleMessage("<line>This is almost XML</line><line>This is almost XML</line>")));
    }

    public void testFilterTrue() throws Exception {
        assertTrue(filter.accept(new MuleMessage("<msg attrib=\"att1\">This is some nice XML!</msg>")));
    }

    public void testFilterBytes() throws Exception {
    	byte[] bytes = "<msg attrib=\"att1\">This is some nice XML!</msg>".getBytes();
        assertTrue(filter.accept(new MuleMessage(bytes)));
    }

    public void testFilterNull() throws Exception {
        assertFalse(filter.accept(new MuleMessage(null)));
    }

    public void testFilterLargeXml() throws Exception {
        assertTrue(filter.accept(new MuleMessage(Utility.fileToString("src/test/conf/cdcatalog.xml"))));
    }

    public void testFilterLargeXmlFalse() throws Exception {
        assertTrue(filter.accept(new MuleMessage(Utility.fileToString("src/test/conf/cdcatalog.html"))));
    }
}
