package com.wormtrader.brain.AutoTrader;
/*
 * DlgFAdvisor.java
 *
 */
import com.wormtrader.client.SBGridBagPanel;
import com.shanebow.ui.SBTextPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class DlgFAdvisor extends JDialog
						implements ActionListener
	{
	private int DIALOG_WIDTH = 500;
	private int EDITOR_HEIGHT = 240;
	private int BUTTON_PANEL_HEIGHT = 60;
	private JButton 	btnOK = new JButton( "OK");
	private JButton 	btnCancel = new JButton( "Cancel");
	private SBTextPanel groupTextEditor = new SBTextPanel("Groups", true);
	private SBTextPanel profileTextEditor = new SBTextPanel("Allocation Profiles", true);
	private SBTextPanel aliasTextEditor = new SBTextPanel("Aliases", true);
	String      groupsXML;
	String      profilesXML;
	String      aliasesXML;
	boolean 		m_rc = false;

	public DlgFAdvisor( Frame owner)
		{
		super( owner, "Financial Advisor", true);

		SBGridBagPanel editPanel = new SBGridBagPanel();

		editPanel.SetObjectPlacement( groupTextEditor,      0, 0 );
		editPanel.SetObjectPlacement( profileTextEditor,    0, 1 );
		editPanel.SetObjectPlacement( aliasTextEditor,      0, 2 );
		Dimension editPanelSizeDimension = new Dimension(DIALOG_WIDTH, 3 * EDITOR_HEIGHT);
		editPanel.setPreferredSize(editPanelSizeDimension);

		SBGridBagPanel buttonPanel = new SBGridBagPanel();
		buttonPanel.add( btnOK);
		buttonPanel.add( btnCancel);
		btnOK.addActionListener( this );
		btnCancel.addActionListener( this );

		//setTitle( "Financial Advisor");
		getContentPane().add( editPanel, BorderLayout.NORTH);
		getContentPane().add( buttonPanel, BorderLayout.CENTER);
		pack();
		}

	public void actionPerformed( ActionEvent e )
		{
		Object src = e.getSource();

		if ( src.equals ( btnOK ))					onOk();
		else if ( src.equals ( btnCancel ))	onCancel();
		}

	void receiveInitialXML(String p_groupsXML, String p_profilesXML, String p_aliasesXML)
		{
		groupTextEditor.setTextDetabbed(p_groupsXML);
		profileTextEditor.setTextDetabbed(p_profilesXML);
		aliasTextEditor.setTextDetabbed(p_aliasesXML);
		}

	void onOk()
		{
		m_rc = true;
		groupsXML = groupTextEditor.getText();
		profilesXML = profileTextEditor.getText();
		aliasesXML = aliasTextEditor.getText();
		setVisible( false );
		}

	void onCancel()
		{
		m_rc = false;
		setVisible( false);
		}
	}