package com.wormtrader.brain.AutoTrader;
/*
 * TabAccount.java
 *
 */
import com.wormtrader.client.Dispatcher;
import com.wormtrader.client.AccountDataListener;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class TabAccount extends JPanel
	implements com.shanebow.ui.SBSelectable
	{
	public TabAccount()
		{
		super( new BorderLayout());
		AcctValueModel acctValueModel = new AcctValueModel();
		Dispatcher.getInstance().addAccountDataListener(acctValueModel);
		JTable table = new JTable(acctValueModel);
		acctValueModel.init( table );
		JScrollPane acctPane = new JScrollPane( table );
		setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		add( acctPane, BorderLayout.CENTER);
		}

	// implement com.shanebow.ui.SBSelectable
	public void onSelect () {}
	public void onDeselect () {}
	}

class AcctValueModel extends AbstractTableModel
	implements AccountDataListener
	{
	Vector<AccountTableRow> m_rows = new Vector<AccountTableRow>();
	int[]  colWidths = { 150, 100 };
	String[] columnNames = { "Key", "Value", };
	
	void init ( JTable table )
		{
		for ( int i = 0; i < columnNames.length; i++ )
			{
			javax.swing.table.TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(colWidths[i]);
			}
		m_rows.add(new AccountTableRow("Update Time", "", null));
		fireTableRowsInserted(0,0);
		}

	// Implement the AccountDataListener interface
	public void updatePortfolio( String symbol, String optDesc, int pos, double price,
				double value, 	double avgCost, double unrealPNL, double realPNL )
		{
		// do nothing: method required by AccountDataListener interface
		}
	public void updateAccountTime(String timeStamp)
		{
		m_rows.get(0).m_value = timeStamp;
		fireTableRowsUpdated(0,0);
		}

	public void updateAccountValue(String key, String value, String currency )
		{
		int size = m_rows.size();
		for ( int i = 1; i < size; i++ )
			{
			AccountTableRow row = m_rows.get(i);
			if ( row.m_key != null
			&&   row.m_key.equals(key))
			/************ ALL DOLLARS - skip this for now ***
			&& (( row.m_currency != null
						&& row.m_currency.equals(newData.m_currency))
				|| row.m_currency == null )
			**************/
				{
				row.m_value = value;
				fireTableRowsUpdated(i,i);
				return;
				}
			}
		// if we got here, this is a new key
		m_rows.add(new AccountTableRow(key, value, currency));
		fireTableDataChanged();
		}

	public int     getColumnCount() { return columnNames.length; }
	public String  getColumnName(int c) { return columnNames[c]; }
	public int     getRowCount() { return m_rows.size(); }
	public Object  getValueAt(int r, int c) { return m_rows.get(r).getValue(c); }
	public boolean isCellEditable(int r, int c) { return false; }

	class AccountTableRow
		{
		String m_key;
		String m_value;
		String m_currency;

		AccountTableRow ( String key, String val, String cur )
			{
			m_key = key;
			m_value = val;
			m_currency = cur;
			}

		Object getValue(int c)
			{
			switch (c)
				{
				case 0:		return m_key;
				case 1:		return m_value;
				case 2:		return m_currency;
				default:	return null;
				}
			}
		}
	} // 183