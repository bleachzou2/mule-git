/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.samples.loanbroker.service;

import org.mule.samples.loanbroker.BankQuoteRequest;

/**
 * <code>CreditAgencyService</code> the service that provides a credit score for a
 * customer.
 */
public interface CreditAgencyService
{
    BankQuoteRequest getCreditProfile(BankQuoteRequest request);
}
