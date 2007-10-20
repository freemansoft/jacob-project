package com.jacob.com;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * An interface to the version.properties file.
 * This code was removed from JacobObject because it doesn't belong there.
 *
 */
public class JacobReleaseInfo {

	/**
	 * holds the build version as retrieved from the version.properties file
	 * that exists in the JAR. This can be retrieved by calling the static
	 * method getBuildVersion()
	 * 
	 * @see #getBuildVersion()
	 */
	private static String buildVersion = "";
	/**
	 * holds the build date as retrieved from the version.properties file that
	 * exists in the JAR This can be retrieved by calling the static method
	 * getBuildDate()
	 * 
	 * @see #getBuildDate()
	 */
	private static String buildDate = "";

	/**
	 * Loads version information from version.properties that was built as part
	 * of this release.
	 * 
	 */
	private static void loadVersionProperties() {
		Properties versionProps = new Properties();
		// can't use system class loader cause won't work in jws
		InputStream stream = JacobObject.class.getClassLoader()
				.getResourceAsStream("version.properties");
		try {
			versionProps.load(stream);
			stream.close();
			buildVersion = (String) versionProps.get("version");
			buildDate = (String) versionProps.get("build.date");
		} catch (IOException ioe) {
			System.out.println("blah, couldn't load props");
		}
	}

	/**
	 * loads version.properties and returns the value of version in it
	 * 
	 * @return String value of version in version.properties or "" if none
	 */
	public static String getBuildDate() {
		if (buildDate.equals("")) {
			loadVersionProperties();
		}
		return buildDate;
	}

	/**
	 * loads version.properties and returns the value of version in it
	 * 
	 * @return String value of version in version.properties or "" if none
	 */
	public static String getBuildVersion() {
		if (buildVersion.equals("")) {
			loadVersionProperties();
		}
		return buildVersion;
	}

}
