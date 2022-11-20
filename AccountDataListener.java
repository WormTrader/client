package com.wormtrader.client;
/*
 * @(#)AccountDataListener.java	1.00 07/11/05
 *
 */

/**
 * Account Data Listener
 *
 * @version 1.00 11/05/07
 * @version 1.00 11/03/09 - removed account name params
 * @author Rick Salamone
 */
public interface AccountDataListener
	{
	public void updateAccountTime ( String timeStamp);
	public void updateAccountValue (	String key, String value, String currency );
	public void updatePortfolio (	String symbol, String optDesc, int position,
											double marketPrice, double marketValue, double avgCost,
											double unrealPNL, 	double realPNL);
	}
