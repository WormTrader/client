package com.wormtrader.brain.AutoTrader;
/*
 * EditContract.java
 *
 */

import com.ib.client.Contract;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.client.SBGridBagPanel;
import com.shanebow.util.SBDate;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class EditContract extends SBGridBagPanel
	{
	private Contract	m_contract = null;

	public static final String[] SEC_TYPES =
														{
														"STK",
														"OPT",
														"FUT",
														"IND",
														"FOP",
														"CASH",
														"BAG",
														};
	public static final String[] CURRENCIES =
														{
														"USD",
														"EUR",
														"CHF",
														"CAD",
														};
	public static final String[] EXCHANGES =
														{
														"ARCA",
														"CBSX",
														"DRCTEDGE",
														"EBS",
														"EDGEA",
														"FWB",
														"IBIS",
														"ISE",
														"ISLAND",
														"LAVA",
														"NYSE",
														"PHILX",
														"PSE",
														"TRACKECN",
														"VENTURE",
														};
	public static final String[] OPT_RIGHTS =
														{
														"",
														"PUT",
														"CALL"
														};
	public static final String[] OPT_EXPIRIES =
														{
														"",
														"200806",
														"200807",
														"200808",
														"200809",
														"200810",
														"200811",
														"200812",
														"200901",
														"201001",
														};

	private JTextField 	tfSymbol = new JTextField( "", 7 );
	private JComboBox	cbSecType = new JComboBox ( SEC_TYPES );
	private JTextField 	tfStrike = new JTextField( "0", 6 );
	private JComboBox	cbExpiry = new JComboBox ( OPT_EXPIRIES );
	private JComboBox	cbRight = new JComboBox ( OPT_RIGHTS );
	private JTextField 	tfMultiplier = new JTextField("");
	private JTextField tfExchange = new JTextField( "SMART", 10 );
	private JComboBox 	cbPrimaryExch = new JComboBox ( EXCHANGES );
	private JComboBox 	cbCurrency = new JComboBox ( CURRENCIES );
	private JTextField	tfLocalSymbol = new JTextField("", 7);
	private JCheckBox	chkIncludeExpired = new JCheckBox( "Include Exipred", false );

	public EditContract( Contract contract, int colWidth1, int colWidth2 )
		{
		super ( "Contract Info", colWidth1, colWidth2 );
		m_contract = contract;
		initializeFields();

		addEqualRow( "Symbol",   tfSymbol,      tfLocalSymbol );
		addEqualRow( "Exchange", cbPrimaryExch, tfExchange );
		addEqualRow( "Type",     cbSecType,     cbCurrency );
		addEqualRow( "Option",   cbExpiry,      tfStrike, cbRight );
		addEqualRow( "Multiplier", tfMultiplier, chkIncludeExpired );

		tfLocalSymbol.setToolTipText( "Local exchange symbol, if any" );
		tfMultiplier.setToolTipText ( "Option or futures multiplier, if needed" );
		}

	private void initializeFields()
		{
		if ( m_contract != null )
			{
			tfSymbol.setText( m_contract.m_symbol );
			cbSecType.setSelectedItem( m_contract.m_secType );
			cbExpiry.setSelectedItem( m_contract.m_expiry );
			tfStrike.setText( "" + m_contract.m_strike );
			cbRight.setSelectedItem( m_contract.m_right );
			tfMultiplier.setText( m_contract.m_multiplier );
			tfExchange.setText( m_contract.m_exchange );
			cbPrimaryExch.setSelectedItem( m_contract.m_primaryExch );
			cbCurrency.setSelectedItem( m_contract.m_currency );
			tfLocalSymbol.setText( m_contract.m_localSymbol );
			chkIncludeExpired.setSelected( m_contract.m_includeExpired );
			}
		}

	public final void show ( PositionLeg leg )
		{
		if ( leg == null )
			return;
		if ( m_contract == null )
			m_contract = new Contract();
		m_contract.m_symbol = leg.getUnderlying();
		m_contract.m_secType = leg.getSecType();		// "OPT" or "STK"
		m_contract.m_expiry = SBDate.yyyymm(leg.getExpiry());
		m_contract.m_strike = (double)(leg.getStrike()) / 100.0;
		m_contract.m_right = leg.getRight();
		m_contract.m_multiplier = "";
		m_contract.m_exchange = "SMART";
		m_contract.m_primaryExch = "ISE";
		m_contract.m_currency = "USD";
		m_contract.m_localSymbol = "";
		m_contract.m_includeExpired = false;
		initializeFields();
		}

	public Contract getContract()
		{
		if ( m_contract == null )
			m_contract = new Contract();
		try
			{
			// set contract fields
			m_contract.m_symbol = tfSymbol.getText();
			m_contract.m_secType = SEC_TYPES[cbSecType.getSelectedIndex()];
			m_contract.m_expiry = OPT_EXPIRIES[cbExpiry.getSelectedIndex()];
			try
				{
				m_contract.m_strike = Double.parseDouble( tfStrike.getText());
				}
			catch ( NumberFormatException ex )
				{
				m_contract.m_strike = 0.0;
				}
			m_contract.m_right = OPT_RIGHTS[cbRight.getSelectedIndex()];
			m_contract.m_multiplier = tfMultiplier.getText();
			m_contract.m_exchange = tfExchange.getText();
			m_contract.m_primaryExch = EXCHANGES[cbPrimaryExch.getSelectedIndex()];
			m_contract.m_currency = CURRENCIES[cbCurrency.getSelectedIndex()];
			m_contract.m_localSymbol = tfLocalSymbol.getText();
			m_contract.m_includeExpired = chkIncludeExpired.isSelected();
			}
		catch( Exception e)
			{
			System.out.println( "Error - " + e );
			return null;
			}
		return m_contract;
		}
	}
