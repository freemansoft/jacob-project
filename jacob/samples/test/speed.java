package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

class speed
{
    /**
     * test script for speed in a loop.
     * This may not be valid depending on compiler or optimizer behavior
     * @param args
     */
  public static void main(String args[])
  {
    String lang = "VBScript";
    ActiveXComponent sC = new ActiveXComponent("ScriptControl");
    Dispatch sControl = sC.getObject();
    sControl.setProperty("Language", lang);
    for(int i=0;i<10000;i++) {
      sControl.call("Eval", "1+1");
    }
  }
}
