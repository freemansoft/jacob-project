package com.jacob.test;

import java.net.URL;

import junit.framework.TestCase;

import com.jacob.com.JacobObject;

/**
 * This base test class may require that the unittest package be
 * 'jacob-project/unittest' be on the classpath to find some resources.
 * 
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options. Or try these:
 * 
 * <pre>
 *      -Djava.library.path=d:/jacob/release/x86 
 *      -Dcom.jacob.autogc=false 
 *      -Dcom.jacob.debug=false 
 *      -Xcheck:jni
 * </pre>
 */
public class BaseTestCase extends TestCase {

	@SuppressWarnings("unused")
	protected void setUp() {
		// verify we have run with the dll in the lib path
		try {
			JacobObject foo = new JacobObject();
			// this is impossible in theory
			if (foo == null) {
				fail("Failed basic sanity test: Can't create JacobObject (-D<java.library.path=xxx>)");
			}
		} catch (UnsatisfiedLinkError ule) {
			fail("Did you remember to run with the jacob.dll in the libpath ?");
		}
	}

	/**
	 * this test exists just to test the setup.
	 */
	public void testSetup() {
		JacobObject foo = new JacobObject();
		assertNotNull(foo);
	}

	/**
	 * 
	 * @return a simple VB script that generates the result "3"
	 */
	public String getSampleVPScriptForEval() {
		return "1+(2*4)-3";

	}

	/**
	 * Converts the class name into a path and appends the resource name. Used
	 * to derive the path to a resource in the file system where the resource is
	 * co-located with the referenced class.
	 * 
	 * @param resourceName
	 * @param classInSamePackageAsResource
	 * @return a class loader compatible fully qualified file system path to a
	 *         resource
	 */
	@SuppressWarnings("unchecked")
	private String getJavaFilePathToPackageResource(String resourceName,
			Class classInSamePackageAsResource) {

		String classPackageName = classInSamePackageAsResource.getName();
		int i = classPackageName.lastIndexOf('.');
		if (i == -1) {
			classPackageName = "";
		} else {
			classPackageName = classPackageName.substring(0, i);
		}

		// change all "." to ^ for later conversion to "/" so we can append
		// resource names with "."
		classPackageName = classPackageName.replace('.', '^');
		System.out.println("classPackageName: " + classPackageName);
		String fullPathToResource;
		if (classPackageName.length() > 0) {
			fullPathToResource = classPackageName + "^" + resourceName;
		} else {
			fullPathToResource = resourceName;
		}

		fullPathToResource = fullPathToResource.replace('^', '/');
		System.out.println("fullPathToResource: " + fullPathToResource);

		URL urlToLibrary = classInSamePackageAsResource.getClassLoader()
				.getResource(fullPathToResource);
		assertNotNull("URL to resource " + resourceName
				+ " should not be null."
				+ " You probably need to add 'unittest' to the"
				+ " classpath so the tests can find resources", urlToLibrary);
		String fullPathToResourceAsFile = urlToLibrary.getFile();
		System.out.println("url to library: " + urlToLibrary);
		System.out.println("fullPathToResourceAsFile: "
				+ fullPathToResourceAsFile);

		return fullPathToResourceAsFile;
	}

	/**
	 * Converts the class name into a path and appends the resource name. Used
	 * to derive the path to a resource in the file system where the resource is
	 * co-located with the referenced class.
	 * 
	 * @param resourceName
	 * @param classInSamePackageAsResource
	 * @return returns the path in the file system of the requested resource in
	 *         windows c compatible format
	 */
	@SuppressWarnings("unchecked")
	public String getWindowsFilePathToPackageResource(String resourceName,
			Class classInSamePackageAsResource) {
		String javaFilePath = getJavaFilePathToPackageResource(resourceName,
				classInSamePackageAsResource);
		javaFilePath = javaFilePath.replace('/', '\\');
		return javaFilePath.substring(1);
	}

	/**
	 * 
	 * @param resourceName
	 * @param classInSamePackageAsResource
	 * @return a resource located in the same package as the passed in class
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	private Object getPackageResource(String resourceName,
			Class classInSamePackageAsResource) {
		String fullPathToResource = getJavaFilePathToPackageResource(
				resourceName, classInSamePackageAsResource);
		ClassLoader localClassLoader = classInSamePackageAsResource
				.getClassLoader();
		if (null == localClassLoader) {
			return ClassLoader.getSystemResource(fullPathToResource);
		} else {
			return localClassLoader.getResource(fullPathToResource);
		}
	}

	/**
	 * load a library from same place in the file system that the class was
	 * loaded from.
	 * <p>
	 * This is an attempt to let unit tests run without having to run regsvr32.
	 * 
	 * @param libraryName
	 * @param classInSamePackageAsResource
	 */
	@SuppressWarnings( { "unchecked", "unused" })
	private void loadLibraryFromClassPackage(String libraryName,
			Class classInSamePackageAsResource) {
		String libraryNameWithSuffix = "";
		String fullLibraryNameWithPath = "";
		if (libraryName != null && libraryName.endsWith("dll")) {
			libraryNameWithSuffix = libraryName;
		} else if (libraryName != null) {
			libraryNameWithSuffix = libraryName + ".dll";
		} else {
			fail("can't create full library name " + libraryName);
		}
		// generate the path the classloader would use to find this on the
		// classpath
		fullLibraryNameWithPath = getJavaFilePathToPackageResource(
				libraryNameWithSuffix, classInSamePackageAsResource);
		System.load(fullLibraryNameWithPath);
		// requires that the dll be on the library path
		// System.loadLibrary(fullLibraryNameWithPath);
	}

}
