package com.wormtrader.client;
/*
 * @(#)MarketDataListener.java	1.00 07/11/05
 *
 */

/**
 * Market Data Listener
 *
 * @version 1.00 11/05/07
 * @author Rick Salamone
 */
import java.util.Iterator;

public final class ListenerArray<E>
	implements Iterable<E>
	{
	private Object[] m_elements;


	public ListenerArray( int capacity )
		{
		m_elements = new Object[capacity];
		}

	public int size() { return m_elements.length; }

	@SuppressWarnings("unchecked")
	public E get(int i) { return (E)(m_elements[i]); }

	public int add(E e)
		{
		int i = 0;
		for ( ; i < m_elements.length; i++ )
			if ( m_elements[i] == null )
				{
				m_elements[i] = e;
				return i;
				}
		arrayExpand( 1 );
		m_elements[i] = e;
		return i;
		}

	public final void arrayExpand ( int capacityIncrement )
		{
		int newSize = m_elements.length + capacityIncrement;
		Object[] copyTo = new Object[newSize];
		System.arraycopy(m_elements, 0, copyTo, 0, m_elements.length);
		m_elements = copyTo;
		}

	public boolean remove(E e)
		{
		int i = 0;
		for ( ; i < m_elements.length; i++ )
			if ( m_elements[i] == e )
				{
				m_elements[i] = null;
				return true;
				}
		return false;
		}

	public Iterator<E> iterator()
		{
		return new Iterator<E>()
			{
			int i;
			// constructor
				{
				i = -1;
				while ( ++i < m_elements.length && m_elements[i] == null )
					;
				}
			public boolean hasNext()
				{
				return (i < m_elements.length && m_elements[i] != null );
				}
			@SuppressWarnings("unchecked")
			public E next()
				{
				E it = null;
				try
					{
					it = (E)m_elements[i];
					while ( ++i < m_elements.length && m_elements[i] == null )
						;
					}
				catch( Exception ex ) 
					{
					throw new java.util.NoSuchElementException ();
					}
				return it;
				}
			public void remove() {throw new UnsupportedOperationException("ListenerArray.remove()");}
			};
		}

	public static void main (String args[])
		{
		ListenerArray<String> a = new ListenerArray<String>(10);
		a.add( " element  0" );
		a.add( " element  1" );
		a.add( " element  2" );
		a.add( " element  3" );
		a.add( " element  4" );
		a.add( " element  5" );
		a.add( " element  6" );
		a.remove( " element  3" );
		for ( int i = 0; i < a.size(); i++ )
			System.out.format("%2d: %s\n", i, a.get(i) );
		if ( !a.remove( " element  7" ))
			System.out.println( "NOT FOUND:" + " element  7" );
		a.add( " NEW element  3" );
		for ( String s : a )
			System.out.println( s );
		for ( Iterator it = a.iterator(); it.hasNext(); )
			{
			String s = (String)(it.next());
			System.out.println( "next>" + s );
			if ( s.equals(" element  5"))
				it.remove();
			}
		}
	}



