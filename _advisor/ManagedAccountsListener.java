package com.wormtrader.client;
/*
 * @(#)ManagedAccountsListener.java	1.00 07/11/05
 *
 */


/**
 * Managed Accounts Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
public interface ManagedAccountsListener
	{
	void managedAccounts( String accountsList);
	void receiveFA(int faDataType, String xml);
	}
