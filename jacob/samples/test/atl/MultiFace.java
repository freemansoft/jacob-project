import com.jacob.com.*;
import com.jacob.activeX.*;

class MultiFace 
{
  public static void main(String[] args)
  {
    System.runFinalizersOnExit(true);

    ActiveXComponent mf = new ActiveXComponent("MultiFace.Face");
    try {
      // I am now dealing with the default interface (IFace1)
      Dispatch.put(mf, "Face1Name", new Variant("Hello Face1"));
      System.out.println(Dispatch.get(mf, "Face1Name"));

      // get to IFace2 through the IID
      Dispatch f2 = mf.QueryInterface("{9BF24410-B2E0-11D4-A695-00104BFF3241}");
      // I am now dealing with IFace2
      Dispatch.put(f2, "Face2Nam", new Variant("Hello Face2"));
      System.out.println(Dispatch.get(f2, "Face2Nam"));

      // get to IFace3 through the IID
      Dispatch f3 = mf.QueryInterface("{9BF24411-B2E0-11D4-A695-00104BFF3241}");
      // I am now dealing with IFace3
      Dispatch.put(f3, "Face3Name", new Variant("Hello Face3"));
      System.out.println(Dispatch.get(f3, "Face3Name"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
