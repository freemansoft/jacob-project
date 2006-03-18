
package com.jacob.test.vbscript;

import com.jacob.com.Variant;

/**
 * Extracted from ScriptTest so everyone can see this
 */
public class ScriptTestErrEvents {

    public void Error(Variant[] args)
    {
      System.out.println("java callback for error!");
    }
    public void Timeout(Variant[] args)
    {
      System.out.println("java callback for error!");
    }
}
