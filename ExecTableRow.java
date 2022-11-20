package com.wormtrader.client;
/********************************************************************
* @(#)ExecTableRow.java 1.00 20091111
* Copyright © 2009-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* ExecTableRow: Implements SBExecution to represent an execution from the
* current trading session. The special requirement of these executions is
* that they may consist of partial executions.
*
* Note: Partial executions are displayed in TWS as expandable executions with a "+".
* They are identified by having the same orderID and exec.m_permId, but different
* exec.m_execId.
*
* @author Rick Salamone
* @version 1.00
* 20091111 rts created from code pulled from ATOrders and TabExecutions.
*              Fixed to produce one database execution record for a given
*              order from the partials.
* 20091113 rts store average price & update fees in db on partial execution
* 20120912 rts changed column order - started merging with simulator code
* 20120921 rts continue merging with simulator code - ExecTableRow implements
*              SBExecution and keeps the Execution Record as a field
* 20130224 rts now maintains an ExecsSummary
* 20130310 rts now uses ExecDAO - no explicit references to db execution record
* 20130326 rts added new methods required by SBExecution
* 20130401 rts decoupled this code from the IBExecList which is a table model
*******************************************************/
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.wormtrader.broker.ExecsSummary;
import com.wormtrader.broker.OrderTracker;
import com.wormtrader.client.LegContract;
import com.wormtrader.client.IBOrderList;
import com.wormtrader.dao.ExecDAO;
import com.wormtrader.dao.SBExecution;
import com.wormtrader.dao.USD;
import com.wormtrader.positions.LegsList;
import com.wormtrader.positions.PositionLeg;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBArray;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBFormat;
import com.shanebow.util.SBLog;

