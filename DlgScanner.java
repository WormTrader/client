/*
 * DlgScanner.java
 *
 */
package com.wormtrader.client;

import com.ib.client.ScannerSubscription;
import com.wormtrader.client.SBGridBagPanel;
import com.wormtrader.client.Dispatcher;
import com.shanebow.ui.SBDialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class DlgScanner extends JDialog
	implements ActionListener
	{
	private static final DlgScanner _instance = new DlgScanner();
	public static void display() { _instance.showIt(); }

	public static final int NO_SELECTION = 0;
    public static final int SUBSCRIBE_SELECTION = 1;
    public static final int CANCEL_SELECTION = 2;
    public static final int REQUEST_PARAMETERS_SELECTION = 3;

    public int          m_userSelection = NO_SELECTION;
    public int 			m_id;
    public ScannerSubscription m_subscription = new ScannerSubscription();

    private JTextField m_Id = new JTextField( "0");
    private JTextField m_numberOfRows = new JTextField("10");
    private JTextField m_instrument = new JTextField("STK");
    private JTextField m_locationCode = new JTextField("STK.US");
    private JTextField m_scanCode = new JTextField("HIGH_OPT_VOLUME_PUT_CALL_RATIO");
    private JTextField m_abovePrice = new JTextField("3");
    private JTextField m_belowPrice = new JTextField();
    private JTextField m_aboveVolume = new JTextField("0");
    private JTextField m_averageOptionVolumeAbove = new JTextField("0");
    private JTextField m_marketCapAbove = new JTextField("100000000");
    private JTextField m_marketCapBelow = new JTextField();
    private JTextField m_moodyRatingAbove = new JTextField();
    private JTextField m_moodyRatingBelow = new JTextField();
    private JTextField m_spRatingAbove = new JTextField();
    private JTextField m_spRatingBelow = new JTextField();
    private JTextField m_maturityDateAbove = new JTextField();
    private JTextField m_maturityDateBelow = new JTextField();
    private JTextField m_couponRateAbove = new JTextField();
    private JTextField m_couponRateBelow = new JTextField();
    private JTextField m_excludeConvertible = new JTextField("0");
    private JTextField m_scannerSettingPairs = new JTextField("Annual,true");
    private JTextField m_stockTypeFilter = new JTextField("ALL");

    private JButton 	btnRequestParams = new JButton( "Request Parameters");
    private JButton 	btnSubscribe = new JButton( "Subscribe");
    private JButton 	btnCancel = new JButton( "Cancel Subscription");

    private static final int COL1_WIDTH = 30;
    private static final int COL2_WIDTH = 100 - COL1_WIDTH;

	private DlgScanner()
		{
		super((java.awt.Frame)null, true );
		setTitle( "Scanner" );

		SBGridBagPanel pId = new SBGridBagPanel( "Message Id", COL1_WIDTH, COL2_WIDTH );
		pId.addRow( "Id", m_Id );

		SBGridBagPanel gbp = new SBGridBagPanel( "Subscription Info", COL1_WIDTH, COL2_WIDTH );
		gbp.addRow( "Number of Rows", m_numberOfRows );
		gbp.addRow( "Instrument",     m_instrument );
		gbp.addRow( "Location Code",  m_locationCode );
		gbp.addRow( "Scan Code",      m_scanCode );
		gbp.addRow( "Above Price",    m_abovePrice );
		gbp.addRow( "Below Price",    m_belowPrice );
		gbp.addRow( "Above Volume",   m_aboveVolume );
		gbp.addRow( "Avg Opt Volume Above", m_averageOptionVolumeAbove );
		gbp.addRow( "Market Cap Above", m_marketCapAbove );
		gbp.addRow( "Market Cap Below", m_marketCapBelow );
		gbp.addRow( "Moody Rating Above", m_moodyRatingAbove );
		gbp.addRow( "Moody Rating Below", m_moodyRatingBelow );
		gbp.addRow( "S & P Rating Above", m_spRatingAbove );
		gbp.addRow( "S & P Rating Below", m_spRatingBelow );
		gbp.addRow( "Maturity Date Above", m_maturityDateAbove );
		gbp.addRow( "Maturity Date Below", m_maturityDateBelow );
		gbp.addRow( "Coupon Rate Above",   m_couponRateAbove );
		gbp.addRow( "Coupon Rate Below",   m_couponRateBelow );
		gbp.addRow( "Exclude Convertible", m_excludeConvertible );
		gbp.addRow( "Scanner Setting Pairs", m_scannerSettingPairs );
		gbp.addRow( "Stock Type Filter",     m_stockTypeFilter );

		// create button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add( btnRequestParams);
		buttonPanel.add( btnSubscribe);
		buttonPanel.add( btnCancel);

		btnRequestParams.addActionListener( this );
		btnSubscribe.addActionListener( this );
		btnCancel.addActionListener( this );

		// create top panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.Y_AXIS) );
		topPanel.add( pId);
		topPanel.add( gbp);

		// create dlg box
		getContentPane().add( topPanel, BorderLayout.CENTER);
		getContentPane().add( buttonPanel, BorderLayout.SOUTH);
		pack();
		}

	public void actionPerformed( ActionEvent e)
		{
		Object src = e.getSource();
		if ( src.equals( btnRequestParams ))		onRequestParameters();
		else if ( src.equals( btnSubscribe ))		onSubscribe();
		else if ( src.equals( btnCancel ))			onCancelSubscription();
		}

    private static String pad( int val) {
        return val < 10 ? "0" + val : "" + val;
    }

	private double parseDouble(JTextField tf)
		{
		try { return Double.parseDouble(tf.getText().trim()); }
		catch (Exception ex) { return Double.MAX_VALUE; }
		}

	private int parseInt(JTextField tf )
		{
		try { return Integer.parseInt(tf.getText().trim()); }
		catch (Exception ex) { return Integer.MAX_VALUE; }
		}

	void onSubscribe()
		{
		m_userSelection = NO_SELECTION;

		try		// set id
			{
			m_id = Integer.parseInt( m_Id.getText().trim() );
			m_subscription.numberOfRows(parseInt(m_numberOfRows));
			m_subscription.instrument(m_instrument.getText().trim());
			m_subscription.locationCode(m_locationCode.getText().trim() );
			m_subscription.scanCode(m_scanCode.getText().trim() );
			m_subscription.abovePrice(parseDouble(m_abovePrice));
			m_subscription.belowPrice(parseDouble(m_belowPrice));
			m_subscription.aboveVolume(parseInt(m_aboveVolume));
			m_subscription.averageOptionVolumeAbove(parseInt(m_averageOptionVolumeAbove));
			m_subscription.marketCapAbove(parseDouble(m_marketCapAbove));
			m_subscription.marketCapBelow(parseDouble(m_marketCapBelow));
			m_subscription.moodyRatingAbove(m_moodyRatingAbove.getText().trim());
			m_subscription.moodyRatingBelow(m_moodyRatingBelow.getText().trim());
			m_subscription.spRatingAbove(m_spRatingAbove.getText().trim());
			m_subscription.spRatingBelow(m_spRatingBelow.getText().trim());
			m_subscription.maturityDateAbove(m_maturityDateAbove.getText().trim());
			m_subscription.maturityDateBelow(m_maturityDateBelow.getText().trim());
			m_subscription.couponRateAbove(parseDouble(m_couponRateAbove));
			m_subscription.couponRateBelow(parseDouble(m_couponRateBelow));
			m_subscription.excludeConvertible(m_excludeConvertible.getText().trim());
			m_subscription.scannerSettingPairs(m_scannerSettingPairs.getText().trim());
	//		m_subscription.stockTypeFilter(m_stockTypeFilter.getText().trim()); Peter ???
			}
		catch( Exception e)
			{
			SBDialog.inputError( "Error - " + e );
			return;
			}

		m_userSelection = SUBSCRIBE_SELECTION;
		Dispatcher.getInstance().reqScannerSubscription( m_id, m_subscription );

		setVisible( false);
		}

	void onRequestParameters()
		{
		m_userSelection = REQUEST_PARAMETERS_SELECTION;
		Dispatcher.getInstance().reqScannerParameters();
		setVisible( false );
		}

	void onCancelSubscription()
		{
		m_userSelection = CANCEL_SELECTION;
		m_id = Integer.parseInt( m_Id.getText().trim());
		setVisible( false );
		Dispatcher.getInstance().cancelScannerSubscription( m_id );
		}

	public void showIt()
		{
		m_userSelection = NO_SELECTION;
		setVisible(true);
		}
	}