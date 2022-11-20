package com.wormtrader.client;
/*
 * LogLevelSelector.java
 *
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import com.wormtrader.client.Dispatcher;

public class LogLevelSelector extends JComboBox
	implements ActionListener
	{
	public static int DISCONNECTED = 0;
	public static int SYSTEM_LOG = 1;
 	public static int ERROR_LOG = 2;
	public static int WARN_LOG = 3;
	public static int INFO_LOG = 4;
	public static int DETAIL_LOG = 5;

	private int				m_serverLogLevel = SYSTEM_LOG;
	private Dispatcher	m_broker = null;

	private static String[] choices =
		{
		"Disconnected",
		"System",
		"Error",
		"Warning",
		"Information",
		"Detail"
		};

	public LogLevelSelector()
		{
		super( choices );
		addActionListener( this );
		m_broker = Dispatcher.getInstance();
		}

	public void informServer ()
		{
		if ( m_broker == null || !m_broker.isConnected())
			{
			setSelectedIndex( DISCONNECTED );
			return;
			}
		setSelectedIndex( m_serverLogLevel );
		m_broker.setServerLogLevel( m_serverLogLevel );
		}

	public void actionPerformed( ActionEvent e )
		{
		int selected = getSelectedIndex();
		if ( selected != DISCONNECTED )
			{
			m_serverLogLevel = selected;
			informServer ();
			}
		}
	}