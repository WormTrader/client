package com.wormtrader.client;
/*
* PreferencesPanel.java
*
*/
import com.shanebow.ui.SBDialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PreferencesPanel extends JPanel
	implements ActionListener
	{
	public static int LAST_CLIENT_ID = 0;
	private static final String[] NEWS_CHOICES =
											{ "No bulletins", "New messages", "All Messages", };

	// Logging
	private JCheckBox	chkAccount  = new JCheckBox( "Account" );
	private JCheckBox	chkBars = new JCheckBox( "Bars" );
	private JCheckBox	chkOrders  = new JCheckBox( "Orders" );
	private JCheckBox	chkTickOther = new JCheckBox( "Tick Other" );
	private JCheckBox	chkTickOption = new JCheckBox( "Tick Options" );
	private JCheckBox	chkTickPrice = new JCheckBox( "Tick Price" );
	private JCheckBox	chkTickSize = new JCheckBox( "Tick Size" );

	// Network
	private JTextField 	tfIPAddress = new JTextField ( 15 );
	private JTextField 	tfPort = new JTextField ( 4 );

	// Other
	private JTextField	tfLevel2Depth = new JTextField( 3 );
//	public LogLevelSelector m_cbLogLevel = new LogLevelSelector();
	private JComboBox	cbNewsAmount = new JComboBox ( NEWS_CHOICES );
	private JTextField tfTickTags = new JTextField();

	private JButton 		btnUpdate = new JButton( "Update" );

	public PreferencesPanel()
		{
		super();
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS));
		Dispatcher d = Dispatcher.getInstance();

		add( network(d));
		add( logging(d));
		add( other(d));

		JPanel buttonPanel = new JPanel();
		buttonPanel.add( btnUpdate );
		btnUpdate.setToolTipText( "Apply new settings" );

		// create action listeners
		btnUpdate.addActionListener(this);

		// put it all together
	/*********
		add( pMidPanel, BorderLayout.CENTER);
		add( buttonPanel, BorderLayout.NORTH);
	*********/
		}

	private JPanel logging(Dispatcher d)
		{
		JPanel p = new JPanel( new GridLayout(0,2));
		p.setBorder( BorderFactory.createTitledBorder( "Logging" ));
		addChk ( p, chkAccount,    d.m_logAccount );
		addChk ( p, chkOrders,     d.m_logOrders );
		addChk ( p, chkBars,       d.m_logBars );
		addChk ( p, chkTickPrice,  d.m_logTickPrice );
		addChk ( p, chkTickSize,   d.m_logTickSize );
		addChk ( p, chkTickOption, d.m_logTickOption );
		addChk ( p, chkTickOther,  d.m_logTickOther );
//		addLabeledComboBox ( p, "Server Log Level", m_cbLogLevel, 0);
		return p;
		}

	private JPanel network(Dispatcher d)
		{
		JPanel p = new JPanel( new GridLayout(0,2));
		p.setBorder( BorderFactory.createTitledBorder( "Network" ));
		addLabeledTextfield( p, "IP Address:", tfIPAddress, d.m_ipAddress);
		addLabeledTextfield( p, "Port:",       tfPort,   "" + d.m_port);
		return p;
		}

	private JPanel other(Dispatcher d)
		{
		JPanel p = new JPanel( new GridLayout(0,2));
		p.setBorder( BorderFactory.createTitledBorder( "Other" ));
		addLabeledComboBox ( p, "News",           cbNewsAmount, d.m_newsLevel);
		addLabeledTextfield( p, "Level II Depth", tfLevel2Depth,
																					"" + d.m_level2Depth );
		addLabeledTextfield( p, "Tick Tags",      tfTickTags,
																					d.m_genericTicks );
		tfTickTags.setToolTipText( "Available: "
										+ Dispatcher.ALL_GENERIC_TICK_TAGS );
		return p;
		}

	private void addChk( JPanel p, JCheckBox chk, boolean checked )
		{
		p.add ( chk );
		chk.addActionListener(this);
		chk.setSelected( checked );
		}

	private void addLabeledComboBox( JPanel p, String label, JComboBox c, int select )
		{
		p.add ( new JLabel(label)); p.add ( c );
		c.setSelectedIndex( select );
		c.addActionListener(this);
		}

	private void addLabeledTextfield( JPanel p, String label, JTextField tf, String text )
		{
		p.add ( new JLabel(label)); p.add ( tf );
		tf.setText( text );
		tf.addActionListener(this);
		}

	// implement ActionListener
	public void actionPerformed( ActionEvent e)
		{
String cmd = e.getActionCommand();
System.out.println( cmd );
		Object src = e.getSource();

		if ( src.equals(btnUpdate))			onUpdate();
		else if ( src.equals ( cbNewsAmount ))
			Dispatcher.getInstance().setNewsLevel(
								(byte)cbNewsAmount.getSelectedIndex());
else onUpdate();
		}

	private void onUpdate()
		{
		Dispatcher d = Dispatcher.getInstance();
		d.m_logAccount = chkAccount.isSelected();
		d.m_logBars = chkBars.isSelected();
		d.m_logOrders = chkOrders.isSelected();
		d.m_logTickOther = chkTickOther.isSelected();
		d.m_logTickOption = chkTickOption.isSelected();
		d.m_logTickPrice = chkTickPrice.isSelected();
		d.m_logTickSize = chkTickSize.isSelected();
		d.m_genericTicks = tfTickTags.getText();
		d.m_ipAddress = tfIPAddress.getText();
		try { d.m_port = Integer.parseInt( tfPort.getText()); }
		catch (Exception e) { SBDialog.inputError( "Port must be an integer." ); }
		try { d.m_level2Depth = Integer.parseInt( tfLevel2Depth.getText()); }
		catch (Exception e) { SBDialog.inputError( "Level II must be an integer." ); }
		}
	}
