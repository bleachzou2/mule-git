/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.jdbc;

import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.transaction.TransactionCoordination;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOTransaction;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOMessageAdapter;
import org.mule.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Jdbc Message dispatcher is responsible for executing SQL queries against a
 * database.
 */
public class JdbcMessageDispatcher extends AbstractMessageDispatcher
{

    private JdbcConnector connector;
    private static final String STORED_PROCEDURE_PREFIX = "{ ";
    private static final String STORED_PROCEDURE_SUFFIX = " }";
    private static final String IN = "in";
    private static final String OUT = "out";
    private static final String INOUT = "inout";

    private Map typesMap = new HashMap();

    public JdbcMessageDispatcher(UMOImmutableEndpoint endpoint)
    {
        super(endpoint);
        this.connector = (JdbcConnector)endpoint.getConnector();
        registerType("int", Types.INTEGER);
        registerType("float", Types.FLOAT);
        registerType("double", Types.DOUBLE);
        registerType("string", Types.VARCHAR);
    }

    protected int getType(String key)
    {
        Integer type = (Integer) typesMap.get(key);
        return type != null ? type.intValue() : 0;
    }

    protected void registerType(String type, int jdbcType)
    {
        typesMap.put(type, new Integer(jdbcType));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.AbstractMessageDispatcher#doDispose()
     */
    protected void doDispose()
    {
        // template method
    }

    protected boolean isProcWithOutParams(Object[][] types)
    {
        for (int i = 0; i < types.length; i++)
        {
            String type = (String) types[i][2];
            if (INOUT.equalsIgnoreCase(type) || OUT.equalsIgnoreCase(type))
            {
                return true;
            }
        }
        return false;
    }

    protected UMOMessage executeWriteStatement(UMOEvent event, String writeStmt) throws Exception
    {
        List paramNames = new ArrayList();
        writeStmt = connector.parseStatement(writeStmt, paramNames);

        Object[][] types = connector.getParamsTypes(paramNames);

        Object[] paramValues = connector.getParams(endpoint, paramNames, new MuleMessage(
            event.getTransformedMessage()), this.endpoint.getEndpointURI().getAddress());

        UMOTransaction tx = TransactionCoordination.getInstance().getTransaction();
        Connection con = null;
        UMOMessage message = event.getMessage();
        try
        {
            con = this.connector.getConnection();
            boolean isCall = false;
            if ("call".equalsIgnoreCase(writeStmt.substring(0, 4)))
            {
                writeStmt = STORED_PROCEDURE_PREFIX + writeStmt + STORED_PROCEDURE_SUFFIX;
                isCall = true;
            }

            if (isCall && isProcWithOutParams(types))
            {
                CallableStatement statement = connector.getConnection().prepareCall(writeStmt);
                for (int i = 0; i < types.length; i++)
                {
                    String dataType = (String) types[i][1];
                    String type = (String) types[i][2];
                    if (type == null || IN.equalsIgnoreCase(type))
                    {
                        statement.setObject(i + 1, paramValues[i]);
                    }
                    else
                    {
                        statement.registerOutParameter(i + 1, getType(dataType));
                        if (INOUT.equalsIgnoreCase(type))
                        {
                            statement.setObject(i + 1, paramValues[i]);
                        }
                    }
                }
                statement.execute();
                Map result = new HashMap();
                for (int i = 0; i < types.length; i++)
                {
                    Object name = types[i][0];
                    String type = (String) types[i][2];
                    if (INOUT.equalsIgnoreCase(type) || OUT.equalsIgnoreCase(type))
                    {
                        result.put(name, statement.getObject(i + 1));
                    }
                }
                UMOMessageAdapter msgAdapter = this.connector.getMessageAdapter(result);
                message = new MuleMessage(msgAdapter);
            }
            else
            {
                int nbRows = connector.createQueryRunner().update(con, writeStmt, paramValues);
                if (nbRows != 1)
                {
                    logger.warn("Row count for write should be 1 and not " + nbRows);
                }
            }
            if (tx == null)
            {
                JdbcUtils.commitAndClose(con);
            }
            logger.debug("Event dispatched succesfuly");
        }
        catch (Exception e)
        {
            logger.debug("Error dispatching event: " + e.getMessage(), e);
            if (tx == null)
            {
                JdbcUtils.rollbackAndClose(con);
            }
            throw e;
        }
        return message;
    }
    
    protected String getStatement(UMOImmutableEndpoint endpoint)
    {
        String writeStmt = endpoint.getEndpointURI().getAddress();
        String str;
        if ((str = this.connector.getQuery(endpoint, writeStmt)) != null)
        { 
            writeStmt = str;
        }
        writeStmt = StringUtils.trimToEmpty(writeStmt);
        if (StringUtils.isBlank(writeStmt))
        {
            throw new IllegalArgumentException("Missing statement");
        }
        
        return writeStmt;
    }
    
    protected boolean isWriteStatement(String writeStmt)
    {
        if (!"insert".equalsIgnoreCase(writeStmt.substring(0, 6))
                        && !"update".equalsIgnoreCase(writeStmt.substring(0, 6))
                        && !"delete".equalsIgnoreCase(writeStmt.substring(0, 6))
                        && !"merge".equalsIgnoreCase(writeStmt.substring(0, 5))
                        && !"call".equalsIgnoreCase(writeStmt.substring(0, 4)))
        {
            return false;
        }
        
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.AbstractMessageDispatcher#doDispatch(org.mule.umo.UMOEvent)
     */
    protected void doDispatch(UMOEvent event) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Dispatch event: " + event);
        }
        
        String writeStmt = getStatement(event.getEndpoint());
        
        if (!isWriteStatement(writeStmt))
        {
            throw new IllegalArgumentException(
                "Write statement should be an insert / update / delete / merge sql statement, or a stored-procedure call");
        }
        
        this.executeWriteStatement(event, writeStmt);
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.AbstractMessageDispatcher#doSend(org.mule.umo.UMOEvent)
     */
    protected UMOMessage doSend(UMOEvent event) throws Exception
    {
        String statement = getStatement(event.getEndpoint());
        
        if (isWriteStatement(statement))
        {
            return executeWriteStatement(event, statement);
        }
        
        return doReceive(event.getTimeout(),event);
        
    }

    /**
     * Make a specific request to the underlying transport
     * 
     * @param timeout the maximum time the operation should block before returning.
     *            The call should return immediately if there is data available. If
     *            no data becomes available before the timeout elapses, null will be
     *            returned
     * @return the result of the request wrapped in a UMOMessage object. Null will be
     *         returned if no data was available
     * @throws Exception if the call to the underlying protocol causes an exception
     */
    protected UMOMessage doReceive(long timeout) throws Exception
    {
        return doReceive(timeout, null);
    }

    /**
     * Make a specific request to the underlying transport
     * Special case: The event is need when doReceive was called from doSend
     * @param timeout only for compatibility with doReceive(long timeout)
     * @param event There is a need to get params from message
     * @return the result of the request wrapped in a UMOMessage object. Null will be
     *         returned if no data was available
     * @throws Exception if the call to the underlying protocol causes an exception
     */
    protected UMOMessage doReceive(long timeout, UMOEvent event) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Trying to receive a message with a timeout of " + timeout);
        }

