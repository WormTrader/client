package com.wormtrader.client;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.TickType;
import com.wormtrader.bars.BarsListener;
import java.text.DateFormat;
// import java.util.Date;

public class MsgGenerator
	{
    public static final String SCANNER_PARAMETERS = "SCANNER PARAMETERS:";
    
	static public String tickPrice( int tickerId, int field, double price, int canAutoExecute)
		{
    	return "id=" + tickerId + "  " + TickType.getField( field) + "=" + price + " " + 
        ((canAutoExecute != 0) ? "can" : "no") + "AutoExecute";
		}
	
	static public String tickSize( int tickerId, int field, int size)
		{
		return "id=" + tickerId + "  " + TickType.getField( field) + "=" + size;
		}
    
	static public String tickOptionComputation( int tickerId, int field, double impliedVol,
    		double delta, double modelPrice, double pvDividend)
		{
		String toAdd = "id=" + tickerId + "  " + TickType.getField( field) +
		   ": IV = " + ((impliedVol >= 0 && impliedVol != Double.MAX_VALUE) ? Double.toString(impliedVol) : "N/A") +
		   " delta = " + ((Math.abs(delta) <= 1) ? Double.toString(delta) : "N/A");
		if (field == TickType.MODEL_OPTION)
			{
			toAdd += ": modelPrice = " + ((modelPrice >= 0 && modelPrice != Double.MAX_VALUE) ? Double.toString(modelPrice) : "N/A");
			toAdd += ": pvDividend = " + ((pvDividend >= 0 && pvDividend != Double.MAX_VALUE) ? Double.toString(pvDividend) : "N/A");
			}
		return toAdd;
		}
    
	static public String tickGeneric(int tickerId, int tickType, double value)
		{
		return "id=" + tickerId + "  " + TickType.getField( tickType) + "=" + value;
		}
    
	static public String tickString(int tickerId, int tickType, String value)
		{
		return "id=" + tickerId + "  " + TickType.getField( tickType) + "=" + value;
		}
    
	static public String tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact, double dividendsToExpiry)
		{
    	return "id=" + tickerId + "  " + TickType.getField(tickType)
		+ ": basisPoints = " + basisPoints + "/" + formattedBasisPoints
		+ " impliedFuture = " + impliedFuture + " holdDays = " + holdDays +
		" futureExpiry = " + futureExpiry + " dividendImpact = " + dividendImpact +
		" dividends to expiry = "	+ dividendsToExpiry;
		}
    
	static public String orderStatus( int orderId, String status, int filled, int remaining,
            double avgFillPrice, int permId, int parentId, double lastFillPrice,
            int clientId, String whyHeld)
		{
		return "id=" + orderId + " parent=" + parentId
			+ " status=" + status + " filled=" + filled 	+ " remain=" + remaining
			+ " avgFill $" + avgFillPrice + " lastFill $" + lastFillPrice
//			+ " clientId=" + clientId
			+ " whyHeld=" + whyHeld;
    }
    
	static public String openOrder( int orderId, Contract contract, Order order)
		{
		String msg = "id=" + orderId + "(" + order.m_permId  + ")"
							+ " " + order.m_tif + " " + order.m_action + " " + order.m_totalQuantity
							+ " " + contract.m_symbol + " " + LegContract.getOptDesc(contract)
							+ " " + order.m_orderType
							+ " @ $" + order.m_lmtPrice + "($" + order.m_auxPrice + ")"
							+ " xchg=" + contract.m_exchange
					//		+ " client Id=" + order.m_clientId + " parent Id=" + order.m_parentId
							+ " ignoreRth=" + order.m_ignoreRth
							+ " rthOnly=" + order.m_rthOnly
							+ " ocaGroup=" + order.m_ocaGroup
							+ " ocaType=" + order.m_ocaType
							+ " allOrNone=" + order.m_allOrNone;
					//		+ " localSymbol=" + contract.m_localSymbol
					//		+ " hidden=" + order.m_hidden
					//		+ " discretionaryAmt=" + order.m_discretionaryAmt
					//		+ " triggerMethod=" + order.m_triggerMethod
					//		+ " goodAfterTime=" + order.m_goodAfterTime
					//		+ " goodTillDate=" + order.m_goodTillDate
					//		+ " account=" + order.m_account
					//		+ " allocation=" + order.m_sharesAllocation
					//		+ " faGroup=" + order.m_faGroup
					//		+ " faMethod=" + order.m_faMethod
					//		+ " faPercentage=" + order.m_faPercentage
					//		+ " faProfile=" + order.m_faProfile
					//		+ " shortSaleSlot=" + order.m_shortSaleSlot
					//		+ " designatedLocation=" + order.m_designatedLocation
					//		+ " rule80A=" + order.m_rule80A
					//		+ " settlingFirm=" + order.m_settlingFirm
					//		+ " minQty=" + order.m_minQty
					//		+ " percentOffset=" + order.m_percentOffset
					//		+ " eTradeOnly=" + order.m_eTradeOnly
					//		+ " firmQuoteOnly=" + order.m_firmQuoteOnly
					//		+ " nbboPriceCap=" + order.m_nbboPriceCap
					//		+ " auctionStrategy=" + order.m_auctionStrategy
					//		+ " startingPrice=" + order.m_startingPrice
					//		+ " stockRefPrice=" + order.m_stockRefPrice
					//		+ " delta=" + order.m_delta
					//		+ " stockRangeLower=" + order.m_stockRangeLower
					//		+ " stockRangeUpper=" + order.m_stockRangeUpper
					//		+ " volatility=" + order.m_volatility
					//		+ " volatilityType=" + order.m_volatilityType
					//		+ " deltaNeutralOrderType=" + order.m_deltaNeutralOrderType
					//		+ " deltaNeutralAuxPrice=" + order.m_deltaNeutralAuxPrice
					//		+ " continuousUpdate=" + order.m_continuousUpdate
					//		+ " referencePriceType=" + order.m_referencePriceType
					//		+ " trailStopPrice=" + order.m_trailStopPrice
					//		+ " scaleNumComponents=" + order.m_scaleNumComponents
					//		+ " scaleComponentSize=" + order.m_scaleComponentSize
					//		+ " scalePriceIncrement=" + order.m_scalePriceIncrement;
	/*************
		if ("BAG".equals(contract.m_secType))
			{
			if (contract.m_comboLegsDescrip != null)
				msg += " comboLegsDescrip=" + contract.m_comboLegsDescrip;
			if (order.m_basisPoints != Double.MAX_VALUE)
				{
				msg += " basisPoints=" + order.m_basisPoints;
				msg += " basisPointsType=" + order.m_basisPointsType;
				}
			}
	*************/
		return msg;
		}

	static public String execDetails( int orderId, Contract contract, Execution execution)
		{
		return "order=" + orderId + "(" + execution.m_permId  + ")"
			+ "execId = " + execution.m_execId + "\n"
			+ execution.m_side + " " + execution.m_shares
			+ " " + contract.m_symbol + " " + LegContract.getOptDesc(contract)
 			+ " @ $" + execution.m_price + ", "
			+ execution.m_exchange + "(" + contract.m_exchange + ")";
		//	+ "client = " + Integer.toString(execution.m_clientId) + "\n"
		//	+ "currency = " + contract.m_currency + "\n"
		//	+ "localSymbol = " + contract.m_localSymbol + "\n"
		//	+ "time = " + execution.m_time + "\n"
		//	+ "acctNumber = " + execution.m_acctNumber + "\n"
		//	+ "liquidation = " + execution.m_liquidation + "\n"
		}

	static public String contractDetails(ContractDetails contractDetails)
		{
		Contract contract = contractDetails.m_summary;
		String msg = " ---- Contract Details begin ----\n"
    		+ contractMsg(contract)
			+ contractDetailsMsg(contractDetails)
    		+ " ---- Contract Details End ----\n";
		return msg;
		}
    
	private static String contractDetailsMsg(ContractDetails contractDetails)
		{
		return "\nmarketName = " + contractDetails.m_marketName + "\n"
			+ " tradingClass = " + contractDetails.m_tradingClass + "\n"
			+ " conid = " + contractDetails.m_conid + "\n"
			+ " minTick = " + contractDetails.m_minTick + "\n"
			+ " multiplier = " + contractDetails.m_multiplier + "\n"
			+ " price magnifier = " + contractDetails.m_priceMagnifier + "\n"
			+ " orderTypes = " + contractDetails.m_orderTypes + "\n"
			+ " validExchanges = " + contractDetails.m_validExchanges + "\n";
		}
    
	static public String contractMsg(Contract contract)
		{
    	return " " + contract.m_symbol + " " + LegContract.getOptDesc(contract)
			+ " (" + contract.m_localSymbol + "), " + contract.m_exchange
			+ " type = " + contract.m_secType + " (" + contract.m_currency + ")\n";
		}
	
	static public String historicalData(int id, String date, double open,
	            double high, double low, double close, int volume)
		{
    	return "id=" + id + " date = " + date + " [" + open + ", " + high + ", " + low
		             + ", " + close + ", " + volume + "]";
		}
	public static String realtimeBar(BarsListener bl, long time, int op,
			int hi, int lo, int cl, long vol )
		{
		return "[" + com.shanebow.util.SBDate.hhmmss(time)
				+ ", " + op + ", " + hi + ", " + lo + ", "	+ cl + ", " + vol
				+ "] ->" + bl.toString();
		}

	static public String scannerParameters(String xml)
		{
		return SCANNER_PARAMETERS + "\n" + xml;
		}
    
	static public String scannerData(int reqId, int rank, ContractDetails contractDetails,
    								 String distance, String benchmark, String projection,
    								 String legsStr)
		{
		Contract contract = contractDetails.m_summary;
    	return "id = " + reqId +
        " rank=" + rank +
        " symbol=" + contract.m_symbol +
        " secType=" + contract.m_secType +
        " expiry=" + contract.m_expiry +
        " strike=" + contract.m_strike +
        " right=" + contract.m_right +
        " exchange=" + contract.m_exchange +
        " currency=" + contract.m_currency +
        " localSymbol=" + contract.m_localSymbol +
        " marketName=" + contractDetails.m_marketName +
        " tradingClass=" + contractDetails.m_tradingClass +
        " distance=" + distance +
        " benchmark=" + benchmark +
        " projection=" + projection +
        " legsStr=" + legsStr;
		}

