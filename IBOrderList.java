package com.wormtrader.client;
/********************************************************************
* @(#)IBOrderList.java 1.00 20120510
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* IBOrderList: Extends OrderList to track orders placed at Interactive
* Brokers. All of the methods here are either private or package private
* since they are only called by Dispatcher.
*
* @version 1.00
* @author Rick Salamone
* 20120510 rts created by generalizing ATOrders
*******************************************************/
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.wormtrader.broker.OrderList;
import com.wormtrader.broker.OrderTracker;
import com.wormtrader.positions.LegsList;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.client.Dispatcher;
import com.wormtrader.client.LegContract;
import com.shanebow.util.SBLog;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public final class IBOrderList
	extends OrderList<OrderTracker>
	{
	public static final String MODULE="IBOrderList.";
	public static final String FILLED="Filled";
	public static final String CANCELED="Cancelled";

	private final int rowForOrderID(int aOrderID)
		{
		for ( int r = getRowCount(); r > 0; )
			if ( getRow(--r).equals(aOrderID))
				return r;
		return -1;
		}

	/**
	* Called from the dispatcher's placeOrder() method after the order
	* has been sent to IB. The tracker's state is SENT until a confirming
	* openOrder() message has been received from IB at which point the
	* state becomes OPEN.
	*/
	synchronized final OrderTracker sentOrder(int aOID, PositionLeg aLeg, Order aOrder )
		{
		OrderTracker tracker = new OrderTracker(aOID, aLeg, aOrder.m_orderType, signedQty(aOrder),
			cents(aOrder.m_lmtPrice), cents(aOrder.m_auxPrice));
		return sentOrder(tracker);
		}

	/**
	* Called from the dispatcher's placeOrder() method after the order
	* has been sent to IB. The tracker's state is SENT until a confirming
	* openOrder() message has been received from IB at which point the
	* state becomes OPEN.
	*/
	synchronized final OrderTracker sentOrder(OrderTracker tracker)
		{
		tracker.setState(OrderTracker.SENT);
		insertInList(tracker);
		return tracker;
		}

	/**
	* Called by the actual broker to report an open order. This happens at
	* at various times, and may result in duplicate messages. This code is
	* is called when:
	*  1) an order is entered by a WormTrader app (this message confirms it),
	*  2) a connection is established (a message is sent for each open order),
	*  3) an order is entered directly with the broker, outside WormTrader.
	* In cases 2 & 3 the order may be for an unknown leg (ie one not in the
	* WormTrader LegList); currently these orders are simply logged and ignored.
	*/
	synchronized final void openOrder(int aOID, Contract aContract, Order aOrder )
		{
		int row = rowForOrderID(aOID);
		if ( row >= 0 ) // tracker already exists, either a modified or newly opened order
			{
			OrderTracker tracker = getRow(row);
			int qty = aOrder.m_totalQuantity;
			if ( aOrder.m_action.toUpperCase().startsWith("S"))
				qty = -qty;
			int aux = (int)(aOrder.m_auxPrice * 100.0);
			int lmt = (int)(aOrder.m_lmtPrice * 100.0);
			tracker.modify(aOrder.m_orderType, qty, lmt, aux);
			tracker.setState(OrderTracker.OPEN);
			fireTableRowsUpdated(row,row);
			}
		else // this order was not entered during this run of the app
			{ // or was created directly with the broker
			PositionLeg leg = LegsList.find(aContract.m_symbol, LegContract.getOptDesc(aContract));
			if (leg == null)// user may have called broker and created order
				{             // for a symbol not tracked in WormTrader!! Log & ignore...
				logError ( "openOrder", aContract.m_symbol + " has order but no leg" );
				return;
				}
			OrderTracker tracker = new OrderTracker(aOID, leg, aOrder.m_orderType, signedQty(aOrder),
				cents(aOrder.m_lmtPrice), cents(aOrder.m_auxPrice));
			tracker.setState(OrderTracker.OPEN);
			tracker.setTIF(aOrder.m_tif);
			insertInList(tracker);
			}
		}

	void orderError ( int aOID, int errCode, String errMsg )
		{
		int row = rowForOrderID(aOID);
		if ( row < 0 )
			{
			logError( "orderError", "Can't find order #" + aOID);
			return;
			}
		logError ( "orderError", "Order #" + aOID  + " " + errMsg + ": " + errCode );
		OrderTracker tracker = getRow(row);
		if ( errCode == Dispatcher.EC_ORDER_CANCELED )
			tracker.setState(OrderTracker.CANCELED);
		else
			{
			tracker.setState(OrderTracker.ERROR);
			tracker.setStatus("Error " + errCode, errMsg);
			if ( errCode == Dispatcher.EC_SHORT_NOT_ALLOWED )
				tracker.leg().getStrategy().setAllowShort(false);
			// else if ( errCode == Dispatcher.EC_ORDER_REJECTED ))
			}
		fireTableRowsUpdated( row, row );
		}

	private final Vector<OrderTracker> fRecentFills = new Vector<OrderTracker>(3);
	public OrderTracker getFilled(int orderID)
		{
		for (OrderTracker order : fRecentFills) // check filled orders list
			if (order.id() == orderID)
				{
				fRecentFills.remove(order);
				return order;
				}
		SBLog.alert ( MODULE + " %d not on fill list check unfilled orderlist", orderID );
		int r = rowForOrderID(orderID); // check unfilled order list
		return (r < 0)? null : getRow(r);
		}

	public void orderStatus( int aOID, String status, int filled, int remaining,
            						 double avgFillPrice, int permId, int parentId,
	                         double lastFillPrice, int clientId, String whyHeld )
		{
		int row = rowForOrderID(aOID);
		SBLog.alert ( MODULE + "OrderStatus id %d row %d", aOID, row );

		if ( row < 0 ) // order not tracked by WormTrader
			return;
		OrderTracker tracker = getRow(row);
		if (status.equals(CANCELED))
			{
			removeRow(row); // fireTableRowsDeleted( row, row );
			}
		else
			{
			tracker.setFilled(filled);
			tracker.setStatus(status, whyHeld);
			if ( tracker.atLeastPartiallyFilled())
				{
				SBLog.alert ( MODULE + " putting on fill list: " + tracker );
				fRecentFills.add(tracker);
		SBLog.alert ( MODULE + "OrderStatus id %d placed on partial fills", aOID );
				}
			if (status.equals(FILLED))
				{
				removeRow(row);
		SBLog.alert ( MODULE + "OrderStatus id %d removed from order list", aOID );
				}
			else fireTableRowsUpdated( row, row );
			}
		}

	private final void logError ( String caller, String msg )
		{
		SBLog.error ( MODULE + caller, msg );
		}

	private final int signedQty(Order aOrder)
		{
		int qty = aOrder.m_totalQuantity;
		if (aOrder.m_action.charAt(0) == 'S')
			qty = -qty;
		return qty;
		}

	private final int cents(double dollars) { return (int)(dollars * 100.0); }
	}
