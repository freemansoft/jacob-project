package com.jacob.test.safearray;

import com.jacob.com.*;
import com.jacob.activeX.*;

public class SafeArrayViaExcel
{
 public static void main(java.lang.String[] args) 
 {
	 //deprecated
	 //System.runFinalizersOnExit(true);

	ActiveXComponent xl = new ActiveXComponent("Excel.Application");
	try {
		Dispatch cell;
		SafeArray sAProdText;
		Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
		System.out.println("have workbooks");
		Dispatch workbook = Dispatch.call(workbooks, "Open", "d:\\jacob\\samples\\test\\ExcelSafeArray" +
				".xls").toDispatch();
		System.out.println("Opened File - jacobtest.xls\n");
		Dispatch sheet = Dispatch.get(workbook,"ActiveSheet").toDispatch();
		cell = Dispatch.invoke(sheet,"Range",Dispatch.Get,new Object[] {"A1:D1000"},new int[1]).toDispatch();
		System.out.println("have cell:"+cell);
		sAProdText = Dispatch.get(cell,"Value").toSafeArray();
		System.out.println("sa: dim="+sAProdText.getNumDim());
		System.out.println("sa: start row="+sAProdText.getLBound(1));
		System.out.println("sa: start col="+sAProdText.getLBound(2));
		System.out.println("sa: end row="+sAProdText.getUBound(1));
		System.out.println("sa: end col="+sAProdText.getUBound(2));
		int i;
		int lineNumber=1;
		int n = 0;
		for(lineNumber=1; lineNumber < 1000; lineNumber++)
		{
			for (i = 1 ; i < 4 ; i++ ) {
				System.out.println((n++) + " " + lineNumber+" "+i+" " +sAProdText.getString(lineNumber,i));
				/*
				if (sAProdText.getString(lineNumber,i).compareTo("aaaa") != 0 ) {
					System.out.println("Invalid String in line " + lineNumber + " Cell " + i + " Value = " + sAProdText.getString(lineNumber,i));
					stringFound = false;
				}
			}
			if (stringFound) {
				System.out.println("Valid Strings in line " + lineNumber);
				lineNumber++;
			}
			*/
			}
		}
		
		Dispatch.call(workbook, "Close");
		System.out.println("Closed File\n");
	} catch (Exception e) {
	  e.printStackTrace();
	} finally {
	  xl.invoke("Quit", new Variant[] {});
	}
 }
}
