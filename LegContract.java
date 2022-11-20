package com.wormtrader.client;
/********************************************************************
* @(#)LegContract.java	1.00 2009/10/01
* Copyright © 2009 by Richard T. Salamone, Jr. All rights reserved.
*
* Routines for translation between PositionLeg objects and
* Interactive Brokers' Contract objects.
*
* Maintains a singleton Contract object which is reused by
* Dispatcher calls to IB.
*
* @author Rick Salamone
* @version 1.00
* 20091001 rts created from existing Dispatcher code
*******************************************************/
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.shanebow.util.SBDate;
import com.wormtrader.positions.PositionLeg;

public final class LegContract
	{
	private static final Contract m_contract = new Contract();

	public static String expiryString(long expiryTime)
		{
		return SBDate.yyyymm(expiryTime);
		}

	public static Contract getContract ( String symbol )
		{
		String[] symbol_exchange = symbol.split("@");
		m_contract.m_symbol = symbol_exchange[0];
		m_contract.m_secType = "STK";		// "OPT" or "STK"
		m_contract.m_expiry = "";
		m_contract.m_strike = (double)(0.0);
		m_contract.m_right = "";
		m_contract.m_multiplier = "";
		if ( symbol_exchange.length > 1 )
			{
			m_contract.m_exchange = symbol_exchange[1];
			m_contract.m_primaryExch = symbol_exchange[1];
			}
		else
			{
			m_contract.m_exchange = "SMART";
			m_contract.m_primaryExch = "ISE";
			}
		m_contract.m_currency = "USD";
		m_contract.m_localSymbol = "";
		m_contract.m_includeExpired = false;
		return m_contract;
		}

	public static Contract getContract ( PositionLeg leg )
		{
		m_contract.m_symbol = leg.getUnderlying();
		m_contract.m_secType = leg.getSecType();		// "IND" or "OPT" or "STK" or "BOND"
		m_contract.m_expiry = expiryString(leg.getExpiry());
		m_contract.m_right = leg.getRight();
		m_contract.m_multiplier = "";
		m_contract.m_exchange = "SMART";
		m_contract.m_primaryExch = "ISE";
		m_contract.m_currency = "USD";
		m_contract.m_localSymbol = "";
		m_contract.m_includeExpired = false;
		try { m_contract.m_strike = (double)(leg.getStrike()) / 100.0; }
		catch( Exception e) { System.out.println ( "LegContract " + e.toString()); }
		return m_contract;
		}

	public static String toMMMYY( String yyyymmdd )
		{
		int month = Integer.parseInt( yyyymmdd.substring(4,6));
		return SBDate.getMonthString(month) + yyyymmdd.substring(2,4);
		}
	public static final String getOptDesc( Contract contract )
		{
		if ( contract.m_secType.equalsIgnoreCase("STK"))
			return "";
		else return toMMMYY(contract.m_expiry) + " "
		            + contract.m_strike + " "
		            + contract.m_right.charAt(0);
		}
	}
