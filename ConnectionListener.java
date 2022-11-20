package com.wormtrader.client;
/*
 * @(#)ConnectionListener.java	1.00 07/12/27
 *
 */

/**
 * Connection Listener
 *
 * @version 1.00 12/27/07
 * @author Rick Salamone
 */
public interface ConnectionListener
	{
	public void connected();
	public void connectionClosed();
	public void connectionProblem( int errCode, String msg );
	public void dispatcherException(String msg);
	void newsUpdate ( int id, int type,	String msg, String origExchange );
	}
