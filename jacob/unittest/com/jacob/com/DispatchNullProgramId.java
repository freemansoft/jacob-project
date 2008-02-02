package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * This test verifies that the Dispatch object protects itself when the
 * constructor is called with a null program id. Prior to this protection, the
 * VM might crash.m
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class DispatchNullProgramId extends BaseTestCase {

	/**
	 * Verify that dispatch constructors are protected from null program ids.
	 */
	public void testNullProgramId() {
		try {
			String nullParam = null;
			new Dispatch(nullParam);
			fail("the dispatch failed to protect itself from null program ids");
		} catch (IllegalArgumentException iae) {
			System.out
					.println("the dispatch protected itself from null program ids");
		}
		try {
			String nullParam = "";
			new Dispatch(nullParam);
			fail("the dispatch failed to protect itself from empty string program ids");
		} catch (IllegalArgumentException iae) {
			System.out
					.println("the dispatch protected itself from empty string program ids");
		}
	}
}
