package com.jacob.samples.system;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;

/**
 * Sample program that shows how to talk to WMI on local machine.
 * 
 * This test program was derived from SourceForge question
 * http://sourceforge.net/forum/forum.php?thread_id=1831650&forum_id=375946
 * fold, spindled and mutilated by clay_shooter
 * 
 * @author chris_knowles
 * 
 */
public class SystemMonitor {

	/**
	 * example run loop method called by main()
	 */
	public void runMonitor() {

		ActiveXComponent wmi = null;
		wmi = new ActiveXComponent("WbemScripting.SWbemLocator");
		// no connection parameters means to connect to the local machine
		Variant conRet = wmi.invoke("ConnectServer");
		// the author liked the ActiveXComponent api style over the Dispatch
		// style
		ActiveXComponent wmiconnect = new ActiveXComponent(conRet.toDispatch());

		// the WMI supports a query language.
		String query = "select CategoryString, Message, TimeGenerated, User, Type "
				+ "from Win32_NtLogEvent "
				+ "where Logfile = 'Application' and TimeGenerated > '20070915000000.000000-***'";
		Variant vCollection = wmiconnect
				.invoke("ExecQuery", new Variant(query));

		EnumVariant enumVariant = new EnumVariant(vCollection.toDispatch());

		String resultString = "";
		Dispatch item = null;

		while (enumVariant.hasMoreElements()) {
			resultString = "";
			item = enumVariant.nextElement().toDispatch();
			String categoryString = Dispatch.call(item, "CategoryString")
					.toString();
			String messageString = Dispatch.call(item, "Message").toString();
			String timeGenerated = Dispatch.call(item, "TimeGenerated")
					.toString();
			String eventUser = Dispatch.call(item, "User").toString();
			String eventType = Dispatch.call(item, "Type").toString();
			resultString += "TimeGenerated: " + timeGenerated + " Category: "
					+ categoryString + " User: " + eventUser + " EventType: "
					+ eventType + " Message:" + messageString;
			System.out.println(resultString);

		}

	}

	/**
	 * sample's main program
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		SystemMonitor utilConnection = new SystemMonitor();
		utilConnection.runMonitor();
	}

}
