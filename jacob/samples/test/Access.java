package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

class Access 
{
    /**
     * the main loop for the test
     * @param args
     * @throws Exception
     */
  public static void main(String[] args) throws Exception
  {
    ComThread.InitSTA();
    ActiveXComponent ax = new ActiveXComponent("DAO.PrivateDBEngine");
		// this only works for access files pre-access-2000
    Dispatch db = open(ax, ".\\sample2.mdb");
    String sql = "select * from MainTable";
    // make a temporary querydef
    Dispatch qd = db.call("CreateQueryDef","").toDispatch();
    // set the SQL string on it
    qd.setProperty("SQL", sql);
    Variant result = getByQueryDef(qd);
		// the 2-d safearray is transposed from what you might expect
    System.out.println(result.toSafeArray());
    close(db);
		ComThread.Release();
  }

  /**
   * Open a database 
   * @param ax
   * @param fileName
   * @return dispatch object that was opened
   */
  public static Dispatch open(ActiveXComponent ax, String fileName)
  {
    Variant f = new Variant(false);
    // open the file in read-only mode
    Variant[] args = new Variant[] {new Variant(fileName), f, f};
    Dispatch openDB = ax.invoke("OpenDatabase", args).toDispatch();
    return openDB;
  }

  /**
   * Close a database
   * @param openDB db to be closed
   */
  public static void close(Dispatch openDB)
  {
    openDB.call("Close");
  }

  /**
   * Extract the values from the recordset
   * @param recset
   * @return Variant that is the returned values
   */
  public static Variant getValues(Dispatch recset)
  {
    DispatchNative.callSub(recset,"moveFirst");
    Variant vi = new Variant(4096);
    Variant v = recset.call("GetRows", vi);
    return v;
  }

  /**
   * should return ?? for the passed in ??
   * @param qd
   * @return Variant results of query?
   */
  public static Variant getByQueryDef(Dispatch qd)
  {
    // get a reference to the recordset
    Dispatch recset = qd.callGetDispatch("OpenRecordset");
    // get the values as a safe array
    String[] cols = getColumns(recset);
    for(int i=0;i<cols.length;i++)
    {
      System.out.print(cols[i]+" ");
    }
    System.out.println("");
    Variant vals = getValues(recset);
    return vals;
  }

  /**
   * gets the columns form the rec set
   * @param recset
   * @return list of column names
   */
  public static String[] getColumns(Dispatch recset)
  {
    Dispatch flds = recset.getPropertyAsDispatch("Fields");
    int n_flds = flds.getPropertyAsInt("Count");
    String[] s = new String[n_flds];
    Variant vi = new Variant();
    for (int i=0;i<n_flds;i++) {
      vi.putInt(i);
      // must use the invoke method because this is a method call
      // that wants to have a Dispatch.Get flag...
      Dispatch fld = DispatchNative.invoke(recset, "Fields",
                       DispatchConstants.Get, new Object[] {vi}, new int[1]).toDispatch();
      s[i] = fld.getPropertyAsString("Name");
    }
    return s;
  }
}
