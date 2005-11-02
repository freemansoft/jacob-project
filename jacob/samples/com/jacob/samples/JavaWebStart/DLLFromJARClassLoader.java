package com.jacob.samples.JavaWebStart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * It is sometimes necessary to run Jacob without being able to install the dll
 * on the client machine.  This is true in JavaWebStart (JWS) and possibly 
 * Applet (assuming security allows access to the file system).
 * The obvious thing to do here is to jar up 
 * the Jacob.dll so that it can be downloaded the client along with the rest
 * of the resources.  This is simple except that the System.Load() function
 * does not search jar files for DLLs.  It searches the classpath.
 * The work around to this problem is to write the DLL to a temporary file and then
 * explicitly load the DLL calling passing the full path to the temporary file.
 * 
 * The following code demonstrates this idea.
 * 
 * @author joe
 *
 */
public class DLLFromJARClassLoader {

    /**
     * Load the DLL from the classpath rather than from the java path.
     * This code uses this class's class loader to find the dell in one
     * of the jar files in this class's class path.  It then
     * writes the file as a temp file and calls Load() on the temp file.
     * The temporary file is marked to be deleted on exit so the dll
     * is deleted from the system when the application exits.
     * <p>
     * Derived from ample code found in Sun's java forums
     * <p.
     * @return true if the native library has loaded, false if there was a problem.
     */
    public boolean loadLibrary()
    {
        try
        {
          //Finds a stream to the dll. Change path/class if necessary
          InputStream inputStream = getClass().getResource("/jacob.dll").openStream();
          //Change name if necessary
          File temporaryDll = File.createTempFile("jacob", ".dll");
          FileOutputStream outputStream = new FileOutputStream(temporaryDll);
          byte[] array = new byte[8192];
          for (int i = inputStream.read(array);
               i != -1;
               i = inputStream.read(array)) {
            outputStream.write(array, 0, i);
          }
          outputStream.close();
          temporaryDll.deleteOnExit();
          System.load(temporaryDll.getPath());
          return true;
        }
        catch(Throwable e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
	
}
