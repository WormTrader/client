package com.wormtrader.client;
/********************************************************************
* @(#)Dispatcher.java 1.00 20071105
* Copyright © 2007-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* Dispatcher
*
* @version 1.00
* @author Rick Salamone
* 20071105 rts created
* 20120508 rts using SBArray's binarySearch for history & bars listeners
* 20120510 rts now maintains IBOrderList which apps can display in JTable
* 20120625 rts error handles server errors for historical requests
* 20120921 rts using SBProperties for connect data
* 20121001 rts added login() which connects or dialog if error
* 20121002 rts cancelOrder takes OrderTracker rather than order id
* 20121017 rts eliminated placeMktOrder
* 20121209 rts added setRealTimeBarDuration
* 20130218 rts modified for changes to BarSize names
*******************************************************/
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
// import com.ib.client.EWrapperMsgGenerator; // using local MsgGenerator instead
import com.ib.client.Execution;
import com.ib.client.ExecutionFilter;
import com.ib.client.Order;
import com.ib.client.ScannerSubscription;
import com.ib.client.TickType;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarsListener;
import com.wormtrader.bars.BarSize;
import com.wormtrader.broker.Broker;
import com.wormtrader.broker.OrderList;
import com.wormtrader.broker.OrderTracker;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.TickListener;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBArray;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBLog;
import com.shanebow.util.SBProperties;
import java.util.Hashtable;
import java.util.Vector;

