package com.wormtrader.client;
/********************************************************************
* @(#)SBGridBagPanel.java 1.00 2007????
* Copyright © 2007-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* SBGridBagPanel.java:
*
* @author Rick Salamone
* @version 1.00
* 2007???? rts created
* 20130216 rts moved to client package - only used here
*******************************************************/
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class SBGridBagPanel extends JPanel
	{
	private static final int GBC_RELATIVE = GridBagConstraints.RELATIVE;
	private static final int GBC_REMAINDER = GridBagConstraints.REMAINDER;
	private static final int DEFAULT_COL_WIDTH = 300;	// was 55
	private static final int DEFAULT_COL1_WIDTH = 100;	// was 30
	private static final int DEFAULT_COL2_WIDTH = DEFAULT_COL_WIDTH
																			 - DEFAULT_COL1_WIDTH;
	private int m_col1width;
	private int m_col2width;
	private static final Insets oneInsets = new Insets(1, 1, 1, 1);
	private GridBagLayout m_layout = new GridBagLayout();
	private GridBagConstraints m_gbc = new GridBagConstraints();

	public SBGridBagPanel()
		{
		this ( null, DEFAULT_COL1_WIDTH, DEFAULT_COL2_WIDTH );
		}

	public SBGridBagPanel( String title )
		{
		this ( title, DEFAULT_COL1_WIDTH, DEFAULT_COL2_WIDTH );
		}

	public SBGridBagPanel( int col1width, int col2width )
		{
		this ( null, col1width, col2width );
		}

	public SBGridBagPanel( String title, int col1width, int col2width )
		{
		setLayout(m_layout);
		m_col1width = col1width;
		m_col2width = col2width;
		
		m_gbc.fill = GridBagConstraints.BOTH;
		m_gbc.anchor = GridBagConstraints.CENTER;
		m_gbc.weighty = 100;
		m_gbc.fill = GridBagConstraints.BOTH;
		m_gbc.gridheight = 1;
		if ( title != null )
			setBorder( BorderFactory.createTitledBorder( title ));
		}

	public void addComponent(Component comp,	GridBagConstraints gbc,
																int weightx, int gridwidth)
		{
		gbc.weightx = weightx;
		gbc.gridwidth = gridwidth;
		setConstraints(comp, gbc);
		add(comp, gbc);
		}

	public final void addRow( String label, Component comp )
		{
		if ( label != null )
			addComponent( new JLabel( label ), m_gbc, m_col1width, GBC_RELATIVE);
		addComponent( comp, m_gbc, m_col2width, GBC_REMAINDER );
		}

	public final void addRow( JLabel label, Component comp )
		{
		if ( label != null )
			addComponent( label, m_gbc, m_col1width, GBC_RELATIVE);
		addComponent( comp, m_gbc, m_col2width, GBC_REMAINDER );
		}

	public final void addRow( String label, Component... comp )
		{
		JPanel col2 = new JPanel();
		for ( int i = 0; i < comp.length; i++ )
			col2.add( comp[i] );
		addRow( label, col2 );
		}

	public final void addRow( JLabel label, Component... comp )
		{
		JPanel col2 = new JPanel();
		for ( int i = 0; i < comp.length; i++ )
			col2.add( comp[i] );
		addRow( label, col2 );
		}

	public final void addEqualRow( String label, Component... comp )
		{
		GridLayout oneRowGrid = new GridLayout( 1, 0);
		JPanel col2 = new JPanel(oneRowGrid);
		for ( int i = 0; i < comp.length; i++ )
			col2.add( comp[i] );
		addRow( label, col2 );
		}

	public final void addEqualRow( JLabel label, Component... comp )
		{
		GridLayout oneRowGrid = new GridLayout( 1, 0);
		JPanel col2 = new JPanel(oneRowGrid);
		for ( int i = 0; i < comp.length; i++ )
			col2.add( comp[i] );
		addRow( label, col2 );
		}

	public void setConstraints(Component comp, GridBagConstraints constraints)
		{
		m_layout.setConstraints(comp, constraints);
		}

	public void SetObjectPlacement(Component c, int x, int y)
		{
		addToPane(c, x, y, 1, 1, 100, 100, oneInsets);
		}

	public void SetObjectPlacement(Component c, int x, int y, int w, int h)
		{
		addToPane( c, x, y, w, h, 100, 100, oneInsets );
		}

	public void SetObjectPlacement ( Component c, int x, int y, int w, int h,
															int xGrow, int yGrow )
		{
		addToPane(c, x, y, w, h, xGrow, yGrow, oneInsets );
		}

	public void SetObjectPlacement ( Component c, int x, int y, int w, int h,
															int xGrow, int yGrow, int fill )
		{
		addToPane(c, x, y, w, h, xGrow, yGrow, GridBagConstraints.WEST, fill, oneInsets );
		}

	public void SetObjectPlacement ( Component c, int x, int y, int w, int h,
															int xGrow, int yGrow, int anchor, int fill )
		{
		addToPane(c, x, y, w, h, xGrow, yGrow, anchor, fill, oneInsets );
		}

	public void SetObjectPlacement ( Component c, int x, int y, int w, int h,
															int xGrow, int yGrow, Insets insets )
		{
		addToPane(c, x, y, w, h, xGrow, yGrow, insets );
		}

	private void addToPane ( Component c, int x, int y, int w, int h,
												int xGrow, int yGrow, Insets insets )
		{
		addToPane(c, x, y, w, h, xGrow, yGrow, GridBagConstraints.WEST,
										GridBagConstraints.BOTH, insets );
		}

	private void addToPane( Component c, int x, int y, int w, int h,
											int xGrow, int yGrow, int anchor, int fill,
											Insets insets )
		{
		GridBagConstraints gbc = new GridBagConstraints();

        // the coordinates of the cell in the layout that contains
        // the upper-left corner of the component
        gbc.gridx = x;
        gbc.gridy = y;

        // the number of cells that this entry is going to take up
        gbc.gridwidth = w;
        gbc.gridheight = h;

        // drive how extra space is distributed among components.
        gbc.weightx = xGrow;
        gbc.weighty = yGrow;

        // drive how component is made larger if extra space is available for it
        gbc.fill = fill;

        // drive where, within the display area, to place the component when it
        // is larger than its display area.
        gbc.anchor = anchor;

        // drive the minimum amount of space between the component and the edges
        // of its display area
        gbc.insets = insets;

        add(c, gbc);
		}
	}