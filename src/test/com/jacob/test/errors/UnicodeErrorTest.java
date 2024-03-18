package com.jacob.test.errors;

import com.jacob.test.BaseTestCase;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;

/**
 * This test verifies patch SF 1794811 . It shows how unicode filenames throw
 * exceptions in 1.13M4 and earlier.
 * 
 * @author justme84
 * 
 */
public class UnicodeErrorTest extends BaseTestCase {

	/**
	 * verifies that messages can now have unicode in them like when the file
	 * names have unicode characters
	 */
	public void testUnicodeCharactersInErrorMessage() {
		ActiveXComponent application = new ActiveXComponent("Word.Application");
		ActiveXComponent documents = application
				.getPropertyAsComponent("Documents");
		String fileName = "abc\u0411\u0412\u0413\u0414def";
		try {
			documents.invoke("Open", fileName);
			fail("Should have thrown an exception");
		} catch (ComException e) {
			assertTrue("Error message should contain file name with unicode "
					+ "characters in it. " + e.getMessage(), e.getMessage()
					.indexOf(fileName) > 0);
		}
	}
}