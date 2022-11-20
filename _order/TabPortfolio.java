package com.wormtrader.brain.AutoTrader;
/*
 * TabPortfolio.java
 *
 */
import com.wormtrader.client.Dispatcher;
import com.wormtrader.client.AccountDataListener;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TabPortfolio extends JPanel
	{
	private PortfolioTable	m_portfolioModel = new PortfolioTable();

	public TabPortfolio()
		{
		super( new BorderLayout());
		Dispatcher.getInstance().addAccountDataListener(m_portfolioModel);
		JScrollPane portPane = new JScrollPane(new JTable(m_portfolioModel));
		add( portPane, BorderLayout.CENTER);
		}
	}

class PortfolioTable extends AbstractTableModel
	implements AccountDataListener
	{
	static final String[] columnNames =
		{ "Symbol", "Option", "Position",
				"Market Price", "Market Value", "Avg Cost", "Unrealized P&L",
					"Realized P&L", "Account" };

	Vector<PortfolioTableRow> m_rows = new Vector<PortfolioTableRow>();

	// Implement the AccountDataListener interface
	public void updateAccountTime(String timeStamp) {}
	public void updateAccountValue(String key, String value, String currency) {}
	public void updatePortfolio ( String symbol, String optDesc, int position,
											double marketPrice, double marketValue, double avgCost,
											double unrealizedPNL, double realizedPNL )
		{
		int size = m_rows.size();
		for ( int i = 0; i < size; i++ )
			{
			PortfolioTableRow row = m_rows.get(i);
			if ( row.equals( symbol, optDesc ))
				{
//				if ( newData.m_position == 0 ) // remove zero position rows
//					m_rows.remove(i);
//				else
					row.update ( position, marketPrice, marketValue, avgCost,
													unrealizedPNL, realizedPNL );
				fireTableRowsUpdated(i,i);
				return;
				}
			}
		m_rows.add(new PortfolioTableRow(symbol, optDesc, position,
																marketPrice, marketValue, avgCost,
																unrealizedPNL, realizedPNL ));
		fireTableDataChanged();
		}

	void reset() { m_rows.clear(); }
	public int getColumnCount() { return columnNames.length; }
	public String getColumnName(int c) { return columnNames[c]; }
	public int getRowCount() { return m_rows.size(); }
	public Object getValueAt(int r, int c) { return m_rows.get(r).getValue(c); }
	public boolean isCellEditable(int r, int c) { return false; }

	class PortfolioTableRow
		{
		String   m_symbol;
		String   m_optDesc;
		int      m_position;
		double   m_marketPrice;
		double   m_marketValue;
		double   m_averageCost;
		double   m_unrealizedPNL;
		double   m_realizedPNL;

		PortfolioTableRow( String symbol, String optDesc, int position,
										double marketPrice, double marketValue, double avgCost,
					double unrealPNL, double realPNL )
			{
			m_symbol = symbol;
			m_optDesc = optDesc;
			update(position, marketPrice, marketValue, avgCost, unrealPNL, realPNL );
			}

		void update ( int position, double marketPrice, double marketValue, double avgCost,
					double unrealizedPNL, double realizedPNL )
			{
			m_position = position;
			m_marketPrice = marketPrice;
			m_marketValue = marketValue;
			m_averageCost = avgCost;
			m_unrealizedPNL = unrealizedPNL;
			m_realizedPNL = realizedPNL;
			}

		boolean equals ( String symbol, String optDesc )
			{
			return symbol.equals(m_symbol) && optDesc.equals(m_optDesc);
			}

		Object getValue( int c )
			{
			switch ( c )
				{
				case 0:	 return m_symbol;
				case 1:	 return m_optDesc;
				case 2:	 return "" + m_position;
				case 3:	 return "" + m_marketPrice;
				case 4:	 return "" + m_marketValue;
				case 5:	 return "" + m_averageCost;
				case 6: return "" + m_unrealizedPNL;
				case 7: return "" + m_realizedPNL;
				default:	return null;
				}
			}
		}
	}
