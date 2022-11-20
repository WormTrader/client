To enable financial advisor / managed accounts put the following code into
Dispatcher.java:

~~~
	// Financial Advisor members
	private static final int faErrorCodes[] = { 503, 504, 505, 522, 1100,
																				EC_NOT_FA_ACCOUNT };
	public boolean		faError;
	public String		faGroupXML;
	public String		faProfilesXML;
	public String		faAliasesXML;
	public String		m_FAAcctCodes;
	public boolean		m_bIsFAAccount = false;


   	public void error( int id, int errorCode, String errorMsg )
	...
		for ( int ctr=0; ctr < faErrorCodes.length; ctr++ )
			faError |= (errorCode == faErrorCodes[ctr]);
	...

	public final void reqFinancialAdvisor()
		{
		faGroupXML = faProfilesXML = faAliasesXML = null;
		faError = false;
		m_client.requestFA( EClientSocket.GROUPS );
		m_client.requestFA( EClientSocket.PROFILES );
		m_client.requestFA( EClientSocket.ALIASES );
		}
	public final void reqManagedAccts()
		{
		m_client.reqManagedAccts();	// request list of managed accounts
		}
	public void receiveFA( int faDataType, String xml )
		{
		SBLog.error ( "receiveFA", "DISABLED" );
		displayXML(EWrapperMsgGenerator.FINANCIAL_ADVISOR
							+ " " + EClientSocket.faMsgTypeName(faDataType), xml);
		switch (faDataType)
			{
			case EClientSocket.GROUPS:		faGroupXML = xml;
															break;
			case EClientSocket.PROFILES:	faProfilesXML = xml;
															break;
			case EClientSocket.ALIASES:	faAliasesXML = xml;
															break;
			}

		if ( !faError
		&&   !(faGroupXML == null || faProfilesXML == null || faAliasesXML == null))
			{
			DlgFAdvisor dlg = new DlgFAdvisor((JFrame)m_frame);
			dlg.receiveInitialXML(faGroupXML, faProfilesXML, faAliasesXML);
			dlg.show();

			if ( !dlg.m_rc ) return;

			m_client.replaceFA( EClientSocket.GROUPS, dlg.groupsXML );
			m_client.replaceFA( EClientSocket.PROFILES, dlg.profilesXML );
			m_client.replaceFA( EClientSocket.ALIASES, dlg.aliasesXML );
			}
		}
	void displayXML( String title, String xml )
		{
		SBLog.write ( SBLog.NET, title, xml );
		}

	public void managedAccounts( String accountsList )
		{
		m_bIsFAAccount = true;
		m_FAAcctCodes = accountsList;
		String msg = EWrapperMsgGenerator.managedAccounts(accountsList);
		SBLog.write ( SBLog.NET, "managedAccounts", msg );
		}
~~~
