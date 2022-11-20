/**
 *
 * DlgOrderExt.java
 *
 */
package com.wormtrader.brain.AutoTrader;

import com.ib.client.Order;
import com.shanebow.ui.SBDialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DlgOrderExt extends JDialog
	{
	public Order 		m_order = new Order();
	private Order		m_destOrder = null;
	public boolean 		m_rc;

	private JTextField 	m_tif = new JTextField( "DAY");
    private JTextField 	m_ocaGroup = new JTextField();
    private JTextField 	m_ocaType = new JTextField("0");
    private JTextField 	m_account = new JTextField();
    private JTextField 	m_openClose = new JTextField( "O");
    private JTextField 	m_origin = new JTextField( "1");
    private JTextField 	m_orderRef = new JTextField();
    private JTextField 	m_parentId = new JTextField( "0");
    private JTextField 	m_transmit = new JTextField( "1");
    private JTextField 	m_blockOrder = new JTextField( "0");
    private JTextField 	m_sweepToFill = new JTextField( "0");
    private JTextField 	m_displaySize = new JTextField( "0");
    private JTextField 	m_triggerMethod = new JTextField( "0");
    private JTextField 	m_ignoreRth = new JTextField( "0");
    private JTextField 	m_onlyRth = new JTextField( "0");
    private JTextField 	m_hidden = new JTextField( "0");
    private JTextField 	m_discretionaryAmt = new JTextField( "0");
    private JTextField 	m_shortSaleSlot = new JTextField( "0");
    private JTextField 	m_designatedLocation = new JTextField();

    private JTextField  m_rule80A = new JTextField();
    private JTextField  m_settlingFirm = new JTextField();
    private JTextField  m_allOrNone = new JTextField();
    private JTextField  m_overridePercentageConstraints = new JTextField();
    private JTextField  m_minQty = new JTextField();
    private JTextField  m_percentOffset = new JTextField();
    private JTextField  m_eTradeOnly = new JTextField();
    private JTextField  m_firmQuoteOnly = new JTextField();
    private JTextField  m_nbboPriceCap = new JTextField();
    private JTextField  m_auctionStrategy = new JTextField("0");
    private JTextField  m_startingPrice = new JTextField();
    private JTextField  m_stockRefPrice = new JTextField();
    private JTextField  m_delta = new JTextField();
    private JTextField  m_BOXstockRangeLower = new JTextField();
    private JTextField  m_BOXstockRangeUpper = new JTextField();

    private JTextField  m_VOLVolatility = new JTextField();
    private JTextField  m_VOLVolatilityType = new JTextField();
    private JTextField  m_VOLDeltaNeutralOrderType = new JTextField();
    private JTextField  m_VOLDeltaNeutralAuxPrice = new JTextField();
    private JTextField  m_VOLContinuousUpdate = new JTextField();
    private JTextField  m_VOLReferencePriceType = new JTextField();
    private JTextField  m_trailStopPrice = new JTextField();
    
    private JTextField 	m_scaleNumComponents = new JTextField();
    private JTextField 	m_scaleComponentSize = new JTextField();
    private JTextField 	m_scalePriceIncrement = new JTextField();

    private JButton 	btnOK = new JButton( "OK" );
    private JButton 	btnCancel = new JButton( "Cancel" );

	public DlgOrderExt( DlgOrder owner )
		{
		super( owner, true);

        setTitle( "Extended Order Details");

        // create extended order attributes panel
        JPanel extOrderDetailsPanel = new JPanel( new GridLayout( 0, 4, 10, 10) );
        extOrderDetailsPanel.setBorder( BorderFactory.createTitledBorder( "Extended Order Info") );
        extOrderDetailsPanel.add( new JLabel( "TIF") );
        extOrderDetailsPanel.add( m_tif);
        extOrderDetailsPanel.add( new JLabel( "OCA Group") );
        extOrderDetailsPanel.add( m_ocaGroup);
        extOrderDetailsPanel.add( new JLabel( "OCA Type") );
        extOrderDetailsPanel.add( m_ocaType);
        extOrderDetailsPanel.add( new JLabel( "Account") );
        extOrderDetailsPanel.add( m_account);
        extOrderDetailsPanel.add( new JLabel( "Open/Close") );
        extOrderDetailsPanel.add( m_openClose);
        extOrderDetailsPanel.add( new JLabel( "Origin") );
        extOrderDetailsPanel.add( m_origin);
        extOrderDetailsPanel.add( new JLabel( "OrderRef") );
        extOrderDetailsPanel.add( m_orderRef);
        extOrderDetailsPanel.add( new JLabel( "Parent Id") );
        extOrderDetailsPanel.add( m_parentId);
        extOrderDetailsPanel.add( new JLabel( "Transmit") );
        extOrderDetailsPanel.add( m_transmit);
        extOrderDetailsPanel.add( new JLabel( "Block Order") );
        extOrderDetailsPanel.add( m_blockOrder);
        extOrderDetailsPanel.add( new JLabel( "Sweep To Fill") );
        extOrderDetailsPanel.add( m_sweepToFill);
        extOrderDetailsPanel.add( new JLabel( "Display Size") );
        extOrderDetailsPanel.add( m_displaySize);
        extOrderDetailsPanel.add( new JLabel( "Trigger Method") );
        extOrderDetailsPanel.add( m_triggerMethod);
        extOrderDetailsPanel.add( new JLabel( "Ignore Regular Trading Hours") );
        extOrderDetailsPanel.add( m_ignoreRth);
        extOrderDetailsPanel.add( new JLabel( "Regular Trading Hours Only") );
        extOrderDetailsPanel.add( m_onlyRth);
        extOrderDetailsPanel.add( new JLabel( "Hidden") );
        extOrderDetailsPanel.add( m_hidden);
        extOrderDetailsPanel.add( new JLabel( "Discretionary Amt") );
        extOrderDetailsPanel.add( m_discretionaryAmt);
        extOrderDetailsPanel.add( new JLabel( "Trail Stop Price") );
        extOrderDetailsPanel.add( m_trailStopPrice);
        extOrderDetailsPanel.add( new JLabel( "Institutional Short Sale Slot") );
        extOrderDetailsPanel.add( m_shortSaleSlot);
        extOrderDetailsPanel.add( new JLabel( "Institutional Designated Location") );
        extOrderDetailsPanel.add( m_designatedLocation);
        extOrderDetailsPanel.add( new JLabel( "Rule 80 A") );
        extOrderDetailsPanel.add(m_rule80A);
        extOrderDetailsPanel.add(new JLabel("Settling Firm"));
        extOrderDetailsPanel.add(m_settlingFirm);
        extOrderDetailsPanel.add(new JLabel("All or None"));
        extOrderDetailsPanel.add(m_allOrNone);
        extOrderDetailsPanel.add(new JLabel("Override Percentage Constraints"));
        extOrderDetailsPanel.add(m_overridePercentageConstraints);
        extOrderDetailsPanel.add(new JLabel("Minimum Quantity"));
        extOrderDetailsPanel.add(m_minQty);
        extOrderDetailsPanel.add(new JLabel("Percent Offset"));
        extOrderDetailsPanel.add(m_percentOffset);
        extOrderDetailsPanel.add(new JLabel("Electronic Exchange Only"));
        extOrderDetailsPanel.add(m_eTradeOnly);
        extOrderDetailsPanel.add(new JLabel("Firm Quote Only"));
        extOrderDetailsPanel.add(m_firmQuoteOnly);
        extOrderDetailsPanel.add(new JLabel("NBBO Price Cap"));
        extOrderDetailsPanel.add(m_nbboPriceCap);
        extOrderDetailsPanel.add( new JLabel( "") );
        extOrderDetailsPanel.add( new JLabel(""));
        extOrderDetailsPanel.add(new JLabel("BOX: Auction Strategy"));
        extOrderDetailsPanel.add(m_auctionStrategy);
        extOrderDetailsPanel.add(new JLabel("BOX: Starting Price"));
        extOrderDetailsPanel.add(m_startingPrice);
        extOrderDetailsPanel.add(new JLabel("BOX: Stock Reference Price"));
        extOrderDetailsPanel.add(m_stockRefPrice);
        extOrderDetailsPanel.add(new JLabel("BOX: Delta"));
        extOrderDetailsPanel.add(m_delta);
        extOrderDetailsPanel.add(new JLabel("BOX or VOL: Stock Range Lower"));
        extOrderDetailsPanel.add(m_BOXstockRangeLower);
        extOrderDetailsPanel.add(new JLabel("BOX or VOL: Stock Range Upper"));
        extOrderDetailsPanel.add(m_BOXstockRangeUpper);

        extOrderDetailsPanel.add(new JLabel("VOL: Volatility"));
        extOrderDetailsPanel.add(m_VOLVolatility);
        extOrderDetailsPanel.add(new JLabel("VOL: Volatility Type (1 or 2)"));
        extOrderDetailsPanel.add(m_VOLVolatilityType);
        extOrderDetailsPanel.add(new JLabel("VOL: Hedge Delta Order Type"));
        extOrderDetailsPanel.add(m_VOLDeltaNeutralOrderType);
        extOrderDetailsPanel.add(new JLabel("VOL: Hedge Delta Aux Price"));
        extOrderDetailsPanel.add(m_VOLDeltaNeutralAuxPrice);
        extOrderDetailsPanel.add(new JLabel("VOL: Continuously Update Price (0 or 1)"));
        extOrderDetailsPanel.add(m_VOLContinuousUpdate);
        extOrderDetailsPanel.add(new JLabel("VOL: Reference Price Type (1 or 2)"));
        extOrderDetailsPanel.add(m_VOLReferencePriceType);
        
        extOrderDetailsPanel.add(new JLabel("SCALE: Scale # Comps"));
        extOrderDetailsPanel.add(m_scaleNumComponents);
        extOrderDetailsPanel.add(new JLabel("SCALE: Scale Comp Size"));
        extOrderDetailsPanel.add(m_scaleComponentSize);
        extOrderDetailsPanel.add(new JLabel("SCALE: Scale Price Increment"));
        extOrderDetailsPanel.add(m_scalePriceIncrement);

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( btnOK);
        buttonPanel.add( btnCancel);

        // create action listeners
        btnOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onOk();
            }
        });
        btnCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onCancel();
            }
        });

        // create dlg box
        getContentPane().add( extOrderDetailsPanel, BorderLayout.CENTER);
        getContentPane().add( buttonPanel, BorderLayout.SOUTH);
        pack();
    }

	public void showIt( Order destOrder )
		{
		m_destOrder = destOrder;
		pack();
		m_rc = false;
		setVisible(true);
		}

	public void showIt()
		{
		showIt(null);
		}

	void onOk()
		{
		m_rc = false;

		try		// set extended order fields
			{
			m_order.m_tif = m_tif.getText().trim();
			m_order.m_ocaGroup = m_ocaGroup.getText().trim();
			m_order.m_ocaType = parseInt( m_ocaType);
			m_order.m_account = m_account.getText().trim();
			m_order.m_openClose = m_openClose.getText().trim();
			m_order.m_origin = parseInt( m_origin );
			m_order.m_orderRef = m_orderRef.getText().trim();
			m_order.m_parentId = parseInt( m_parentId);
			m_order.m_transmit = parseInt(m_transmit) != 0;
			m_order.m_blockOrder = parseInt(m_blockOrder) != 0;
			m_order.m_sweepToFill = parseInt(m_sweepToFill) != 0;
			m_order.m_displaySize = parseInt( m_displaySize);
			m_order.m_triggerMethod = parseInt( m_triggerMethod);
			m_order.m_ignoreRth = parseInt(m_ignoreRth) != 0;
			m_order.m_rthOnly = parseInt(m_onlyRth) != 0;
			m_order.m_hidden = parseInt(m_hidden) != 0;
			m_order.m_discretionaryAmt = parseDouble( m_discretionaryAmt);
			m_order.m_shortSaleSlot = parseInt( m_shortSaleSlot );
			m_order.m_designatedLocation = m_designatedLocation.getText().trim();
			m_order.m_rule80A = m_rule80A.getText().trim();
			m_order.m_settlingFirm = m_settlingFirm.getText().trim();
			m_order.m_allOrNone = parseInt(m_allOrNone) != 0;
			m_order.m_minQty = parseMaxInt(m_minQty);
			m_order.m_overridePercentageConstraints =
                parseInt(m_overridePercentageConstraints) != 0;
			m_order.m_percentOffset = parseMaxDouble(m_percentOffset);
			m_order.m_eTradeOnly = parseInt(m_eTradeOnly) != 0;
			m_order.m_firmQuoteOnly = parseInt(m_firmQuoteOnly) != 0;
			m_order.m_nbboPriceCap = parseMaxDouble(m_nbboPriceCap);
			m_order.m_auctionStrategy = parseInt(m_auctionStrategy);
			m_order.m_startingPrice = parseMaxDouble(m_startingPrice);
			m_order.m_stockRefPrice = parseMaxDouble(m_stockRefPrice);
			m_order.m_delta = parseMaxDouble(m_delta);
			m_order.m_stockRangeLower = parseMaxDouble(m_BOXstockRangeLower);
			m_order.m_stockRangeUpper = parseMaxDouble(m_BOXstockRangeUpper);
			m_order.m_volatility = parseMaxDouble(m_VOLVolatility);
			m_order.m_volatilityType = parseMaxInt(m_VOLVolatilityType);
			m_order.m_deltaNeutralOrderType = m_VOLDeltaNeutralOrderType.getText().trim();
			m_order.m_deltaNeutralAuxPrice = parseMaxDouble(m_VOLDeltaNeutralAuxPrice);
			m_order.m_continuousUpdate = parseInt(m_VOLContinuousUpdate);
			m_order.m_referencePriceType = parseMaxInt(m_VOLReferencePriceType);
			m_order.m_trailStopPrice = parseMaxDouble(m_trailStopPrice);

			m_order.m_scaleNumComponents = parseMaxInt(m_scaleNumComponents);
			m_order.m_scaleComponentSize = parseMaxInt(m_scaleComponentSize);
			m_order.m_scalePriceIncrement = parseMaxDouble(m_scalePriceIncrement);
			}
		catch( Exception e )
			{
			SBDialog.inputError( "Error - " + e);
			return;
			}
		if ( m_destOrder != null )
			copyExtendedOrderDetails( m_destOrder, m_order );

		m_rc = true;
		setVisible( false);
		}

	private int parseMaxInt(JTextField textField)
		{
		String text = textField.getText().trim();
		return (text.length() == 0) ? Integer.MAX_VALUE
														: 	Integer.parseInt(text);
		}

    private double parseMaxDouble(JTextField textField) {
        String text = textField.getText().trim();
        if (text.length() == 0) {
            return Double.MAX_VALUE;
            }
        else {
            return Double.parseDouble(text);
        }
    }

    private int parseInt(JTextField textField) {
        String text = textField.getText().trim();
        if (text.length() == 0) {
            return 0;
            }
        else {
            return Integer.parseInt(text);
        }
    }

    private double parseDouble(JTextField textField) {
        String text = textField.getText().trim();
        if (text.length() == 0) {
            return 0;
            }
        else {
            return Double.parseDouble(text);
        }
    }

	public static final void copyExtendedOrderDetails( Order dest, Order src )
		{
		dest.m_tif = src.m_tif;
		dest.m_ocaGroup = src.m_ocaGroup;
		dest.m_ocaType = src.m_ocaType;
		dest.m_account = src.m_account;
		dest.m_openClose = src.m_openClose;
		dest.m_origin = src.m_origin;
		dest.m_orderRef = src.m_orderRef;
		dest.m_transmit = src.m_transmit;
		dest.m_parentId = src.m_parentId;
		dest.m_blockOrder = src.m_blockOrder;
		dest.m_sweepToFill = src.m_sweepToFill;
		dest.m_displaySize = src.m_displaySize;
		dest.m_triggerMethod = src.m_triggerMethod;
		dest.m_ignoreRth = src.m_ignoreRth;
		dest.m_rthOnly = src.m_rthOnly;
		dest.m_hidden = src.m_hidden;
		dest.m_discretionaryAmt = src.m_discretionaryAmt;
		dest.m_goodAfterTime = src.m_goodAfterTime;
		dest.m_shortSaleSlot = src.m_shortSaleSlot;
		dest.m_designatedLocation = src.m_designatedLocation;
		dest.m_ocaType = src.m_ocaType;
		dest.m_rthOnly = src.m_rthOnly;
		dest.m_rule80A = src.m_rule80A;
		dest.m_settlingFirm = src.m_settlingFirm;
		dest.m_allOrNone = src.m_allOrNone;
		dest.m_minQty = src.m_minQty;
		dest.m_percentOffset = src.m_percentOffset;
		dest.m_eTradeOnly = src.m_eTradeOnly;
		dest.m_firmQuoteOnly = src.m_firmQuoteOnly;
		dest.m_nbboPriceCap = src.m_nbboPriceCap;
		dest.m_auctionStrategy = src.m_auctionStrategy;
		dest.m_startingPrice = src.m_startingPrice;
		dest.m_stockRefPrice = src.m_stockRefPrice;
		dest.m_delta = src.m_delta;
		dest.m_stockRangeLower = src.m_stockRangeLower;
		dest.m_stockRangeUpper = src.m_stockRangeUpper;
		dest.m_overridePercentageConstraints = src.m_overridePercentageConstraints;
		dest.m_volatility = src.m_volatility;
		dest.m_volatilityType = src.m_volatilityType;
		dest.m_deltaNeutralOrderType = src.m_deltaNeutralOrderType;
		dest.m_deltaNeutralAuxPrice = src.m_deltaNeutralAuxPrice;
		dest.m_continuousUpdate = src.m_continuousUpdate;
		dest.m_referencePriceType = src.m_referencePriceType;
		dest.m_trailStopPrice = src.m_trailStopPrice;
		dest.m_scaleNumComponents = src.m_scaleNumComponents;
		dest.m_scaleComponentSize = src.m_scaleComponentSize;
		dest.m_scalePriceIncrement = src.m_scalePriceIncrement;
		}

	void onCancel()
		{
		m_rc = false;
		setVisible( false);
		}
	}