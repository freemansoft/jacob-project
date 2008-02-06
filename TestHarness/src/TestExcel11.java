/*
  Copyright (C) 2007  Robert Searle  
  
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

import junit.framework.TestCase;

import com.company.jacob.Excel.Application;
import com.company.jacob.Excel.JACOB_Application;
import com.company.jacob.Excel.jacobimpl.Range;
import com.company.jacob.Excel.Workbook;
import com.company.jacob.Excel.jacobimpl.Workbooks;
import com.company.jacob.Excel.Worksheet;
import com.company.jacob.Excel.jacobimpl.Sheets;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class TestExcel11  extends TestCase {
	private ActiveXComponent activeXcomponent;
	private Application excelApp;
	private Workbook workbook;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activeXcomponent = new ActiveXComponent("Excel.Application");
		excelApp = new Application();
		excelApp.setDispatch(activeXcomponent);
	}

	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (workbook != null){
			workbook.Close(new Variant(false), null, null);
			workbook = null;
		}
		excelApp.Quit();
		activeXcomponent.safeRelease();
	}

	
	public void testLoad() {
		excelApp.setVisible(true);
		assertEquals("11.0", excelApp.getVersion());
	}
	
	public void testWorkbook() throws Exception {
		testLoad();
		final Workbooks workbooks = new Workbooks();
		workbooks.setDispatch((Dispatch) excelApp.getWorkbooks());
		workbook = workbooks.Add(Variant.VT_MISSING);
	}
	
	public void testWorksheet() throws Exception {
		testWorkbook();
		final Worksheet sheet = new Worksheet();
		final Variant activeSheet = new Variant( workbook.getActiveSheet());
		sheet.setDispatch(activeSheet.toDispatch());
		final Range range_A1 = (Range) sheet.getRange(new Variant("a1"),new Variant("a1"));
		range_A1.setValue(new Variant("123.456"));
		final Range range_A2 = (Range) sheet.getRange(new Variant("a2"),new Variant("a2"));
		range_A2.setFormula(new Variant("=A1*2"));
		assertEquals(123.456, range_A1.getValue().getDouble());
		assertEquals(246.912,  range_A2.getValue().getDouble());
	}
}
