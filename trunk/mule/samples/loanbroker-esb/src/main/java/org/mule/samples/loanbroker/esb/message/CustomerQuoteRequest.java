/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.samples.loanbroker.esb.message;

import java.io.Serializable;

/**
 * <code>CustomerQuoteRequest</code> is the request sent by the the LoanBroker
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class CustomerQuoteRequest implements Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 6365612435470800746L;

    /** The customer that requested the quote */
    private Customer customer;

    /** The requested loan Amount */
    private double loanAmount;

    /** the duration of the loan */
    private int loanDuration;

    public CustomerQuoteRequest(Customer customer, double loanAmount, int loanDuration)
    {
        this.customer = customer;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public double getLoanAmount()
    {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount)
    {
        this.loanAmount = loanAmount;
    }

    public int getLoanDuration()
    {
        return loanDuration;
    }

    public void setLoanDuration(int loanDuration)
    {
        this.loanDuration = loanDuration;
    }
}
