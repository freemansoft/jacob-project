package samples.test;

/**
 * JACOB Outlook sample contributed by
 * Christopher Brind <christopher.brind@morse.com> 
 */

import com.jacob.com.*;
import com.jacob.activeX.*;


public class Outlook {

    private static String pad(int i) {
        StringBuffer sb = new StringBuffer();

        while(sb.length() < i) {
            sb.append(' ');
        }

        return sb.toString();
    }


    private static void recurseFolders(int iIndent, Dispatch o) {

				if (o == null) return;
        Dispatch oFolders = o.getPropertyAsDispatch("Folders");
        // System.out.println("oFolders=" + oFolders);
				if (oFolders == null) return;

        Dispatch oFolder = oFolders.getPropertyAsDispatch("GetFirst");
        do {
            String oFolderName = oFolder.getPropertyAsString("Name");
            if (null == oFolderName) {
                break;
            }

            System.out.println(pad(iIndent) + oFolderName);
            recurseFolders(iIndent + 3, oFolder);

            oFolder = oFolders.getPropertyAsDispatch("GetNext");
        } while(true);

    }


    public static void main(String asArgs[]) throws Exception {
        System.out.println("Outlook: IN");

        ActiveXComponent axOutlook = new ActiveXComponent("Outlook.Application");
        try {
            System.out.println("version="+axOutlook.getProperty("Version"));

            Dispatch oOutlook = axOutlook.getObject();
            System.out.println("version="+oOutlook.getPropertyAsString("Version"));

            Dispatch oNameSpace = axOutlook.getProperty("Session").toDispatch();
            System.out.println("oNameSpace=" + oNameSpace);

            recurseFolders(0, oNameSpace);

        } finally {
            axOutlook.invoke("Quit", new Variant[] {});
        }
    }

}

