/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.transaction;

import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.jdbc.JdbcUtils;
import org.mule.transport.jms.JmsConnector;
import org.mule.transport.jms.JmsConstants;
import org.mule.transport.jms.activemq.ActiveMQJmsConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class XAJdbcMule1479TestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "org/mule/test/integration/transaction/jdbc-xatransaction-1479.xml";
    }

    protected void doPostFunctionalSetUp() throws Exception
    {
        emptyTable();
    }

    protected void emptyTable() throws Exception
    {
        try
        {
            execSqlUpdate("DELETE FROM TEST");
        }
        catch (Exception e)
        {
            execSqlUpdate("CREATE TABLE TEST(ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0)  NOT NULL PRIMARY KEY,DATA VARCHAR(255))");
        }
    }

    protected Connection getConnection() throws Exception
    {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        return DriverManager.getConnection("jdbc:derby:muleEmbeddedDB;create=true");
    }

    protected List execSqlQuery(String sql) throws Exception
    {
        Connection con = null;
        try
        {
            con = getConnection();
            return (List)new QueryRunner().query(con, sql, new ArrayListHandler());
        }
        finally
        {
            JdbcUtils.close(con);
        }
    }

    protected int execSqlUpdate(String sql) throws Exception
    {
        Connection con = null;
        try
        {
            con = getConnection();
            return new QueryRunner().update(con, sql);
        }
        finally
        {
            JdbcUtils.close(con);
        }
    }

/*
    public void testJdbcXa() throws Exception
    {

        MuleClient client = new MuleClient();

        client.dispatch("vm://in","test",null);

        List results = execSqlQuery("SELECT * FROM TEST");
        assertEquals(0, results.size());
    }    
*/

    public void testJmsXa() throws Exception
    {

        MuleClient client = new MuleClient();

        client.dispatch("vm://in1","test",null);

        logger.debug("########### receiving message");

//        MuleMessage res = client.receive("jms://queue.out", 1000);
//        assertNotNull(res);

        Thread.sleep(5000000);

        List results = execSqlQuery("SELECT * FROM TEST");
        assertEquals(1, results.size());
    }

    public JmsConnector createConnector() throws Exception
    {
        ActiveMQJmsConnector connector = new ActiveMQJmsConnector();
        connector.setSpecification(JmsConstants.JMS_SPECIFICATION_11);
        connector.setName("myConnector");
        connector.getDispatcherThreadingProfile().setDoThreading(false);
        return connector;
    }
    

}
