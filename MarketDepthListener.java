package com.wormtrader.client;
/*
 * @(#)MarketDepthListener.java	1.00 07/11/05
 *
 */


/**
 * Market Depth Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
public interface MarketDepthListener
	{
	public void reset();
	public void updateMktDepth (		int tickerId, int position,
											int operation, int side,
											double price, int size );
	public void updateMktDepthL2 (	int tickerId, int position,
											String marketMaker, int operation,
											int side, double price, int size );
	}