final class ExecTableRow
	implements SBExecution
	{
	public static final int MIN_COMMISSION = 100; // cents
	public static final double FEE_PER_SHARE = 0.5; // half a cent
	static ExecDAO fExecDAO;
	static boolean DEBUG=false;
	static IBOrderList _orderList;

	private static int calculateCommission( final String type, final int qty, final int price )
		{
		int commission = MIN_COMMISSION;
		int shares = (qty < 0) ? -qty : qty;
		if ( type.equals("STK"))
			{
			int max = (int)(shares * price * 0.005);
			commission = (int)(shares * FEE_PER_SHARE);
			if ( commission > max ) commission = max;
			}
		else if ( type.equals("OPT"))
			{
			int feePer = (price >= 10) ? 70 : (price >= 5) ? 50 : 25;
			commission = shares * feePer;
			}
		if ( commission < MIN_COMMISSION )
			commission = MIN_COMMISSION;
		return commission;
		}

	SBArray<Execution> m_parts = new SBArray<Execution>(1);
	int      m_orderId;
	final String   m_symbol;
	String   m_optDesc;
	String   m_type;
	int      m_totalQty; // unsigned
	String   m_qString;
	private final OrderTracker m_order;
	SBExecution dbe;
	private final long fTime; // time of first partial

	private void tallyExec(ExecsSummary aSummary, Execution execution,
		boolean isOption, boolean insert)
		{
		// update summary fields
		int qty = execution.m_shares;
		int cents = (int)(execution.m_price * 100.0);
		if ( isOption )
			qty *= 100;
		cents *= qty;
		switch ( execution.m_side.charAt(0))
			{
			case 'S':		if (insert) aSummary.m_sldTrades++;
								aSummary.m_sldQty += qty;
								aSummary.m_sldCents += cents;
								break;

			case 'B':		if (insert) aSummary.m_botTrades++;
								aSummary.m_botQty += qty;
								aSummary.m_botCents += cents;
								break;
			}
		}

	ExecTableRow ( int orderId, Contract contract, Execution execution, ExecsSummary aSummary )
		{
		m_orderId = orderId;
		m_symbol = contract.m_symbol;
		m_optDesc = LegContract.getOptDesc(contract);
		m_parts.add( execution );
// int		m_avgPrice = SBFormat.parseDollarString(execution.m_price);
		m_totalQty = execution.m_shares;
		m_qString = "" + execution.m_shares;
		m_order = _orderList.getFilled(m_orderId);
		m_type = contract.m_secType;
SBLog.alert("IBExecList looks for %d finds %s", m_orderId, m_order);
		tallyExec(aSummary, execution, !contract.m_secType.equalsIgnoreCase("STK"), true );
		dbe = fExecDAO.get(execution.m_permId, contract.m_symbol);
		if (dbe == null) // doesn't exist in database yet
			{
			long permid = execution.m_permId;
			String execid = execution.m_execId;
			String localSymbol = contract.m_localSymbol;
			String yyyymmdd = execution.m_time.substring(0,8);
			String hhmmss = execution.m_time.substring(10,18);
			String exchange = execution.m_exchange;
			String side = execution.m_side;
			USD price = new USD(execution.m_price);

			int qty = getSignedQty( execution );
			String why = (m_order == null)? "?" : m_order.getReason();
			int fees = calculateCommission( m_type, m_totalQty, price.cents());
			dbe = fExecDAO.insert(permid, execid, m_symbol, localSymbol, yyyymmdd,
		     hhmmss, exchange, m_optDesc, m_type, qty, side, price, new USD(fees), why);
			if (dbe == null)
				SBDialog.fatalError( m_symbol + " Execution INSERT FAILED!" );
			// _tradeLog.add( exec );
			notifyLeg(qty, price.cents(), getTime(execution));
			}
		fTime = dbe.getTime(); // cache for quicker graphing
		}

	private void notifyLeg(int qty, int avgPrice, long time)
		{
		PositionLeg leg = LegsList.find ( m_symbol, m_optDesc );
		if (leg != null)
			leg.execution(m_order, qty, avgPrice, time);
		}

	private long getTime( Execution e ) { return SBDate.toTime ( e.m_time ); }
	private int getSignedQty( Execution e )
		{
		switch ( e.m_side.charAt(0))
			{
			default: SBLog.error ( "IBExecList", "Unrecognized side: " + e.m_side );
			case 'b': case 'B': return e.m_shares;
			case 's': case 'S': return -(e.m_shares);
			}
		}

	boolean updatePartialExecs( Execution partial, ExecsSummary aSummary )
		{
		for ( Execution exec : m_parts ) // here we're checking for an existing execId
			if ( partial.m_execId.equals(exec.m_execId )) // which is different for each
				return false;                             // partial, (but m_permId is same)
		m_parts.add(partial);
		// calc avg price
		int ePrice = SBFormat.parseDollarString(partial.m_price);
		m_totalQty += partial.m_shares; // always positive
		int avgPrice = dbe.getPrice().cents() * m_totalQty;
		avgPrice += (int)(ePrice * partial.m_shares);
		avgPrice /= m_totalQty;
		m_qString += "," + partial.m_shares;
		int dbQty = dbe.getQty();
		tallyExec(aSummary, partial, !getOptDesc().isEmpty(), false );
		if (Math.abs(dbQty) >= m_totalQty ) // exec is in db from a prior run,
			return true;                     // but this run needs to update table
		int qty = getSignedQty(partial);
		dbQty += qty;
		setQty(dbQty);
		setPrice(avgPrice);
		setFees(calculateCommission( m_type, m_totalQty, avgPrice ));
		if ( DEBUG )
			SBLog.alert("updatePartialExecs(%d,%s,%s %d/%d) HERE WE GO: %d",
				m_orderId, m_symbol, partial.m_side, partial.m_shares, m_totalQty,
				dbe.getQty());
		fExecDAO.commit(dbe);
		notifyLeg(qty, ePrice, getTime(partial));
		return true;
		}

	// implement SBExecution
	public boolean equals( SBExecution e ) { return dbe.equals(e); }
	public final String execid()      { return dbe.execid(); }
	public final String type()        { return dbe.type(); }
	public final String localSymbol() { return dbe.localSymbol(); }
	public final String exchange()    { return dbe.exchange(); }
	public long    getID() { return dbe.getID(); }
	public int     getOrderID() { return dbe.getOrderID(); }
	public String  getDesc() { return dbe.getDesc(); }
	public USD     getFees() { return dbe.getFees(); }
	public String  getOptDesc() { return dbe.getOptDesc(); }
	public USD     getPrice() { return dbe.getPrice(); }
	public int     getQty()
		{
		return (m_parts.get(0).m_side.charAt(0) == 'B')? m_totalQty : -m_totalQty;
		}
	public String getQtyString()
		{
		String it = "" + getQty();
		if (m_parts.size() > 1) it += ":" + m_qString;
		return it;
		}
	public String  getSide() { return dbe.getSide(); }
	public String  getSymbol() { return dbe.getSymbol(); }
	public String  getReason() { return dbe.getReason(); }
	public long    getTime() { return fTime; } // cached for graph perf dbe.getTime(); }
	public String  yyyymmdd() { return dbe.yyyymmdd(); }
	public String  hhmmss() { return dbe.hhmmss(); }
	public boolean isOpposing(SBExecution e) { return dbe.isOpposing(e); }
	public boolean isOpposingIgnoreQty(SBExecution e) { return dbe.isOpposingIgnoreQty(e); }

	public final void setFees(int cents) { dbe.setFees(cents); }
	public void setPrice(int price) { dbe.setPrice(price); }
	public void setQty(int qty) { dbe.setQty(qty); }
	public boolean setReason(String s) { return dbe.setReason(s); }
	public void setDate(String yyyymmdd) {dbe.setDate(yyyymmdd);}
	public void setHour(String hhmmss) { dbe.setHour(hhmmss); }
	public void setType(String aType) { dbe.setType(aType); }
	public void setSymbol(String aSymbol) { dbe.setSymbol(aSymbol); }
	public void setLocalSymbol(String aLocalSymbol) { dbe.setLocalSymbol(aLocalSymbol); }
	public void setExchange(String aExchange) { dbe.setExchange(aExchange);}
	public void setSide(String aSide) { dbe.setSide(aSide); }
	public void setOptDesc(String aOptDesc) { dbe.setOptDesc(aOptDesc); }
	public void setExecID(String aExecID) { dbe.setExecID(aExecID); }
	public void setOrderID(int aOrderID) { dbe.setOrderID(aOrderID); }
	}
