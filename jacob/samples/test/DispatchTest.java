package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

class DispatchTest 
{
    /**
     * main to test Dispatch
     * @param args
     */
  public static void main(String[] args)
  {
    ComThread.InitSTA();

    ActiveXComponent xl = new ActiveXComponent("Excel.Application");
    Dispatch xlo = xl.getObject();
    try {
      System.out.println("version="+ xl.getPropertyAsString("Version"));
      System.out.println("version="+xlo.getPropertyAsString("Version"));
      xlo.setProperty("Visible", true);
      Dispatch workbooks = xl.getPropertyAsDispatch("Workbooks");
      Dispatch workbook = workbooks.getPropertyAsDispatch("Add");
      Dispatch sheet = workbook.getPropertyAsDispatch("ActiveSheet");
      Dispatch a1 = DispatchNative.invoke(sheet, "Range", DispatchConstants.Get,
                                  new Object[] {"A1"},
                                  new int[1]).toDispatch();
      Dispatch a2 = DispatchNative.invoke(sheet, "Range", DispatchConstants.Get,
                                  new Object[] {"A2"},
                                  new int[1]).toDispatch();
      a1.setProperty("Value", "123.456");
      a2.setProperty("Formula", "=A1*2");
      System.out.println("a1 from excel:"+a1.getPropertyAsString("Value"));
      System.out.println("a2 from excel:"+a2.getPropertyAsString("Value"));
      workbook.call("Close", false);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      xl.invoke("Quit", new Variant[] {});
      ComThread.Release();
    }
  }
}
