package com.wormtrader.client;
/*
 * @(#)ContractDetailsListener.java	1.00 07/11/05
 *
 */

import com.ib.client.ContractDetails;

/**
 * Contract Details Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
public interface ContractDetailsListener
	{
	void contractDetails(ContractDetails contractDetails);
	void bondContractDetails(ContractDetails contractDetails);
	}