public final class Dispatcher
	implements com.ib.client.EWrapper, Broker
	{
	public static final String CONNECTION_CLOSED = "Connection closed";
	public static final byte NO_NEWS = 0;
	public static final byte NEW_NEWS = 1;
	public static final byte ALL_NEWS = 2;
	public static final int DEFAULT_PORT = 7496;
	public static final String ALL_GENERIC_TICK_TAGS =
							"100,101,104,106,162,165,221,225,236";
	public static final int EC_1ST_HIST_ERROR = 162;
	public static final int EC_LAST_HIST_ERROR = 200;

	public static final int EC_1ST_ORDER_ERROR = 103;
	public static final int EC_SHORT_NOT_ALLOWED = 201;
	public static final int EC_ORDER_REJECTED = 201;
	public static final int EC_ORDER_CANCELED = 202;
	public static final int EC_SECURITY_NOT_ALLOWED = 203;
	public static final int EC_MKT_DEPTH_DATA_RESET = 317;
	public static final int EC_NOT_FA_ACCOUNT = 321;
	public static final int EC_LAST_ORDER_ERROR = 422;

	public static final int EC_1ST_CONNECT_ERROR = 1100;
	public static final int EC_IB_TWS_DISCONNECT = 1100;
	public static final int EC_IB_TWS_RECONNECT_DATA_LOSS = 1101;
	public static final int EC_IB_TWS_RECONNECT_DATA_OK = 1102;
	public static final int EC_MKT_FARM_DISCONNECT = 2103;
	public static final int EC_HST_FARM_DISCONNECT = 2105;
	public static final int EC_LAST_CONNECT_ERROR = 2108;

	private static EClientSocket	m_client = null;
	private boolean						m_disconnectInProgress = false;
	private static final Dispatcher	 m_broker = new Dispatcher(); // returned via getInstance()
	private int								m_orderID = -1;
	private String							m_account = "";
	private final IBOrderList    m_orderList = new IBOrderList();
	private final IBExecList     m_execList = new IBExecList(m_orderList);

	public final IBExecList getExecList() { return m_execList; }
	// Configuration Settings
	int             m_level2Depth = 20;
	String          m_genericTicks = "";	// ALL_GENERIC_TICK_TAGS
	private int     m_barSize = 60;			// # of seconds per realtime bar
	int             m_newsLevel = NEW_NEWS;

	public void setRealTimeBarDuration(int seconds) { m_barSize = seconds; }
	// Network Settings
	String    m_ipAddress = "";
	int       m_port = DEFAULT_PORT;

	// what to send to the message logger
	boolean   m_logAccount = false;
	boolean   m_logBars = false;
	boolean   m_logOrders = false;
	boolean   m_logTickOther = false;	// tickGeneric, tickEFP, tickString
	boolean   m_logTickOption = false;
	boolean   m_logTickPrice = false;
	boolean   m_logTickSize = false;

	private Vector<AccountDataListener>  accountListeners = new Vector<AccountDataListener>(3);
	private Vector<ConnectionListener>   conectListeners = new Vector<ConnectionListener>(2);
	private Vector<MarketDepthListener>  level2Listeners = new Vector<MarketDepthListener>(1);
	private SBArray<TickListener>        tickListeners = new SBArray<TickListener>(14);
//	private Vector scannerDataListeners = new Vector();

	private Hashtable<BarsListener,Bar> barBuilders = new Hashtable<BarsListener,Bar>();
	private SBArray<BarsListener>  barsListeners = new SBArray<BarsListener>(30); // realtime
	private SBArray<BarsListener>  historyListeners = new SBArray<BarsListener>(20); // historical

	private Dispatcher ()
		{
		if ( m_client != null ) return;
		m_client = new EClientSocket( this );
		}

	public static synchronized Dispatcher getInstance()
		{
		return m_broker;
		}

	public Object clone() throws CloneNotSupportedException
		{
		throw new CloneNotSupportedException(); // that'll teach 'em
		}
 
	public synchronized void addConnectionListener( ConnectionListener cl )
		{
		if ( !conectListeners.contains( cl ))
			conectListeners.add( cl );
		}
	public synchronized void removeConnectionListener( ConnectionListener cl )
		{
		conectListeners.remove( cl );
		}

	public synchronized void addAccountDataListener( AccountDataListener adl )
		{
		if ( !accountListeners.contains( adl ))
			accountListeners.add( adl );
		}
	public synchronized void removeAccountDataListener( AccountDataListener adl )
		{
		accountListeners.remove( adl );
		}

	public synchronized void addMarketDepthListener( MarketDepthListener mdl )
		{
		if ( !level2Listeners.contains( mdl ))
			level2Listeners.add( mdl );
		}
	public synchronized void removeMarketDepthListener( MarketDepthListener mdl )
		{
		level2Listeners.remove( mdl );
		}

	public synchronized final void setServerLogLevel(int logLevel)
		{
		m_client.setServerLogLevel(logLevel);
		}

	public final boolean isConnected () { return m_client.isConnected(); }
	public final boolean login(java.awt.Component aParent)
		{
		if (!connect())
			return SBDialog.error("IB Connect", "<html>Unable to connect:<br>"
				+ getConnectStatus(), aParent);
		return true;
		}

	public final boolean connect()
		{
		if (m_client.isConnected()) return true;
		SBProperties props = SBProperties.getInstance();
		m_ipAddress = props.getProperty("usr.ib.ip", "");
		m_port = props.getInt("usr.ib.port", DEFAULT_PORT);
		int clientId = props.getInt("app.ib.client");

		log( true, "TWS Connection", "connecting client " + clientId
		                             + " to ip " + m_ipAddress + " port " + m_port);
		m_client.eConnect( m_ipAddress, m_port, clientId );
		if ( m_client.isConnected())
			{
			log( true, "TWS Connection", getConnectStatus());
			m_disconnectInProgress = false;
			if ( m_newsLevel != NO_NEWS )
				m_client.reqNewsBulletins( m_newsLevel == ALL_NEWS );
			for ( ConnectionListener cl : conectListeners )
				cl.connected();
			return true;
			}
		else	return false;
		}
	public final void disconnect()
		{
		log( true, "TWS Connection", "disconnecting...");
		m_disconnectInProgress = true;
		m_client.eDisconnect();
		}
	public final String getConnectStatus()
		{
		if ( m_client.isConnected())
			return "Connected to Tws server version "
										+ m_client.serverVersion()
										+ "\nat " + m_client.TwsConnectionTime();
		else
			return "Disconnected";
		}

	public final int exerciseOptions( PositionLeg leg, int qty, String exchange )
		{
		if ( exchange.equals( "SMART" ))
			{
	  		logError ( "EXERCISE", "Cannot use SMART exchange" );
			return 0;
			}
		Contract contract = LegContract.getContract(leg);
		contract.m_exchange = exchange;
		log ( m_logOrders, "EXERCISE", "" + qty + " " + leg  + " contracts" );
		m_client.exerciseOptions (	 ++m_orderID, contract, 1 /* exercise */,
													qty, "", 0 /* 1 -> override */ );
		return 	m_orderID;
		}

	// implement Broker interface
	@Override public long time() { return SBDate.timeNow(); }
	@Override public void setTime(long aTime)
		{
		throw new IllegalArgumentException("Dispatcher.setTime() not supported");
		}

	@Override public final void placeOrder(OrderTracker aTracker)
		{
		PositionLeg leg = aTracker.leg();
		String aType = aTracker.type();
		int aQty = aTracker.qty();
		int aLmt  = aTracker.getLmt();
		int aAux  = aTracker.getAux();
		Order order = new Order();
		order.m_action = (aQty < 0)? "SELL" : "BUY";
		order.m_totalQuantity = Math.abs(aQty);
		order.m_orderType = aType;

		if (aType.equals("STP"))
			{
			order.m_auxPrice = (double)aLmt / 100.0;
			order.m_lmtPrice = (double)aAux / 100.0;
			}
		else // lmt or mkt
			{
			order.m_lmtPrice = (double)aLmt / 100.0;
			order.m_auxPrice = (double)aAux / 100.0;
			}
// System.out.println("lmt $: " + order.m_lmtPrice + " aux $: " + order.m_auxPrice);
		order.m_rthOnly = true;
	/************
		// order.m_sharesAllocation = m_sharesAllocProfile;
		// order.m_goodAfterTime = tfGoodAfter.getText();
		// order.m_goodTillDate = tfGoodTil.getText();

		order.m_faGroup = null; // see FAAllocationInfoDlg
		order.m_faProfile = null;
		order.m_faMethod = null;
		order.m_faPercentage = null;
	************/
		if ( m_orderID < 0 )
			{
	  		logError ( "ORDER", "No valid ID for " + leg );
			return;
			}
		++m_orderID;
		aTracker.setID(m_orderID);
		order.m_orderId = m_orderID;
		order.m_clientId = 0;
		m_orderList.sentOrder(aTracker);
		m_client.placeOrder( m_orderID, LegContract.getContract(leg), order );
		log ( m_logOrders, "ORDER", "#" + m_orderID + " placed from tracker " + leg );
		}

	@Override public final void cancelOrder(OrderTracker aTracker)
		{
		m_client.cancelOrder(aTracker.id());
		}

	@Override public final OrderList getOrderList() { return m_orderList; }

	@Override public boolean getLogOrders() { return m_logOrders; }
	@Override public void setLogOrders(boolean on) { m_logOrders = on; }

	public final void reqAccountUpdates()
		{
		log ( m_logAccount, "ACCOUNT", "requested account updates" );
		m_client.reqAccountUpdates( false, m_account );
		m_client.reqAccountUpdates( true, m_account );
		}

	public final void reqContractDetails( PositionLeg leg )
		{
		m_client.reqContractDetails( LegContract.getContract(leg));
		}

	public final void reqCurrentTime()
		{
		m_client.reqCurrentTime();
		}

	public final void reqExecutions()
		{
		ExecutionFilter filter = new ExecutionFilter();
		m_client.reqExecutions( filter );
		}

	public void reqHistoricalData( String symbol, String endTime, String duration,
														BarSize barSize, int useRTH, BarsListener bl )
		{
		int id = bl.hashCode();
		int index = historyListeners.binarySearch(id);
		if ( index >= 0 )
			logError ( symbol, "only one open history request allowed per listener" );
		else historyListeners.add( -index-1, bl );
		int formatDate = 2;
		m_client.reqHistoricalData (	id, LegContract.getContract(symbol),
						endTime, duration, barSize.ibString(), "TRADES", useRTH, formatDate );
		}

	public void reqIndexHistory ( String indexSymbol,
												String endTime, String duration,
												BarSize barSize, int useRTH,
												BarsListener bl )
		{
		int id = bl.hashCode();
		int index = historyListeners.binarySearch(id);
		if ( index >= 0 )
			logError ( indexSymbol, "only one open history request allowed per listener" );
		else historyListeners.add( -index-1, bl );
		Contract c = LegContract.getContract( indexSymbol ); c.m_secType = "IND";
		int formatDate = 2;
		m_client.reqHistoricalData (	id, c, endTime, duration,
												barSize.ibString(), "TRADES", useRTH, formatDate );
		}

	public final void canHistoricalData ( BarsListener bl )
		{
		int id = bl.hashCode();
		m_client.cancelHistoricalData(id);
		int index = historyListeners.binarySearch(id);
		if ( index >= 0 ) // historyListeners.contains( bl ))
			historyListeners.removePack(index);
		}

	public synchronized void reqRealTimeBars ( String symbol, BarsListener bl )
		{
		int id = bl.hashCode();
		// BUG: only 5 second bars currently supported
		int bsDuration = 5; // BarSize.FIVE_SEC.duration();
		int index = barsListeners.binarySearch(id);
		if ( index >= 0 ) // barsListeners.contains( bl ))
			{
			logError ( bl.toString(), "only one open bars request allowed per listener" );
			m_client.cancelRealTimeBars( id );
			}
		else barsListeners.insert( bl ); // keep sorted, so we can do binarySearch
		log ( m_logBars, "BARS", "**Requested for " + symbol + ", " + bl.toString());
		m_client.reqRealTimeBars( id, LegContract.getContract(symbol), bsDuration, "TRADES", true );
		}

	public final void canRealTimeBars( BarsListener bl )
		{
		int id = bl.hashCode();
		log ( m_logBars, "BARS", "Cancelled for " + id  + " " + bl );
		m_client.cancelRealTimeBars( id );
		barBuilders.remove( bl );
		int index = barsListeners.binarySearch(id);
		if ( index >= 0 )
			barsListeners.removePack(index);
		}

	public final void reqIndexData ( final String symbol, final TickListener tl )
		{
		int id = tickListeners.indexOf(tl);
		if ( id == -1 )
			id = tickListeners.iAdd( tl );
		Contract contract = LegContract.getContract(symbol);
		contract.m_secType = "IND";
		log ( m_logAccount, "Index", "Requested: "
				+ contract.m_symbol + " @ " + contract.m_exchange );
		m_client.reqMktData( id, contract, "", false /* snapshotMktData */ );
		}

	public final void reqMarketData ( PositionLeg leg )
		{
		log ( m_logAccount, "Tick", "Requested: " + leg );
		int id = tickListeners.indexOf(leg);
		if ( id == -1 )
			id = tickListeners.iAdd( leg );
		m_client.reqMktData( id, LegContract.getContract(leg),
											m_genericTicks, false /* snapshotMktData */ );
		}

	public final void canMarketData ( TickListener tl )
		{
		int id = tickListeners.indexOf( tl );
		if ( id == -1 )
			{
			logError ( "canMarketData", tl.toString() + " Not found in list" );
			return;
			}
		m_client.cancelMktData( id );
		tickListeners.remove(id);
		}

public void dumpTickListeners()
	{
	log( true, "TickListeners:", "" );
	for ( TickListener tl : tickListeners )
		log( true, " * ", tl.toString());
	log( true, "**********", "" );
	}

	public final int  getMktDepthNumRows() { return m_level2Depth; }
	public final void setMktDepthNumRows(int nRows) { m_level2Depth = nRows; }
	public final void reqMktDepth( PositionLeg leg )
		{
		m_client.reqMktDepth( leg.hashCode(), LegContract.getContract(leg), m_level2Depth );
		}
	public final void cancelMktDepth( PositionLeg leg )
		{
		m_client.cancelMktDepth( leg.hashCode());
		}

	public final void setNewsLevel( byte newsLevel )
		{
		if ( newsLevel == m_newsLevel ) return;
		switch ( m_newsLevel = newsLevel )
			{
			case NO_NEWS:  m_client.cancelNewsBulletins();
			case NEW_NEWS: m_client.reqNewsBulletins( false );
			case ALL_NEWS: m_client.reqNewsBulletins( true );
			}
		}

	public final void reqOpenOrders()
		{
		log ( m_logOrders, "ORDER", "req Open Orders" );
		// m_client.reqOpenOrders();
		m_client.reqAllOpenOrders();	// request list of all open orders

		// request to automatically bind any newly entered TWS orders
		// to this API client. NOTE: TWS orders can only be bound to
		// client's with clientId=0.
		m_client.reqAutoOpenOrders( true );
		}

	public synchronized void reqScannerParameters()
		{
		m_client.reqScannerParameters();
		}
	public final void reqScannerSubscription( int id, ScannerSubscription sub )
		{
		m_client.reqScannerSubscription( id, sub );
		}
	public final void cancelScannerSubscription( int id )
		{
		m_client.cancelScannerSubscription( id );
		}

	public void tickPrice( int id, int field, double price, int canAutoExecute )
		{			// received price tick
		TickListener tl = tickListeners.get( id );
/*
		log ( m_logTickPrice, "Tick " + tl.toString(),
				MsgGenerator.tickPrice( id, field, price, canAutoExecute )
					+ " field " + field );
*/
		switch ( field )
			{
			case TickType.ASK:
			case TickType.ASK_OPTION:							tl.setAsk ( toCents(price)); return;
			case TickType.BID:
			case TickType.BID_OPTION:							tl.setBid ( toCents(price)); return;
			case TickType.LAST:
			case TickType.LAST_OPTION:							tl.setLast ( toCents(price)); return;
			case TickType.CLOSE:
			case TickType.OPEN:										tl.setOpen(toCents(price)); // return;
			case TickType.HIGH:	
			case TickType.LOW:
	log ( m_logTickPrice, "Tick " + tl.toString(),
				MsgGenerator.tickPrice( id, field, price, canAutoExecute )
					+ " field " + field );
  break;
			case TickType.MODEL_OPTION:						break;
			case TickType.LOW_13_WEEK:							break;
			case TickType.HIGH_13_WEEK:						break;
			case TickType.LOW_26_WEEK:							break;
			case TickType.HIGH_26_WEEK:						break;
			case TickType.LOW_52_WEEK:							break;
			case TickType.HIGH_52_WEEK:						break;
			case TickType.AVG_VOLUME:							break;
			case TickType.OPEN_INTEREST:						break;
			case TickType.OPTION_HISTORICAL_VOL:		break;
			case TickType.OPTION_IMPLIED_VOL:				break;
			case TickType.OPTION_BID_EXCH:					break;
			case TickType.OPTION_ASK_EXCH:					break;
			case TickType.OPTION_CALL_OPEN_INTEREST:	break;
			case TickType.OPTION_PUT_OPEN_INTEREST:	break;
			case TickType.OPTION_CALL_VOLUME:				break;
			case TickType.OPTION_PUT_VOLUME:				break;
			case TickType.INDEX_FUTURE_PREMIUM:			break;
			case TickType.BID_EXCH:								break;
			case TickType.ASK_EXCH:								break;
			case TickType.AUCTION_VOLUME:					break;
			case TickType.AUCTION_PRICE:						break;
			case TickType.AUCTION_IMBALANCE:				break;
			case TickType.MARK_PRICE:							break;
			case TickType.BID_EFP_COMPUTATION:			break;
			case TickType.ASK_EFP_COMPUTATION:			break;
			case TickType.LAST_EFP_COMPUTATION:			break;
			case TickType.OPEN_EFP_COMPUTATION:			break;
			case TickType.HIGH_EFP_COMPUTATION:			break;
			case TickType.LOW_EFP_COMPUTATION:			break;
			case TickType.CLOSE_EFP_COMPUTATION:		break;
			case TickType.LAST_TIMESTAMP:					break;
			case TickType.SHORTABLE:							break;
			default:														break;
			}
		return;
		}

	public void tickOptionComputation ( int tickerId, int field,
																double impliedVol, 	double delta,
																double modelPrice, double pvDividend )
		{		// received computation tick
		TickListener tl = tickListeners.get( tickerId );
		log ( m_logTickOption, "tickOpt " + tl.toString(),
					MsgGenerator.tickOptionComputation( tickerId,
										field, impliedVol, delta, modelPrice, pvDividend )
											+ " field " + field );
		((PositionLeg)tl).setDelta ( delta );
		}

	public void tickSize( int tickerId, int field, int size )
		{
		log ( m_logTickSize, "tickSize",
			MsgGenerator.tickSize( tickerId, field, size ));
		}

	public void tickGeneric( int tickerId, int tickType, double value )
		{
		log ( m_logTickOther, "tickGeneric",
			MsgGenerator.tickGeneric(tickerId, tickType, value));
		}

	public void tickString( int tickerId, int tickType, String value )
		{
		log ( m_logTickOther, "tickString",
			MsgGenerator.tickString( tickerId, tickType, value ));
		}

	public void tickEFP ( int tickerId, int tickType, double basisPoints,
										String formattedBasisPoints, double impliedFuture,
										int holdDays, String futureExpiry, double dividendImpact,
										double dividendsToExpiry )
		{
		log ( m_logTickOther, "tickEFP",
			MsgGenerator.tickEFP (	tickerId, tickType,
																basisPoints, formattedBasisPoints,
																impliedFuture, holdDays, futureExpiry,
																dividendImpact, dividendsToExpiry));
		}

	public void openOrder( int orderId, Contract contract, Order order )
		{
		log ( m_logOrders, "openOrder",
			MsgGenerator.openOrder( orderId, contract, order ));
		m_orderList.openOrder(orderId, contract, order);
		}

	public void orderStatus ( int orderId, String status,
												int filled, int remaining,
												double avgFillPrice, int permId,
												int parentId, double lastFillPrice,
												int clientId, String whyHeld )
		{
		log ( m_logOrders, "orderStatus",
			MsgGenerator.orderStatus( orderId, status, filled,
																	remaining, avgFillPrice, permId,
																	parentId, lastFillPrice, clientId, whyHeld));
		if ( m_orderID < orderId ) m_orderID = orderId;
		m_orderList.orderStatus ( orderId, status, filled, remaining,
												avgFillPrice, permId, parentId,
												lastFillPrice, clientId, whyHeld );

		reqExecutions();
		}

	public void execDetails( int orderId, Contract contract, Execution execution )
		{
		log ( m_logOrders, "execDetails",
			MsgGenerator.execDetails(orderId, contract, execution));
		m_execList.execDetails(orderId, contract, execution);
		}

	public void nextValidId( int orderId )		// received next valid order id
		{
		log ( m_logOrders, "ORDER", "nextValidID: " + orderId );
		m_orderID = orderId;
		}

	public void contractDetails( ContractDetails contractDetails )
		{
		log ( true, "contractDetails", 	MsgGenerator.contractDetails(contractDetails));
		}

	public void scannerData (	int reqId, int rank, ContractDetails cd,
												String distance, String benchmark,
												String projection, String legsStr)
		{
		log ( true, "scannerData",
			MsgGenerator.scannerData(reqId, rank, cd, distance,
													benchmark, projection, legsStr ));
		}

	public void bondContractDetails(ContractDetails contractDetails)
		{
	//	log ( true, "bondDetails",	MsgGenerator.bondContractDetails(contractDetails));
		}

	public void updateMktDepth( int tickerId, int position, int operation,
                    int side, double price, int size )
		{
		for ( MarketDepthListener mdl : level2Listeners )
			mdl.updateMktDepth( tickerId, position, operation, side, price, size );
		}

	public void updateMktDepthL2( int tickerId, int position, String mktMaker,
                    int operation, int side, double price, int size )
		{
		for ( MarketDepthListener mdl : level2Listeners )
			mdl.updateMktDepthL2( tickerId, position, mktMaker, operation, side, price, size );
		}

	public void error( Exception ex )
		{
		if ( !m_disconnectInProgress )	// do not report exceptions
			{												// if we initiated disconnect
			String msg = "Exception: " + ex.toString();
			for ( ConnectionListener cl : conectListeners )
				cl.dispatcherException(msg);
			ex.printStackTrace();
			}
		}

	public void error( String msg )
		{
		logError ( "", msg );
		}

	private boolean isServerError(int code) { return code >= 320 && code <= 324; }
	private boolean isHistoryError(int code)
		{ return code >= EC_1ST_HIST_ERROR && code <= EC_LAST_HIST_ERROR; }

	public void error( int id, int code, String errorMsg )
		{
		if ( code < EC_1ST_ORDER_ERROR ) // TODO: fatal system errors???
			;
		else if (isHistoryError(code) || isServerError(code))
			// careful these are in the middle of the order codes!
			{
			int index = historyListeners.binarySearch(id);
			if ( index >= 0 )
				{
				BarsListener bl = historyListeners.get(index);
				logError ( bl.toString() + " | " + code + " |", errorMsg );
				bl.barError ( code, errorMsg );
				return;
				}
			}
		else if ((code >= EC_1ST_ORDER_ERROR) && (code <= EC_LAST_ORDER_ERROR))
			{
			m_orderList.orderError ( id, code, errorMsg );
			}
		else if ((code >= EC_1ST_CONNECT_ERROR) && (code <= EC_LAST_CONNECT_ERROR))
			{
			for ( ConnectionListener cl : conectListeners )
				cl.connectionProblem ( code, errorMsg );
			}
		logError ( "id: " + id + " | " + code + " |", errorMsg );

		if ( code == EC_MKT_DEPTH_DATA_RESET )
			{
			for ( MarketDepthListener mdl : level2Listeners )
				mdl.reset();
			}
		}

	public void connectionClosed()
		{
		m_disconnectInProgress = false;
		for ( ConnectionListener cl : conectListeners )
			cl.connectionClosed();
		}

	public void updateAccountValue(String key, String value,
                                   String currency, String account)
		{
		for ( AccountDataListener adl : accountListeners )
			adl.updateAccountValue(key, value, currency);
		}

	public void updatePortfolio ( Contract contract, int position,
														double marketPrice, double marketValue,
														double averageCost, double unrealizedPNL,
														double realizedPNL, String account )
		{
		String optDesc = LegContract.getOptDesc( contract );
		log ( m_logAccount, "updatePortfolio", contract.m_symbol
										+ ": " + position + " @ $" + averageCost );
		for ( AccountDataListener adl : accountListeners )
			adl.updatePortfolio(contract.m_symbol, optDesc, position, marketPrice,
			                     marketValue, averageCost, unrealizedPNL, realizedPNL );
		}

	public void updateAccountTime( String timeStamp )
		{
		log ( m_logAccount, "ACCOUNT", "Updated " + timeStamp );
		for ( AccountDataListener adl : accountListeners )
			adl.updateAccountTime(timeStamp);
		}

	public void historicalData ( int id, String date,
													double open, double high, double low,
                               double close, int volume, int count,
													double WAP, boolean gaps )
		{
		log ( m_logBars, "HIST",
			MsgGenerator.historicalData ( id, date, open, high, low, close, volume ));
		if ( open < 0.0 )
			{
			historyDone ( id );
			return;
			}
		int op = (int)(open * 100);		// convert prices to cents
		int hi = (int)(high * 100);
		int lo = (int)(low * 100);
		int cl = (int)(close * 100);
		int wap = (int)(WAP * 100);
		// NOTE: IB bug daily data request ignores formatDate param in reqHist
		// but we need to get daily data beginning at 9:30 to ensure that time is bar start
		long time = (date.length() == 8) ? SBDate.toTime( date + "  09:30" )
			                               : Long.parseLong(date);
		Bar bar = new Bar ( time, op, hi, lo, cl, volume, count, wap );
		int index = historyListeners.binarySearch(id);
		if ( index >= 0 )
			historyListeners.get(index).historyBar( bar );
		}

	private synchronized final void historyDone ( int id )
		{
		log ( m_logBars, "HIST", "finished " + id );
		int index = historyListeners.binarySearch(id);
		if ( index >= 0 )
			{
			BarsListener bl = historyListeners.removePack(index);
			bl.historyDone();
			}
		}

	public void realtimeBar ( int id, long time, double open,
												double high, double low, double close,
												long volume, double WAP, int count )
		{
		int index = barsListeners.binarySearch(id);
		if ( index < 0 )
			{
			logError ( "BARS", "Can't find listener id: " + id );
			return;
			}

		BarsListener bl = barsListeners.get(index);
		int op = (int)(open * 100);		// convert prices to cents
		int hi = (int)(high * 100);
		int lo = (int)(low * 100);
		int cl = (int)(close * 100);
		log ( m_logBars, "BAR", MsgGenerator.realtimeBar ( bl, time, op, hi, lo, cl, volume ));

		// the following code compensates for IB's bug that only
		// allows for 5 sec bars, we build longer bars ourself
		Bar bar = (Bar)barBuilders.get( bl );	// get bar in progress
		if ((( time % m_barSize ) == 0 )	// time to send the bar to listeners?
		&&  ( bar != null ))
			{
			bl.realtimeBar ( bar );			// send the "finsished" bar to the listener
			barBuilders.remove( bl );		// then remove it from the "in progress" list
			bar = null;
			}
		if ( bar == null )						// start a new bar
			{
			bar = new Bar ( time, op, hi, lo, cl, volume, count, 0 );
			barBuilders.put( bl, bar );
			}
		else // we have a bar in progress, adjust its values
			{
			bar.adjust ( time, hi, lo, cl, volume, count );
			}
		// Note, when this bug fixed, replace above code with the following line
		// Bar bar = new Bar ( time, op, hi, lo, cl, volume, count, wap );
		}

	public void scannerParameters(String xml)
		{
		log ( true, MsgGenerator.SCANNER_PARAMETERS, xml );
		}

	public void currentTime( long time )
		{
		log ( true, "TIME", SBDate.hhmmss(time));
		}

	public void updateNewsBulletin( int id, int type, String msg, String exchange )
		{
		for ( ConnectionListener cl : conectListeners )
			cl.newsUpdate(id, type, msg, exchange);
		}

	private void log ( boolean enabled, final String title, final String msg )
		{
		if ( !enabled ) return;
		SBLog.write ( SBLog.NET, title, msg );
		}

	private void logError ( final String title, final String msg )
		{
		SBLog.error ( title, msg );
		}

	private final int toCents(final double dollars) { return (int)(dollars * 100.0); }

	// Financial Advisor stuff disabled...
	public final void reqFinancialAdvisor() {}
	public final void reqManagedAccts() {}
	public void receiveFA( int faDataType, String xml ) {}
	public void managedAccounts( String accountsList ) {}
	} // 858