/***************************
	static public String currentTime(long time)
		{
		return "current time = " + time
			+ " (" + DateFormat.getDateTimeInstance().format(new Date(time * 1000)) + ")";
		}

	static public String nextValidId( int orderId)
		{
		return "Next Valid Order ID: " + orderId;
		}

	static public String updateAccountTime(String timeStamp) {
		return "updateAccountTime: " + timeStamp;
    }

	static public String bondContractDetails(ContractDetails contractDetails) {
        Contract contract = contractDetails.m_summary;
        String msg = " ---- Bond Contract Details begin ----\n"
        + "symbol = " + contract.m_symbol + "\n"
        + "secType = " + contract.m_secType + "\n"
        + "cusip = " + contract.m_cusip + "\n"
        + "coupon = " + contract.m_coupon + "\n"
        + "maturity = " + contract.m_maturity + "\n"
        + "issueDate = " + contract.m_issueDate + "\n"
        + "ratings = " + contract.m_ratings + "\n"
        + "bondType = " + contract.m_bondType + "\n"
        + "couponType = " + contract.m_couponType + "\n"
        + "convertible = " + contract.m_convertible + "\n"
        + "callable = " + contract.m_callable + "\n"
        + "putable = " + contract.m_putable + "\n"
        + "descAppend = " + contract.m_descAppend + "\n"
        + "exchange = " + contract.m_exchange + "\n"
        + "currency = " + contract.m_currency + "\n"
        + "marketName = " + contractDetails.m_marketName + "\n"
        + "tradingClass = " + contractDetails.m_tradingClass + "\n"
        + "conid = " + contractDetails.m_conid + "\n"
        + "minTick = " + contractDetails.m_minTick + "\n"
        + "orderTypes = " + contractDetails.m_orderTypes + "\n"
        + "validExchanges = " + contractDetails.m_validExchanges + "\n"
        + "nextOptionDate = " + contract.m_nextOptionDate + "\n"
        + "nextOptionType = " + contract.m_nextOptionType + "\n"
        + "nextOptionPartial = " + contract.m_nextOptionPartial + "\n"
        + "notes = " + contract.m_notes + "\n"
        + " ---- Bond Contract Details End ----\n";
        return msg;
    }

	static public String updateMktDepth( int tickerId, int position, int operation, int side,
    									 double price, int size)
		{
		return "updateMktDepth: " + tickerId + " " + position + " " + operation + " " + side + " " + price + " " + size;
		}

	static public String updateMktDepthL2( int tickerId, int position, String marketMaker,
    									   int operation, int side, double price, int size)
		{
		return "updateMktDepth: " + tickerId + " " + position + " " + marketMaker + " " + operation + " " + side + " " + price + " " + size;
		}

	static public String updateAccountValue(String key, String value,
	         String currency, String accountName)
		{
		return "updateAccountValue: " + key + " " + value + " " + currency + " " + accountName;
		}

	static public String updatePortfolio(Contract contract, int position, double marketPrice,
    									 double marketValue, double averageCost, double unrealizedPNL,
    									 double realizedPNL, String accountName)
		{
    	String msg = "updatePortfolio: "
    		+ contractMsg(contract)
    		+ position + " " + marketPrice + " " + marketValue + " " + averageCost + " " + unrealizedPNL + " " + realizedPNL + " " + accountName;
    	return msg;
    }

	static public String updateNewsBulletin( int id, int type, String msg, String xchange)
		{
		return "ID=" + id + " :: Type=" + type +  " :: Origin=" + xchange + " :: " + msg;
		}
***************************/

/***************************
	public static final String FINANCIAL_ADVISOR = "FA:";

	static public String managedAccounts( String accountsList)
		{
		return "Connected : The list of managed accounts are : [" + accountsList + "]";
		}

	static public String receiveFA(int faDataType, String xml)
		{
		return FINANCIAL_ADVISOR + " " + EClientSocket.faMsgTypeName(faDataType)
						+ " " + xml;
		}
***************************/
	}