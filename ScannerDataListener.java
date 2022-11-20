package com.wormtrader.client;
/*
 * @(#)ScannerDataListener.java	1.00 07/11/05
 *
 */

import com.ib.client.ContractDetails;

/**
 * Scanner Data Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
public interface ScannerDataListener
	{
	void scannerParameters ( String xml );
	void scannerData ( int reqId, int rank,
											ContractDetails contractDetails,
											String distance, String benchmark,
											String projection, String legsStr );
	}
