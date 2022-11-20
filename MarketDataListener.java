package com.wormtrader.client;
/*
 * @(#)MarketDataListener.java	1.00 07/11/05
 *
 */

/**
 * Market Data Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
public interface MarketDataListener
	{
	void tickPrice (	int tickerId, int field, double price, int canAutoExecute);
	void tickSize(		int tickerId, int field, int size);
	void tickOptionComputation( int tickerId, int field, double impliedVol,
    		double delta, double modelPrice, double pvDividend);
	void tickGeneric(int tickerId, int tickType, double value);
	void tickString(int tickerId, int tickType, String value);
	void tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact, double dividendsToExpiry);
	}
