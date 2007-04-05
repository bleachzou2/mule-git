/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.samples.loanbroker;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.impl.UMODescriptorAware;
import org.mule.samples.loanbroker.service.BankService;
import org.mule.umo.UMODescriptor;

/**
 * <code>Bank</code> is a representation of a bank from which to obtain loan
 * quotes.
 */

public class Bank implements BankService, UMODescriptorAware, Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 2576893239001689631L;

    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(Bank.class);

    private String bankName;
    private String endpoint = "";
    private double primeRate;

    public Bank()
    {
        this.primeRate = Math.random() * 10;
    }

    public Bank(String bankname, String endpoint)
    {
        this();
        this.bankName = bankname;
        this.endpoint = endpoint;
    }

    public void setDescriptor(UMODescriptor descriptor)
    {
        this.bankName = descriptor.getName();
    }

    public LoanQuote getLoanQuote(BankQuoteRequest request)
    {
        LoanQuote quote = new LoanQuote();
        quote.setBankName(getBankName());
        quote.setInterestRate(primeRate);
        logger.info("Returning Rate is:" + quote);
        return quote;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public double getPrimeRate()
    {
        return primeRate;
    }

    public void setPrimeRate(double primeRate)
    {
        this.primeRate = primeRate;
    }

}
