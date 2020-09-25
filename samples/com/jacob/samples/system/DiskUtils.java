package com.jacob.samples.system;

import java.text.DecimalFormat;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Example VB script that grabs hard drive properties.
 * <p>
 * Source Forge posting
 * http://sourceforge.net/forum/forum.php?thread_id=1785936&forum_id=375946
 * <p>
 * Enhance by clay_shooter with info from
 * http://msdn2.microsoft.com/en-us/library/d6dw7aeh.aspx
 * 
 * @author qstephenson
 * 
 */
public class DiskUtils {

	/** formatters aren't thread safe but the sample only has one thread */
	private static DecimalFormat sizeFormatter = new DecimalFormat(
			"###,###,###,###");

	/** a pointer to the scripting file system object */
	private ActiveXComponent fileSystemApp = null;

	/** the dispatch that points at the drive this DiskUtil operates against */
	private Dispatch myDrive = null;

	/**
	 * Standard constructor
	 * 
	 * @param drive
	 *            the drive to run the test against.
	 */
	public DiskUtils(String drive) {
		setUp(drive);
	}

	/**
	 * open the connection to the scripting object
	 * 
	 * @param drive
	 *            the drive to run the test against
	 */
	public void setUp(String drive) {
		if (fileSystemApp == null) {
			ComThread.InitSTA();
			fileSystemApp = new ActiveXComponent("Scripting.FileSystemObject");
			myDrive = Dispatch.call(fileSystemApp, "GetDrive", drive)
					.toDispatch();
		}
	}

	/**
	 * Do any needed cleanup
	 */
	public void tearDown() {
		ComThread.Release();
	}

	/**
	 * convenience method
	 * 
	 * @return driver serial number
	 */
	public int getSerialNumber() {
		return Dispatch.get(myDrive, "SerialNumber").getInt();
	}

	/**
	 * Convenience method. We go through these formatting hoops so we can make
	 * the size string pretty. We wouldn't have to do that if we didn't mind
	 * long strings with Exxx at the end or the fact that the value returned can
	 * vary in size based on the size of the disk.
	 * 
	 * @return driver total size of the disk
	 */
	public String getTotalSize() {
		Variant returnValue = Dispatch.get(myDrive, "TotalSize");
		if (returnValue.getvt() == Variant.VariantDouble) {
			return sizeFormatter.format(returnValue.getDouble());
		} else if (returnValue.getvt() == Variant.VariantInt) {
			return sizeFormatter.format(returnValue.getInt());
		} else {
			return "Don't know type: " + returnValue.getvt();
		}
	}

	/**
	 * Convenience method. We wouldn't have to do that if we didn't mind long
	 * strings with Exxx at the end or the fact that the value returned can vary
	 * in size based on the size of the disk.
	 * 
	 * @return driver free size of the disk
	 */
	public String getFreeSpace() {
		Variant returnValue = Dispatch.get(myDrive, "FreeSpace");
		if (returnValue.getvt() == Variant.VariantDouble) {
			return sizeFormatter.format(returnValue.getDouble());
		} else if (returnValue.getvt() == Variant.VariantInt) {
			return sizeFormatter.format(returnValue.getInt());
		} else {
			return "Don't know type: " + returnValue.getvt();
		}
	}

	/**
	 * 
	 * @return file system on the drive
	 */
	public String getFileSystemType() {
		// figure ot the actual variant type
		// Variant returnValue = Dispatch.get(myDrive, "FileSystem");
		// System.out.println(returnValue.getvt());
		return Dispatch.get(myDrive, "FileSystem").getString();
	}

	/**
	 * 
	 * @return volume name
	 */
	public String getVolumeName() {
		return Dispatch.get(myDrive, "VolumeName").getString();
	}

	/**
	 * Simple main program that creates a DiskUtils object and queries for the
	 * C: drive
	 * 
	 * @param args
	 *            standard command line arguments
	 */
	public static void main(String[] args) {
		// DiskUtils utilConnection = new DiskUtils("F");
		DiskUtils utilConnection = new DiskUtils("C");
		System.out.println("Disk serial number is: "
				+ utilConnection.getSerialNumber());
		System.out.println("FileSystem is: "
				+ utilConnection.getFileSystemType());
		System.out.println("Volume Name is: " + utilConnection.getVolumeName());
		System.out.println("Disk total size is: "
				+ utilConnection.getTotalSize());
		System.out.println("Disk free space is: "
				+ utilConnection.getFreeSpace());
		utilConnection.tearDown();
	}
}
