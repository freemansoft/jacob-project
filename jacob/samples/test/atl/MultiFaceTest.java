package samples.test.atl;

import com.jacob.com.*;
import com.jacob.activeX.*;

class MultiFaceTest 
{
  public static void main(String[] args)
  {
    System.runFinalizersOnExit(true);

    ActiveXComponent mf = new ActiveXComponent("MultiFace.Face");
    try {
      // I am now dealing with the default interface (IFace1)
      mf.setProperty("Face1Name", "Hello Face1");
      System.out.println(mf.getPropertyAsString("Face1Name"));

      // get to IFace2 through the IID
      Dispatch f2 = mf.QueryInterface("{9BF24410-B2E0-11D4-A695-00104BFF3241}");
      // I am now dealing with IFace2
      f2.setProperty("Face2Nam", "Hello Face2");
      System.out.println(f2.getPropertyAsString("Face2Nam"));

      // get to IFace3 through the IID
      Dispatch f3 = mf.QueryInterface("{9BF24411-B2E0-11D4-A695-00104BFF3241}");
      // I am now dealing with IFace3
      f3.setProperty("Face3Name", "Hello Face3");
      System.out.println(f3.getPropertyAsString("Face3Name"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
