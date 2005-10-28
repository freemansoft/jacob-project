package com.jacob.com;

/**
 * This test verifies that the Dispatch object protects itself when
 * the constructor is called with a null program id.
 * Prior to this protection, the VM might crash.m
 * @author joe
 *
 */
public class DispatchNullProgramId {

	public static void main(String[] args) {
		try {
			String nullParam = null;
			new Dispatch(nullParam);
			System.out.println(
					"the dispatch failed to protect itself from null program ids");
		} catch (IllegalArgumentException iae){
			System.out.println(
					"the dispatch protected itself from null program ids");
		}
		try {
			String nullParam = "";
			new Dispatch(nullParam);
			System.out.println(
			"the dispatch failed to protect itself from empty string program ids");
		} catch (IllegalArgumentException iae){
			System.out.println(
					"the dispatch protected itself from empty string program ids");
		}
	}
}
