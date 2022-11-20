package com.wormtrader.client;
/********************************************************************
* @(#)IBExceList.java 1.00 20091111
* Copyright © 2009-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* IBExceList: Manages a list of current session executions from the broker.
* Extends AbstractTableModel so can be displayed in a table.
*
* Receives execDetails messages from the broker, which contain executions for
* the current session. The executions are saved via the ExecDAO (database or file)
* and forwarded to the appropriate PositionLeg. The main issues are that duplicate
* messages are sent (for instance when the program is restarted during the day)
* and that partial fills are sent as separate executions.
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
* 20130315 rts added new methods required by SBExecution
* 20130326 rts added new methods required by SBExecution
*******************************************************/
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.wormtrader.broker.ExecsSummary;
import com.wormtrader.broker.OrderTracker;
import com.wormtrader.client.IBOrderList;
import com.wormtrader.dao.ExecDAO;
import com.wormtrader.dao.SBExecution;
import com.wormtrader.trades.ExecTableModel;
import com.shanebow.util.SBLog;
import java.util.Vector;

public final class IBExecList
	extends ExecTableModel
	{
	private static final int[] DEFAULT_FIELDS =
		{ COL_TIME, COL_SIDE, COL_DESC, COL_PRICE, COL_QTY, COL_ORDERID };

//	public static final int[] colWidths = { 90, 40, 185, 75, 45, 30 };

	private final ExecsSummary m_summary = new ExecsSummary();

	public Iterable<SBExecution> list() { return fExecs; }
	public final ExecsSummary summary() { return m_summary; }

	IBExecList(IBOrderList orders)
		{
		super();
		ExecTableRow._orderList = orders;
		fExecs = new Vector<SBExecution>();
		fFields = DEFAULT_FIELDS;
		}

	public void setDAO(ExecDAO aExecDAO)
		{
		ExecTableRow.fExecDAO = aExecDAO;
		}

	public void execDetails( int orderId, Contract contract, Execution execution )
		{
	SBLog.alert ( "execDetails id " + orderId );
		int i, size = fExecs.size();
		for ( i = 0; i < size; i++ )		// look for existing partials...
			{
			ExecTableRow row = (ExecTableRow)fExecs.get(i);
			if (( row.m_orderId == orderId )
			&&  (row.m_parts.get(0).m_permId == execution.m_permId))
				{
	SBLog.alert ( "execDetails id " + orderId + " found partial");
				if ( row.updatePartialExecs( execution, m_summary ))
					fireTableRowsUpdated(i,i);
				return;
				}
			}
		// no partials found ... this is first or only exec for orderId
		String newTime = execution.m_time.substring(9,18);
		ExecTableRow newRow = new ExecTableRow( orderId, contract, execution, m_summary );
		fExecs.add(i, newRow);
		fireTableRowsInserted(i,i);
		}
	}
