package com.jacob.samples.JavaWebStart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.jacob.com.LibraryLoader;

/**
 * It is sometimes necessary to run Jacob without being able to install the dll
 * on the client machine. This is true in JavaWebStart (JWS) and possibly Applet
 * (assuming security allows access to the file system). The obvious thing to do
 * here is to jar up the Jacob.dll so that it can be downloaded the client along
 * with the rest of the resources. This is simple except that the System.Load()
 * function does not search jar files for DLLs. It searches the classpath. The
 * work around to this problem is to write the DLL to a temporary file and then
 * explicitly load the DLL calling passing the full path to the temporary file.
 * 
 * The following code demonstrates this idea.
 * 
 * @author joe
 * 
 */
public class DLLFromJARClassLoader {

	/**
	 * Load the DLL from the classpath rather than from the java path. This code
	 * uses this class's class loader to find the dell in one of the jar files
	 * in this class's class path. It then writes the file as a temp file and
	 * calls Load() on the temp file. The temporary file is marked to be deleted
	 * on exit so the dll is deleted from the system when the application exits.
	 * <p>
	 * Derived from ample code found in Sun's java forums <p.
	 * 
	 * @return true if the native library has loaded, false if there was a
	 *         problem.
	 */
	public boolean loadLibrary() {
		try {
			// this assumes that the dll is in the root dir of the signed
			// jws jar file for this application.
			//
			// Starting in 1.14M6, the dll is named by platform and architecture
			// so the best thing to do is to ask the LibraryLoader what name we
			// expect.
			// this code might be different if you customize the name of
			// the jacob dll to match some custom naming convention
			InputStream inputStream = getClass().getResource(
					"/" + LibraryLoader.getPreferredDLLName() + ".dll")
					.openStream();
			// Put the DLL somewhere we can find it with a name Jacob expects
			File temporaryDll = File.createTempFile(LibraryLoader
					.getPreferredDLLName(), ".dll");
			FileOutputStream outputStream = new FileOutputStream(temporaryDll);
			byte[] array = new byte[8192];
			for (int i = inputStream.read(array); i != -1; i = inputStream
					.read(array)) {
				outputStream.write(array, 0, i);
			}
			outputStream.close();
			temporaryDll.deleteOnExit();
			// Ask LibraryLoader to load the dll for us based on the path we
			// set
			System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll
					.getPath());
			LibraryLoader.loadJacobLibrary();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

}
