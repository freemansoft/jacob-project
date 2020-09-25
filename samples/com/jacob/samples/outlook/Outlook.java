package com.jacob.samples.outlook;

/**
 * JACOB Outlook sample contributed by
 * Christopher Brind <christopher.brind@morse.com> 
 */

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * sample class to show simple outlook manipulation
 */
public class Outlook {

	private static String pad(int i) {
		StringBuffer sb = new StringBuffer();

		while (sb.length() < i) {
			sb.append(' ');
		}

		return sb.toString();
	}

	private static void recurseFolders(int iIndent, Dispatch o) {

		if (o == null) {
			return;
		}
		Dispatch oFolders = Dispatch.get(o, "Folders").toDispatch();
		// System.out.println("oFolders=" + oFolders);
		if (oFolders == null) {
			return;
		}

		Dispatch oFolder = Dispatch.get(oFolders, "GetFirst").toDispatch();
		do {
			Object oFolderName = Dispatch.get(oFolder, "Name");
			if (null == oFolderName) {
				break;
			}

			System.out.println(pad(iIndent) + oFolderName);
			recurseFolders(iIndent + 3, oFolder);

			oFolder = Dispatch.get(oFolders, "GetNext").toDispatch();
		} while (true);

	}

	/**
	 * standard run loop
	 * 
	 * @param asArgs
	 *            command line arguments
	 * @throws Exception
	 */
	public static void main(String asArgs[]) throws Exception {
		System.out.println("Outlook: IN");

		ActiveXComponent axOutlook = new ActiveXComponent("Outlook.Application");
		try {
			System.out.println("version=" + axOutlook.getProperty("Version"));

			Dispatch oOutlook = axOutlook.getObject();
			System.out.println("version=" + Dispatch.get(oOutlook, "Version"));

			Dispatch oNameSpace = axOutlook.getProperty("Session").toDispatch();
			System.out.println("oNameSpace=" + oNameSpace);

			recurseFolders(0, oNameSpace);

		} finally {
			axOutlook.invoke("Quit", new Variant[] {});
		}
	}

}
