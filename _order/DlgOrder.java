package com.wormtrader.brain.AutoTrader;
/*
 * DlgOrder.java
 *
 */
import com.ib.client.ComboLeg;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.client.Dispatcher;
import com.shanebow.ui.SBDialog;
import com.wormtrader.client.SBGridBagPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DlgOrder extends JDialog
	implements ActionListener
	{
	private static final int COL1_WIDTH = 30;
	private static final int COL2_WIDTH = 100 - COL1_WIDTH;

	public static final int CONTRACT = 0;
	public static final int ORDER = 1;
	public static final int OPT_EXERCISE = 2;

	final static int SIDE_ASK = 0;
	final static int SIDE_BID = 1;

	private SBGridBagPanel[] m_subPanel;
	private static int m_currentOperation;
	private static String[] m_titles =
												{
												"Contract",
												"Order",
												"Option Exercise",
												};

	public boolean		m_rc;

	public Contract	m_contract = null;
	public Order		m_order = new Order();
	public int			m_exerciseAction;
	public int			m_exerciseQuantity;
	public int			m_override;

	private EditContract	m_contractPanel;

	private JTextField 	m_action = new JTextField( "BUY" );
	private JTextField 	m_totalQuantity = new JTextField( "100" );
	private JTextField 	m_orderType = new JTextField( "LMT" );
	private JTextField 	m_lmtPrice = new JTextField( "40" );
	private JTextField 	m_auxPrice = new JTextField( "0" );
	private JTextField 	m_goodAfterTime = new JTextField();
	private JTextField 	m_goodTillDate = new JTextField();

	private JTextField		tfExerciseAction = new JTextField("1");
	private JTextField		tfExerciseQty = new JTextField("1");
	private JTextField		tfOverride = new JTextField("0");

	private JButton			btnComboLegs = new JButton( "Combo Legs..." );
	private JButton			btnMore = new JButton( "More..." );
	private JButton			btnOK = new JButton( "OK" );
	private JButton			btnCancel = new JButton( "Cancel" );

	private Dispatcher		m_broker = null;

	public DlgOrder( java.awt.Frame owner )
		{
		super( owner, true );
		setTitle( "Order Dialog" );

		m_broker = Dispatcher.getInstance();

		// create sub panels
		m_subPanel = new SBGridBagPanel[m_titles.length];
		m_subPanel[CONTRACT]		= new EditContract( m_contract, COL1_WIDTH, COL2_WIDTH );
		m_subPanel[ORDER]			= createOrderPanel( m_titles[ORDER] );
		m_subPanel[OPT_EXERCISE] = createOptionsExercisePanel( m_titles[OPT_EXERCISE] );

		// create mid Panel
		JPanel pMidPanel = new JPanel();
		pMidPanel.setLayout( new BoxLayout( pMidPanel, BoxLayout.Y_AXIS ));
		for ( int i = 0; i < m_subPanel.length; i++ )
			pMidPanel.add( m_subPanel[i], BorderLayout.CENTER );

		// create button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add( btnOK );
		buttonPanel.add( btnCancel );

		btnComboLegs.addActionListener( this );
		btnMore.addActionListener( this );
		btnOK.addActionListener( this );
		btnCancel.addActionListener( this );

		// create dlg box
		getContentPane().add( pMidPanel, BorderLayout.CENTER );
		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
		pack();
		}

	public void actionPerformed( ActionEvent e )
		{
		Object src = e.getSource();

		if ( src.equals ( btnCancel ))         onCancel();
		else if ( src.equals ( btnOK ))        onOk();
		else if ( src.equals ( btnMore ))      onMore();
		else if ( src.equals ( btnComboLegs )) onAddComboLegs();
		}

	private SBGridBagPanel createOrderPanel( String title )
		{
		SBGridBagPanel gbp = new SBGridBagPanel( title, COL1_WIDTH, COL2_WIDTH );
		gbp.addRow( "Action", m_action);
		gbp.addRow( "Total Order Size", m_totalQuantity );
		gbp.addRow( "Order Type", m_orderType );
		gbp.addRow( "Lmt Price", m_lmtPrice );
		gbp.addRow( "Aux Price", m_auxPrice );
		gbp.addRow( "Good After Time", m_goodAfterTime);
		gbp.addRow( "Good Till Date", m_goodTillDate);

		JPanel btnPanel = new JPanel();
		btnPanel.add( btnMore );
		btnPanel.add( btnComboLegs );
		gbp.addRow((String)null, btnPanel );
		return gbp;
		}

	private SBGridBagPanel createOptionsExercisePanel( String title )
		{
		SBGridBagPanel gbp = new SBGridBagPanel( title, COL1_WIDTH, COL2_WIDTH );
		gbp.addRow( "Action (1 or 2)",   tfExerciseAction);
		gbp.addRow( "# of Contracts",    tfExerciseQty);
		gbp.addRow( "Override (0 or 1)", tfOverride);
		return gbp;
		}

	void onAddComboLegs()
		{
		m_contract = ((EditContract)(m_subPanel[CONTRACT])).getContract();
		String exchange = "Fix this";	// TODO: get from the contract panel
		DlgComboLeg comboLegDlg
				= new DlgComboLeg( m_contract.m_comboLegs, exchange, this );
		comboLegDlg.setVisible( true);
		}

	void onMore()	// Show the extended order attributes dialog
		{
		DlgOrderExt dlgExtOrd = new DlgOrderExt( this );
		dlgExtOrd.showIt( m_order );
		}

	void onOk()
		{
		m_rc = false;

		try
			{
			// set contract fields
			m_contract = ((EditContract)(m_subPanel[CONTRACT])).getContract();

			// set order fields
			m_order.m_action = m_action.getText();
			m_order.m_totalQuantity = Integer.parseInt( m_totalQuantity.getText());
			m_order.m_orderType = m_orderType.getText();
			m_order.m_lmtPrice = Double.parseDouble( m_lmtPrice.getText());
			m_order.m_auxPrice = Double.parseDouble( m_auxPrice.getText());
			// m_order.m_sharesAllocation = m_sharesAllocProfile;
			m_order.m_goodAfterTime = m_goodAfterTime.getText();
			m_order.m_goodTillDate = m_goodTillDate.getText();

			m_order.m_faGroup = null; // see FAAllocationInfoDlg
			m_order.m_faProfile = null;
			m_order.m_faMethod = null;
			m_order.m_faPercentage = null;

			// set Options exercise fields
			m_exerciseAction = Integer.parseInt( tfExerciseAction.getText());
			m_exerciseQuantity = Integer.parseInt( tfExerciseQty.getText());
			m_override = Integer.parseInt( tfOverride.getText());
			}
		catch( Exception e)
			{
			SBDialog.inputError( "Error - " + e );
			return;
			}

		m_rc = true;
		switch ( m_currentOperation )
			{
			case CONTRACT:			m_broker.reqContractDetails( m_contract );
											break;

			case ORDER:				m_broker.placeOrder( m_contract, m_order );
											break;

			case OPT_EXERCISE:	m_broker.exerciseOptions ( m_contract,
																						m_exerciseAction,
																						m_exerciseQuantity,
																						m_order.m_account,
																						m_override );
			default:					break;
			}
		setVisible( false );
		}

	void onCancel()
		{
		m_rc = false;
		setVisible( false );
		}

	public void showIt( int curOperation )
		{
		m_currentOperation = curOperation;
		for ( int i = 1; i < m_subPanel.length; i++ )
			m_subPanel[i].setVisible( i == curOperation );
		setTitle( m_titles[curOperation] + " Dialog" );

		pack();
		m_rc = false;
		setVisible( true );
		}

	public void showIt()
		{
		this.showIt( ORDER );
		}

	public void showIt( PositionLeg leg )
		{
		EditContract contractPanel = (EditContract)m_subPanel[CONTRACT];
		contractPanel.show ( leg );
		this.showIt( ORDER );
		}
	}
// 278