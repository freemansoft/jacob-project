package com.jacob.samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

class speed
{
  public static void main(String args[])
  {
    String lang = "VBScript";
    ActiveXComponent sC = new ActiveXComponent("ScriptControl");
    Dispatch sControl = sC.getObject();
    Dispatch.put(sControl, "Language", lang);
    for(int i=0;i<10000;i++) {
      Dispatch.call(sControl, "Eval", "1+1");
    }
  }
}
