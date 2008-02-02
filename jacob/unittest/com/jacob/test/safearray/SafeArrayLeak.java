package com.jacob.test.safearray;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.JacobReleaseInfo;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test program demonstrates a weak in the setString(int[],String) method
 * in SafeArray. To see the leak:
 * <ul>
 * <li>Bring up the windows task manager and click on the performance tab.
 * <li>Run the test program
 * </ul>
 * You should see the Page File Usage History graph rise at te end of every
 * cycle. Running the same program with setString(r,c,String) does not show the
 * same symptoms
 */
public class SafeArrayLeak extends BaseTestCase {

	/**
	 * ----------------------------------------------------------------------------------------------------------------------------
	 * 
	 * ----------------------------------------------------------------------------------------------------------------------------
	 */
	public void testLeakWithSetString() {

		ActiveXComponent xl = null;
		Dispatch workbooks = null;
		Dispatch workbook = null;
		Dispatch workSheets = null;
		Dispatch sheet = null;
		Dispatch tabCells = null;
		SafeArray sa = null;

		// -Dcom.jacob.autogc=true
		System.out.println("Jacob version: " + JacobReleaseInfo.getBuildVersion());

		for (int t = 0; t < 10; t++) {
			// look at a large range of cells
			String position = "A7:DM8934";

			try {
				xl = new ActiveXComponent("Excel.Application");
				System.out
						.println("Excel version=" + xl.getProperty("Version"));

				xl.setProperty("Visible", new Variant(false));
				workbooks = xl.getProperty("Workbooks").toDispatch();

				workbook = Dispatch.get(workbooks, "Add").toDispatch();

				workSheets = Dispatch.get(workbook, "Worksheets").toDispatch();

				sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
				// grab the whole range specified above.
				tabCells = Dispatch.invoke(sheet, "Range", Dispatch.Get,
						new Object[] { position }, new int[1]).toDispatch();

				sa = Dispatch.get(tabCells, "Value").toSafeArray(true);

				System.out.println("Ub0=" + sa.getUBound(1)); // nbCol
				System.out.println("Ub1=" + sa.getUBound(2)); // nbLgn

				// number of rows
				int nbLgn = sa.getUBound(2);
				// number of columns
				int nbCol = sa.getUBound(1);

				int[] colLgn = new int[] { 0, 0 };

				// now set a value on every cell in the range we retrieved
				for (int i = 1; i <= nbLgn; i++) {
					colLgn[1] = i;

					for (int j = 1; j <= nbCol; j++) {
						colLgn[0] = j;
						// this one works with out a leak 1.13-M3
						// sa.setString(j, i, "test");
						// This one leaks with 1.13-M3 and earlier
						sa.setString(colLgn, "test");
					}
				}

				Dispatch.put(tabCells, "Value", sa);

				Variant f = new Variant(false);
				Dispatch.call(workbook, "Close", f);
				System.out.println("Close");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				if (sa != null) {
					try {
						sa.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						sa = null;
					}
				}

				if (tabCells != null) {
					try {
						tabCells.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						tabCells = null;
					}
				}

				if (sheet != null) {
					try {
						sheet.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						sheet = null;
					}
				}

				if (workSheets != null) {
					try {
						workSheets.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						workSheets = null;
					}
				}

				if (workbook != null) {
					try {
						workbook.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						workbook = null;
					}
				}

				if (workbooks != null) {
					try {
						workbooks.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						workbooks = null;
					}
				}

				if (xl != null) {
					try {
						xl.invoke("Quit", new Variant[] {});
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						xl.safeRelease();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						xl = null;
					}
				}

				ComThread.Release();
			}
		}
	}
}