        String[] stmts = this.connector.getReadAndAckStatements(endpoint);
        String readStmt = stmts[0];
        String ackStmt = stmts[1];
        List readParams = new ArrayList();
        List ackParams = new ArrayList();
        readStmt = connector.parseStatement(readStmt, readParams);
        ackStmt = connector.parseStatement(ackStmt, ackParams);

        Connection con = null;
        long t0 = System.currentTimeMillis();
        try
        {
            con = this.connector.getConnection();
            if (timeout < 0)
            {
                timeout = Long.MAX_VALUE;
            }
            Object result;
            do
            {
                result = connector.createQueryRunner().query(con, readStmt,
                                                             connector.getParams(endpoint,
                                                                                 readParams,
                                                                                 event!=null ? event.getMessage() : null,
                                                                                 this.endpoint.getEndpointURI().getAddress()),
                                                             connector.createResultSetHandler());
                if (result != null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Received: " + result);
                    }
                    break;
                }
                long sleep = Math.min(this.connector.getPollingFrequency(),
                                      timeout - (System.currentTimeMillis() - t0));
                if (sleep > 0)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("No results, sleeping for " + sleep);
                    }
                    Thread.sleep(sleep);
                }
                else
                {
                    logger.debug("Timeout");
                    JdbcUtils.rollbackAndClose(con);
                    return null;
                }
            }
            while (true);
            if (ackStmt != null)
            {
                int nbRows = connector.createQueryRunner().update(con, ackStmt,
                                                                  connector.getParams(endpoint, ackParams, result, ackStmt));
                if (nbRows != 1)
                {
                    logger.warn("Row count for ack should be 1 and not " + nbRows);
                }
            }
            UMOMessageAdapter msgAdapter = this.connector.getMessageAdapter(result);
            UMOMessage message = new MuleMessage(msgAdapter);
            JdbcUtils.commitAndClose(con);
            return message;
        }
        catch (Exception e)
        {
            JdbcUtils.rollbackAndClose(con);
            throw e;
        }

    }


    protected void doConnect() throws Exception
    {
        // template method
    }

    protected void doDisconnect() throws Exception
    {
        // template method
    }

}
